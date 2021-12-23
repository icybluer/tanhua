package com.itheima.test;

import com.tanhua.commons.utils.HttpUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试短信
 */
public class SmsTest {
    public static void main(String[] args) {
        String host = "https://gyytz.market.alicloudapi.com";
        String path = "/sms/smsSend";
        String method = "POST";
        //自己的AppCode
        String appcode = "";
        Map<String, String> headers = new HashMap<>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<>();
        querys.put("mobile", ""); //接收短信的号码
        querys.put("param", "**code**:12345,**minute**:5"); //参数: 验证码 失效时间
        querys.put("smsSignId", ""); //标签
        querys.put("templateId", ""); //模板
        Map<String, String> bodys = new HashMap<>();


        try {
            /*
              重要提示如下:
              HttpUtils请从
              https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
              下载

              相应的依赖请参照
              https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
             */
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            System.out.println(response.toString());
            //获取response的body
            System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}