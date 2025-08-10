package com.atnjupt.sqyxgo.home.service.impl;

import com.atnjupt.sqyxgo.client.activity.ActivityFeignClient;
import com.atnjupt.sqyxgo.client.product.ProductFeignClient;
import com.atnjupt.sqyxgo.client.search.SearchFeignClient;
import com.atnjupt.sqyxgo.home.service.ItemService;
import com.atnjupt.sqyxgo.home.utils.ThreadPoolConfig;
import com.atnjupt.sqyxgo.vo.product.SkuInfoVo;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * ClassName:ItemServiceImpl
 * Package: com.atnjupt.sqyxgo.home.service.impl
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/29 21:10
 * @Version 1.0
 */
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ProductFeignClient productFeignClient;
    private final ActivityFeignClient activityFeignClient;
    private final ThreadPoolExecutor homeThreadPoolExecutor;
    private final SearchFeignClient searchFeignClient;


    //通过skuid获取sku详细信息
    @Override
    public Map<String, Object> item(Long userId, Long skuId) {
        Map<String,Object> map = new HashMap<>();

        // 通过skuId 查询skuInfoVo, product模块
        CompletableFuture<SkuInfoVo> skuInfoCompletableFuture = CompletableFuture.supplyAsync(() -> {
            SkuInfoVo skuInfoVo = productFeignClient.getSkuInfoVo(skuId);
            map.put("skuInfoVo",skuInfoVo);
            return skuInfoVo;
        },homeThreadPoolExecutor);

        //TODO 如果商品是秒杀商品，获取秒杀信息

        //sku对应的促销与优惠券信息, 调用activity模块
        CompletableFuture<Void> activityCompletableFuture = CompletableFuture.runAsync(() -> {
            //sku对应的促销与优惠券信息
            Map<String, Object> activityAndCouponMap = activityFeignClient.findActivityAndCoupon(skuId, userId);
            map.putAll(activityAndCouponMap);
        },homeThreadPoolExecutor);
        CompletableFuture<Void> hotCompletableFuture = CompletableFuture.runAsync(() -> {
            searchFeignClient.incrHotScore(skuId);
        },homeThreadPoolExecutor);
        // 使用join等待所有线程执行完，在执行后面的逻辑
        CompletableFuture.allOf(skuInfoCompletableFuture,activityCompletableFuture,hotCompletableFuture).join();
        return map;
    }
}
