package com.atnjupt.sqyxgo.common.security;

import com.atnjupt.sqyxgo.vo.acl.AdminLoginVo;
import com.atnjupt.sqyxgo.vo.user.UserLoginVo;

/**
 * ClassName:AuthContextHolder
 * Package: com.atnjupt.sqyxgo.common.security
 * Description:线程上下文管理类
 *
 * @Author Monkey
 * @Create 2025/7/24 16:27
 * @Version 1.0
 */
public class AuthContextHolder {

    //会员用户id
    private static ThreadLocal<Long> userIdThreadLocal = new ThreadLocal<>();
    //仓库id
    private static ThreadLocal<Long> wareIdThreadLocal = new ThreadLocal<>();
    //会员基本信息
    private static ThreadLocal<UserLoginVo> userLoginVoThreadLocal = new ThreadLocal<>(); ;
    //后台管理用户id
    private static ThreadLocal<Long> adminIdThreadLocal = new ThreadLocal<>();
    //管理员基本信息
    private static ThreadLocal<AdminLoginVo> adminLoginVoThreadLocal = new ThreadLocal<>();

    public static Long getUserIdThreadLocal() {
        return userIdThreadLocal.get();//底层就是map集合
    }

    public static void setUserIdThreadLocal(Long _userIdThreadLocal) {
        userIdThreadLocal.set(_userIdThreadLocal);
    }

    public static Long getWareIdThreadLocal() {
        return wareIdThreadLocal.get();
    }

    public static void setWareIdThreadLocal(Long _wareIdThreadLocal) {
        wareIdThreadLocal.set(_wareIdThreadLocal);
    }

    public static UserLoginVo getUserLoginVoThreadLocal() {
        return userLoginVoThreadLocal.get();
    }

    public static void setUserLoginVoThreadLocal(UserLoginVo _userLoginVoThreadLocal) {
        userLoginVoThreadLocal.set(_userLoginVoThreadLocal);
    }

    public static Long getAdminIdThreadLocal() {
        return adminIdThreadLocal.get();
    }

    public static void setAdminIdThreadLocal(Long _adminIdThreadLocal) {
        adminIdThreadLocal.set(_adminIdThreadLocal);
    }

    public static AdminLoginVo getAdminLoginVoThreadLocal() {
        return adminLoginVoThreadLocal.get();
    }

    public static void setAdminLoginVoThreadLocal(AdminLoginVo _adminLoginVoThreadLocal) {
        adminLoginVoThreadLocal.set(_adminLoginVoThreadLocal);
    }
}
