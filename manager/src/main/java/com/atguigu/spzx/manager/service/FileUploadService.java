package com.atguigu.spzx.manager.service;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {
    String upload(MultipartFile file);

}
