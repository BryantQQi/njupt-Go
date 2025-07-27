package com.atnjupt.sqyxgo.search.service;

import org.springframework.stereotype.Service;

/**
 * ClassName:SkuService
 * Package: com.atnjupt.sqyxgo.search.service
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/20 11:40
 * @Version 1.0
 */
@Service
public interface SkuService {
    //上架商品
    void upperSkuById(Long skuId);
    //下架商品
    void downSkuInfoById(Long skuId);
}
