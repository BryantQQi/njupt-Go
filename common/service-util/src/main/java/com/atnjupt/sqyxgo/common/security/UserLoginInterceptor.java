package com.atnjupt.sqyxgo.common.security;

import com.atnjupt.sqyxgo.common.constant.RedisConst;
import com.atnjupt.sqyxgo.common.utils.helper.JwtHelper;
import com.atnjupt.sqyxgo.vo.user.UserLoginVo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ClassName:UserLoginInterceptor
 * Package: com.atnjupt.sqyxgo.common.security
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/24 16:30
 * @Version 1.0
 */
public class UserLoginInterceptor implements HandlerInterceptor {


    private final RedisTemplate redisTemplate;

    public UserLoginInterceptor(RedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        this.getUserLoginVo(request);
        return true;
    }

    private void getUserLoginVo(HttpServletRequest request) {
        //获取Token
        String token = request.getHeader("token");

        //判断Token不为空
        if (token == null) {
            return;
        }
        //从Token获取userId
        Long userId = JwtHelper.getUserId(token);
        //根据userId获取用户信息
        UserLoginVo userLoginVo = (UserLoginVo)redisTemplate.opsForValue().get(RedisConst.USER_LOGIN_KEY_PREFIX + userId);
        //把用户信息封装到ThreadLocal
        if (userLoginVo != null) {
            AuthContextHolder.setUserIdThreadLocal(userLoginVo.getUserId());
            AuthContextHolder.setUserLoginVoThreadLocal(userLoginVo);
            AuthContextHolder.setWareIdThreadLocal(userLoginVo.getWareId());
        }


    }


}
