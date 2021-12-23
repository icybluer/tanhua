package com.itheima.test;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.tanhua.autoconfig.template.OssTemplate;
import com.tanhua.server.AppServerApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppServerApplication.class)
public class OssTest {
    @Test
    public void ossTest() throws FileNotFoundException {
        // 要上传的文件路径
        String path = "";
        FileInputStream is = new FileInputStream(new File(path));

        // yourEndpoint填写Bucket所在地域对应的Endpoint。以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com。
        String endpoint = "";
        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
        String accessKeyId = "";
        String accessKeySecret = "";
        //
        String bucketName = "";
        // 填写文件名。文件名包含路径，不包含Bucket名称。例如exampledir/exampleobject.txt。
        String objectName = new SimpleDateFormat("yyyy/MM/dd").format(new Date())
                + "/" + UUID.randomUUID() + path.substring(path.lastIndexOf("."));
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        ossClient.putObject(bucketName, objectName, is);

        // 关闭OSSClient。
        ossClient.shutdown();
    }

    @Autowired
    private OssTemplate ossTemplate;

    @Test
    public void ossTemplateTest() throws FileNotFoundException {
        String filename = "D:\\System\\Pictures\\image.jpg";
        FileInputStream is = new FileInputStream(new File(filename));
        String url = ossTemplate.upload(filename, is);
        System.out.println(url);
    }
}
