package com.tanhua.autoconfig.template;

import com.tanhua.autoconfig.properties.SmsProperties;
import com.tanhua.commons.utils.HttpUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.util.HashMap;
import java.util.Map;

public class SmsTemplate {

    private SmsProperties properties;

    public SmsTemplate(SmsProperties properties) {
        this.properties = properties;
    }

    public void sendSms(String mobile, String code) throws Exception {
        String host = "https://gyytz.market.alicloudapi.com";
        String path = "/sms/smsSend";
        String method = "POST";
        Map<String, String> headers = new HashMap<>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + properties.getAppCode()); //自己的AppCode
        Map<String, String> querys = new HashMap<>();
        querys.put("mobile", mobile); //接收短信的号码
        querys.put("param", "**code**:" + code + ",**minute**:" + properties.getMinute()); //参数: 验证码 失效时间
        querys.put("smsSignId", properties.getSmsSignId()); //标签
        querys.put("templateId", properties.getTemplateId()); //模板
        Map<String, String> bodys = new HashMap<>();

        HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
        System.out.println(response.toString());
        //获取response的body
        //System.out.println(EntityUtils.toString(response.getEntity()));

    }

}
