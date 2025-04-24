package com.atguigu.spzx.manager.service.impl;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.atguigu.spzx.common.exception.GuiguException;
import com.atguigu.spzx.manager.mapper.SysRoleUserMapper;
import com.atguigu.spzx.manager.mapper.SysUserMapper;
import com.atguigu.spzx.manager.service.SysUserService;
import com.atguigu.spzx.model.dto.system.AssignRoleDto;
import com.atguigu.spzx.model.dto.system.LoginDto;
import com.atguigu.spzx.model.dto.system.SysUserDto;
import com.atguigu.spzx.model.entity.system.SysRoleCodeUserId;
import com.atguigu.spzx.model.entity.system.SysRoleUser;
import com.atguigu.spzx.model.entity.system.SysUser;
import com.atguigu.spzx.model.vo.common.ResultCodeEnum;
import com.atguigu.spzx.model.vo.system.LoginVo;
import com.atguigu.spzx.model.vo.system.SysUserVo;
import com.atguigu.spzx.utils.JwtTokenUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class SysUserServiceImpl implements SysUserService {

    @Resource
    private SysUserMapper sysUserMapper ;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private SysRoleUserMapper sysRoleUserMapper;
    @Resource
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public LoginVo login(LoginDto loginDto) {
        //TODO:因为redis数据是存在内存上的，一直点击验证码，redis会爆，所以这里应该做一个验证码防刷
        //1 获取输入的验证码和存储到redis的key名称
        String captcha = loginDto.getCaptcha();
        String key = loginDto.getCodeKey();
        //2 根据获取到的redis里面的key，查询redis里面存储的验证码
        String redisCode = stringRedisTemplate.opsForValue().get("user:validate" + key);
        //3 比较输入的验证码和redis存储的验证码是否一致
        if(StrUtil.isEmpty(captcha) || !StrUtil.equalsIgnoreCase(redisCode,captcha)){
            //4 如果不一致，提示用户校验失败
            throw new GuiguException(ResultCodeEnum.VALIDATECODE_ERROR);
        }
        //5 如果一致，删除redis里面的验证码
        stringRedisTemplate.delete("user:validate" + key);

        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",loginDto.getUserName());
        SysUser sysUser = sysUserMapper.selectOne(queryWrapper);
        if(sysUser==null){
//            throw new RuntimeException("用户名不存在！");
            throw new GuiguException(ResultCodeEnum.LOGIN_ERROR);
        }
        String input_password = DigestUtils.md5DigestAsHex(loginDto.getPassword().getBytes());
        if(!sysUser.getPassword().equals(input_password)){
//            throw new RuntimeException("密码不正确！");
            throw new GuiguException(ResultCodeEnum.LOGIN_ERROR);
        }
        //存有角色基本信息的token放进redis
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        stringRedisTemplate.opsForValue().set("user:login"+token, JSON.toJSONString(sysUser),1, TimeUnit.DAYS);
        //查询该角色所持有的角色
        SysRoleCodeUserId sysRoleCodeUserId = new SysRoleCodeUserId();
        sysRoleCodeUserId.setUserId(sysUser.getId());
        List<String> roleCodes = sysRoleUserMapper.selectRoleCodesByUserId(sysUser.getId());
        sysRoleCodeUserId.setRoleCodes(roleCodes);
        String jwt = jwtTokenUtil.generateToken(sysRoleCodeUserId);
        LoginVo loginVo = new LoginVo();
        loginVo.setToken(token);
        loginVo.setJwt(jwt);
        return loginVo;
    }

    @Override
    public void logout(String token) {
        stringRedisTemplate.delete("user:login" + token);
    }

    @Override
    public SysUserVo findByPage(int pageNum, int pageSize, SysUserDto sysUserDto) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        if(sysUserDto.getKeyword() != null && !"".equals(sysUserDto.getKeyword())){
            queryWrapper.like("username",sysUserDto.getKeyword());
        }
        if(sysUserDto.getCreateTimeBegin() != null && !"".equals(sysUserDto.getCreateTimeBegin())){
            queryWrapper.ge("create_time",sysUserDto.getCreateTimeBegin());
        }
        if(sysUserDto.getCreateTimeEnd() != null && !"".equals(sysUserDto.getCreateTimeEnd())){
            queryWrapper.le("create_time",sysUserDto.getCreateTimeEnd());
        }
        sysUserMapper.selectPage(page,queryWrapper);
        SysUserVo sysUserVo = new SysUserVo();
        sysUserVo.setSysUsers(page.getRecords());
        sysUserVo.setPageTotal(page.getTotal());
        return sysUserVo;
    }

    @Override
    public void saveSysUser(SysUser sysUser) {
        //1 判断用户名不能重复
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",sysUser.getUserName());
        SysUser sysUser1 = sysUserMapper.selectOne(queryWrapper);
        if(sysUser1!=null){
            throw new GuiguException(ResultCodeEnum.USER_NAME_IS_EXISTS);
        }
        //2 对密码进行加密
        String md5_password = DigestUtils.md5DigestAsHex(sysUser.getPassword().getBytes());
        sysUser.setPassword(md5_password);
        sysUserMapper.insert(sysUser);
    }

    @Override
    public void updateSysUser(SysUser sysUser) {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",sysUser.getUserName());
        SysUser sysUser1 = sysUserMapper.selectOne(queryWrapper);
        if(sysUser1!=null&&sysUser1.getId()!=sysUser.getId()){
            throw new GuiguException(ResultCodeEnum.USER_NAME_IS_EXISTS);
        }
        Date date = new Date();
        sysUser.setUpdateTime(date);
        sysUserMapper.updateById(sysUser);
    }

    @Override
    public void deleteById(int userId) {
        sysUserMapper.deleteById(userId);
    }

    @Override
    public void doAssign(AssignRoleDto assignRoleDto) {
        //1 根据userId删除用户之前分配的角色数据
        QueryWrapper<SysRoleUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",assignRoleDto.getUserId());
        sysRoleUserMapper.delete(queryWrapper);
        //2 重新分配新数据
        for(Long roleId:assignRoleDto.getRoleIdList()){
            SysRoleUser sysRoleUser = new SysRoleUser();
            sysRoleUser.setUserId(assignRoleDto.getUserId());
            sysRoleUser.setRoleId(roleId);
            sysRoleUser.setUpdateTime(new Date());
            sysRoleUserMapper.insert(sysRoleUser);
        }
    }
}