package com.atnjupt.sqyxgo.client.activity;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * ClassName:ActivityFeignClient
 * Package: com.atnjupt.sqyxgo.client.activity
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/22 9:42
 * @Version 1.0
 */
@FeignClient(value = "service-activity")
public interface ActivityFeignClient {

    //获取skuId对应的促销活动标签
    @PostMapping("/api/activity/inner/findActivity")
    Map<Long, List<String>> findActivity(@RequestBody List<Long> skuIdList);
    //sku对应的促销与优惠券信息
    @GetMapping("/api/activity/inner/findActivityAndCoupon/{skuId}/{userId}")
    Map<String, Object> findActivityAndCoupon(@PathVariable(value = "skuId") Long skuId,@PathVariable(value = "userId") Long userId);
}
