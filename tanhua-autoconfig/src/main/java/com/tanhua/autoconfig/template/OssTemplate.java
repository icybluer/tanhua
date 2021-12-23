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

        // yourEndpoint填写Bucket所在地域对应的Endpoint。以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com。

        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。

        // 填写Bucket名称，例如examplebucket。

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
