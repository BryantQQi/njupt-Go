package com.atnjupt.sqyxgo.home.service;

import java.util.Map;

/**
 * ClassName:ItemService
 * Package: com.atnjupt.sqyxgo.home.service
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/29 21:09
 * @Version 1.0
 */
public interface ItemService {
    //通过skuid获取sku详细信息
    Map<String, Object> item(Long userId, Long skuId);
}
