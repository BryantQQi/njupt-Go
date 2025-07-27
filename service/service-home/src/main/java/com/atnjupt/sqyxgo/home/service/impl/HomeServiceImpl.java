package com.atnjupt.sqyxgo.home.service.impl;

import com.atnjupt.sqyxgo.home.service.HomeService;
import com.sun.xml.internal.bind.v2.TODO;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * ClassName:HomeServiceImpl
 * Package: com.atnjupt.sqyxgo.home.service.impl
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/25 17:34
 * @Version 1.0
 */
@Service
public class HomeServiceImpl implements HomeService {

    //获取首页数据
    @Override
    public Map<String, Object> getHomeData(Long userId) {
        //当前登录用户的提货点地址信息
        //远程调用service-product接口

        //新人专享商品
        //远程调用service-product接口

        //商品分类
        //远程调用service-product接口


        //首页秒杀数据
        //TODO


        //热销商品
        //远程调用service-product接口

        //封装到map返回

        return null;
    }
}
