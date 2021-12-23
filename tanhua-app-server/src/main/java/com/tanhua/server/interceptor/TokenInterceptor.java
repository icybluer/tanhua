package com.tanhua.server.interceptor;


import com.tanhua.commons.utils.JwtUtils;
import com.tanhua.model.domain.pojo.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Administrator
 */
public class TokenInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //1、获取请求头
        String token = request.getHeader("Authorization");

        //2、使用工具类，判断token是否有效
        boolean verifyToken = JwtUtils.verifyToken(token);
        //3、如果token失效，返回状态码401，拦截
        if (!verifyToken) {
            response.setStatus(401);
            return false;
        }
        //4、如果token正常可用，放行
        //解析token，获取id和手机号码，
        Claims claims = JwtUtils.getClaims(token);
        String mobile = (String) claims.get("mobile");
        Integer id = (Integer) claims.get("id");

        //构造User对象，存入Threadlocal
        User user = new User();
        user.setId(Long.valueOf(id));
        user.setMobile(mobile);

        UserHolder.set(user);

        return true;
    }

    //清空
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.remove();
    }
}