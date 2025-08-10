package com.atnjupt.sqyxgo.client.search;

import com.atnjupt.sqyxgo.model.search.SkuEs;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * ClassName:SearchFeignClient
 * Package: com.atnjupt.sqyxgo.client.search
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/28 16:27
 * @Version 1.0
 */
@FeignClient(value = "service-search")
public interface SearchFeignClient {

    //获取热销商品
    @GetMapping("/api/search/sku/inner/findHotSkuList")
    List<SkuEs> findHotSkuList();
    //更新商品incrHotScore
    @GetMapping("/api/search/sku/inner/incrHotScore/{skuId}")
    void incrHotScore(@PathVariable(value = "skuId") Long skuId);
}
