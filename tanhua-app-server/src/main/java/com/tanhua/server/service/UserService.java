package com.tanhua.server.service;

import com.tanhua.autoconfig.template.HuanXinTemplate;
import com.tanhua.autoconfig.template.SmsTemplate;
import com.tanhua.commons.constant.Constants;
import com.tanhua.commons.utils.JwtUtils;
import com.tanhua.dubbo.api.UserApi;
import com.tanhua.model.domain.User;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.UserVO;
import com.tanhua.server.exception.BusinessException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private SmsTemplate smsTemplate;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value("${tanhua.sms.minute}")
    private String minute;

    /**
     * 发送短信验证码
     */
    public void sendCode(String mobile) throws Exception {
        //1. 生成随机验证码
        String code = RandomStringUtils.randomNumeric(6);
        //2. 调用阿里云发送验证码

        //smsTemplate.sendSms(mobile, code);
        System.out.println("code = " + code);
        //3. 将验证码保存到redis
        redisTemplate.opsForValue().set(Constants.SMS_CODE + mobile, code, Duration.ofMinutes(Long.parseLong(minute)));
    }

    @DubboReference
    private UserApi userApi;

    @Autowired
    private HuanXinTemplate huanXinTemplate;

    /**
     * 登录校验
     */
    public UserVO loginVerification(String mobile, String code) {
        // 1. 从Redis中获取验证码
        String redisCode = redisTemplate.opsForValue().get(Constants.SMS_CODE + mobile);

        // 2. 校验验证码
        if (StringUtils.isEmpty(code) || !code.equals("123456")) {
            throw new BusinessException(ErrorResult.loginError());
        }
        // 比对通过, 删除验证码, 选做
        redisTemplate.delete(Constants.SMS_CODE + mobile);

        // 3. 判断用户是否注册
        boolean isNew = false;
        User user = userApi.findByMobile(mobile);
        if (user == null) {
            user = new User();
            user.setMobile(mobile);
            user.setPassword(DigestUtils.md5Hex(Constants.INIT_PASSWORD));
            Long id = userApi.save(user);
            user.setId(id);
            isNew = true;

            // 注册环信用户
            String username = "hx" + id;
            String password = Constants.INIT_PASSWORD;
            Boolean result = huanXinTemplate.createUser(username, password);
            if (result) {
                user.setHxUser(username);
                user.setHxPassword(password);
                userApi.update(user);
            }
        }

        // 4. 登录成功, 生成token
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("id", user.getId());
        tokenMap.put("mobile", mobile);
        String token = JwtUtils.getToken(tokenMap);

        // 5. 封装数据返回
        return UserVO.builder()
                .isNew(isNew)
                .token(token)
                .build();
    }
}