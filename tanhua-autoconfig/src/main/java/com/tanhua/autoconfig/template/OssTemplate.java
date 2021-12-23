package com.tanhua.autoconfig.template;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.tanhua.autoconfig.properties.OssProperties;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class OssTemplate {
    private OssProperties properties;

    public OssTemplate(OssProperties properties) {
        this.properties = properties;
    }

    public String upload(String filename, InputStream is) {
        // 填写文件名。文件名包含路径，不包含Bucket名称。例如exampledir/exampleobject.txt。
        String objectName = new SimpleDateFormat("yyyy/MM/dd").format(new Date())
                + "/" + UUID.randomUUID() + filename.substring(filename.lastIndexOf("."));
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder()
                .build(properties.getEndpoint(), properties.getAccessKeyId(), properties.getAccessKeySecret());

        ossClient.putObject(properties.getBucketName(), objectName, is);

        // 关闭OSSClient。
        ossClient.shutdown();

        return properties.getUrl() + objectName;
    }
}
