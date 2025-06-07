package com.atguigu.spzx.product.properties;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix="spzx.minio")
public class MinioProperties {
    private String endpointUrl;
    private String accessKey;
    private String secretKey;
    private String bucketName;
}
