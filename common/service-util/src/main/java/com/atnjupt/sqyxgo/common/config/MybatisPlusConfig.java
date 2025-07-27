package com.atnjupt.sqyxgo.common.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * MybatisPlus配置类
 * @MapperScan("包的路径") 是mybatis提供的扫描接口的注解。自动扫描指定包路径下的所有 Mapper 接口，
 * 并为其生成实现类的 Spring Bean，加入到 Spring 容器中，使得你可以通过 @Autowired
 * 或 @Resource 等方式进行注入使用。
 * MyBatis 的 Mapper 是接口，没有实现类。这个注解会自动为你生成代理类，并注入 Spring 容器。
 * 替代了在每个接口上写@Mapper
 */
@EnableTransactionManagement
@Configuration
@MapperScan("com.atnjupt.sqyxgo.*.mapper")
public class MybatisPlusConfig {

    /**
     * mp插件
     */
    @Bean
    public MybatisPlusInterceptor optimisticLockerInnerInterceptor(){
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        //向Mybatis过滤器链中添加分页拦截器
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}