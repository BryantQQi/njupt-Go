package com.atnjupt.sqyxgo.home.service;

import java.util.Map;

/**
 * ClassName:HomeService
 * Package: com.atnjupt.sqyxgo.home.service
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/25 17:34
 * @Version 1.0
 */
public interface HomeService {
    //获取首页数据
    Map<String, Object> getHomeData(Long userId);
}
