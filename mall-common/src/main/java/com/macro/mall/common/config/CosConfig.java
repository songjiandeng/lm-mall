package com.macro.mall.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 腾讯云上传参数
 * @author： dongao
 * @create: 2019/10/16
 */
@Component
@ConfigurationProperties(prefix = "cos")
public class CosConfig {

    private String secretId = "AKIDFMH1Xo0yiEVIsELDsJVSFpAGQbYGuLaa";

    private String secretKey = "qRozvd6OUBestgHfpmX6RjqmV1NWSBvU";

    private String region = "ap-guangzhou";

    private String bucketName = "lm-app-1320139518";

    private String projectName = "灵猫商城";

    private String common = "common";

    private String imageSize = "2";

    private String prefixDomain = "http://lm-app-1320139518.cos.ap-guangzhou.myqcloud.com/";

    private Long expiration = 60L;

    public String getSecretId() {
        return secretId;
    }

    public void setSecretId(String secretId) {
        this.secretId = secretId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getCommon() {
        return common;
    }

    public void setCommon(String common) {
        this.common = common;
    }

    public String getImageSize() {
        return imageSize;
    }

    public void setImageSize(String imageSize) {
        this.imageSize = imageSize;
    }

    public String getPrefixDomain() {
        return prefixDomain;
    }

    public void setPrefixDomain(String prefixDomain) {
        this.prefixDomain = prefixDomain;
    }

    public Long getExpiration() {
        return expiration;
    }

    public void setExpiration(Long expiration) {
        this.expiration = expiration;
    }
}
