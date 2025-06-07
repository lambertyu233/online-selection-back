package com.atguigu.spzx.manager.service.impl;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
import cn.hutool.core.date.DateUtil;
import com.atguigu.spzx.manager.properties.MinioProperties;
import com.atguigu.spzx.manager.service.FileUploadService;
import io.minio.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Autowired
    private MinioProperties minioProperties;

    @Override
    public String upload(MultipartFile file) {
        try {
            // 创建一个Minio的客户端对象
            MinioClient minioClient = MinioClient.builder()
                    .endpoint(minioProperties.getEndpointUrl())
                    .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                    .build();

            // 判断桶是否存在
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(minioProperties.getBucketName()).build());
            if (!found) {       // 如果不存在，那么此时就创建一个新的桶
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioProperties.getBucketName()).build());
            }

            // 设置存储对象名称
            // 1 让每个上传的文件名称是唯一的 uuid生成 01.jpg
            // 2 根据当前日期对上传文件进行分组
            String dateDir = DateUtil.format(new Date(), "yyyyMMdd");
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            //20230801/443e1e772bef482c95be28704bec58a901.jpg
            String fileName = dateDir+"/"+uuid+file.getOriginalFilename();
            System.out.println(fileName);

            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .object(fileName)
                    .build();
            // 文件上传
            minioClient.putObject(putObjectArgs) ;

//            return minioProperties.getEndpointUrl() + "/" + minioProperties.getBucketName() + "/" + fileName;
            return fileName ;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteFile(String fileNames) {
        try {
            // 创建一个Minio的客户端对象
            MinioClient minioClient = MinioClient.builder()
                    .endpoint(minioProperties.getEndpointUrl())
                    .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                    .build();
            //把要删除的链接变成minio需要的类
            List<DeleteObject> objects = Arrays.stream(fileNames.split(","))
                    .map(String::trim)  // 去除前后空格
                    .map(DeleteObject::new)
                    .toList();
            // 获取删除结果迭代器
            Iterable<Result<DeleteError>> results = minioClient.removeObjects(
                    RemoveObjectsArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .objects(objects)
                            .build());
            //如果需要堆结果进行处理，可以用这个results，这里暂不做处理，因为现在做的只是对上传出错的改善
        }catch (Exception e){
            throw new RuntimeException("批量删除初始化失败: " + e.getMessage(), e);
        }
    }
}
