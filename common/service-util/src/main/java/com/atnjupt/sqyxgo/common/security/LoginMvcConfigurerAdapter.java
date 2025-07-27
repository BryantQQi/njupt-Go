package com.atnjupt.sqyxgo.common.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import javax.annotation.Resource;

/**
 * ClassName:LoginMvcConfigurerAdapter
 * Package: com.atnjupt.sqyxgo.common.security
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/24 16:31
 * @Version 1.0
 */
@Configuration
@RequiredArgsConstructor
public class LoginMvcConfigurerAdapter extends WebMvcConfigurationSupport {

    private final RedisTemplate redisTemplate;

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(new UserLoginInterceptor(redisTemplate))
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/user/weixin/wxLogin/*");
        super.addInterceptors(registry);
    }


}