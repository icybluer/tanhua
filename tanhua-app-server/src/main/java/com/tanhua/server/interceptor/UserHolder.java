package com.tanhua.server.interceptor;

import com.tanhua.model.domain.pojo.User;

/**
 * @author Administrator
 */
public class UserHolder {

    private static ThreadLocal<User> tl = new ThreadLocal<>();

    /**
     * 保存数据到线程
     */
    public static void set(User user) {
        tl.set(user);
    }

    /**
     * 获取线程中的用户信息
     */
    public static User get() {
        return tl.get();
    }

    /**
     * 从当前线程，获取用户对象的id
     */
    public static Long getUserId() {
        if (tl.get() == null) {
            return null;
        }
        return tl.get().getId();
    }

    /**
     * 从当前线程，获取用户对象的手机号码
     */
    public static String getMobile() {
        if (tl.get() == null) {
            return null;
        }
        return tl.get().getMobile();
    }

     /**
     * 移除线程中数据
     */
    public static void remove() {
        tl.remove();
    }
}