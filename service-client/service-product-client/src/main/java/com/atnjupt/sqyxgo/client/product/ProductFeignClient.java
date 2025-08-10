package com.atnjupt.sqyxgo.client.product;

import com.atnjupt.sqyxgo.model.product.Category;
import com.atnjupt.sqyxgo.model.product.SkuInfo;
import com.atnjupt.sqyxgo.vo.product.SkuInfoVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * ClassName:ProductFeignClient
 * Package: com.atnjupt.sqyxgo.client.product
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/20 12:17
 * @Version 1.0
 */
@FeignClient(value = "service-product")
public interface ProductFeignClient {

    @GetMapping("/api/product/inner/getCategory/{categoryId}")
    Category getCategory(@PathVariable(value = "categoryId") Long categoryId);

    @GetMapping("/api/product/inner/getSkuInfo/{skuId}")
    SkuInfo getSkuInfo(@PathVariable(value = "skuId") Long skuId);

    @PostMapping("/api/product/inner/findSkuInfoList")
    List<SkuInfo> findSkuInfoList(@RequestBody List<Long> skuInfoList);

    @GetMapping("/api/product/inner/findSkuInfoByKeyword/{keyword}")
    List<SkuInfo> findSkuInfoByKeyword(@PathVariable(value = "keyword") String keyword);

    @PostMapping("/api/product/inner/findCategoryListByCategoryIdList")
    List<Category> findCategoryListByCategoryIdList(@RequestBody List<Long> categoryIdList);

    //新人专享商品
    @GetMapping("/api/product/inner/indNewPersonSkuInfoList")
    List<SkuInfo> findNewPersonSkuInfoList();

    //获取分类信息
    @GetMapping("/api/product/inner/findAllCategoryList")
    List<Category> findAllCategoryList();

    // 通过skuId 查询skuInfoVo
    @GetMapping("/api/product/inner/getSkuInfoVo/{skuId}")
    SkuInfoVo getSkuInfoVo(@PathVariable(value = "skuId") Long skuId);
}
