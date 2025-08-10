package com.atnjupt.sqyxgo.product.api;

import com.atnjupt.sqyxgo.common.result.Result;
import com.atnjupt.sqyxgo.model.product.Category;
import com.atnjupt.sqyxgo.model.product.SkuInfo;
import com.atnjupt.sqyxgo.product.service.CategoryService;
import com.atnjupt.sqyxgo.product.service.SkuInfoService;
import com.atnjupt.sqyxgo.vo.product.SkuInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * ClassName:ProductInnerController
 * Package: com.atnjupt.sqyxgo.product.api
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/20 11:54
 * @Version 1.0
 */
@Api(tags = "商品中用于远程调用的接口测试")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/product")
public class ProductInnerController {

    private final CategoryService categoryService;
    private final SkuInfoService skuInfoService;

    //es:根据categoryId获取分类的信息
    @ApiOperation("es:根据categoryId获取分类的信息")
    @GetMapping("inner/getCategory/{categoryId}")
    public Category getCategory(@PathVariable(value = "categoryId") Long categoryId){
        return categoryService.getCategoryToElasticseacrch(categoryId);
    }
    //es:根据skuid获取sku信息
    @ApiOperation("es:根据skuid获取sku信息")
    @GetMapping("inner/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable(value = "skuId") Long skuId){
        return skuInfoService.getSkuInfoByIdToElasticsearch(skuId);
    }

    //activity：通过skuIdList获得skuInfo集合
    @ApiOperation("activity：通过skuIdList获得skuInfo集合")
    @PostMapping("inner/findSkuInfoList")
    public List<SkuInfo> findSkuInfoList(@RequestBody List<Long> skuInfoList){
        List<SkuInfo> skuInfoList1 = skuInfoService.listByIds(skuInfoList);
        if (skuInfoList1 == null || skuInfoList1.isEmpty()){
            return new ArrayList<>();
        }
        return skuInfoList1;
    }

    //activity：通过关键字获得skuInfo集合
    @ApiOperation("activity：通过关键字获得skuInfo集合")
    @GetMapping("inner/findSkuInfoByKeyword/{keyword}")
    public List<SkuInfo> findSkuInfoByKeyword(@PathVariable(value = "keyword") String keyword){
        List<SkuInfo> skuInfoList = skuInfoService.findSkuInfoByKeyword(keyword);
        return skuInfoList;
    }

    //给一个categoryIdList，返回一个categoryList集合
    @ApiOperation("给一个skuIdList，返回一个SkuInfoList集合")
    @PostMapping("inner/findCategoryListByCategoryIdList")
    public List<Category> findCategoryListByCategoryIdList(@RequestBody List<Long> categoryIdList){
        List<Category> categoryList = categoryService.findCategoryListByCategoryIdList(categoryIdList);
        if (categoryList == null || categoryList.isEmpty()) {
            return new ArrayList<>();
        }
        return categoryList;
    }

    //获取新人专享
    @GetMapping("inner/findNewPersonSkuInfoList")
    public List<SkuInfo> findNewPersonSkuInfoList(){
        List<SkuInfo> newPersonSkuInfoList = skuInfoService.findNewPersonSkuInfoList();
        return newPersonSkuInfoList;
    }
    //获取分类信息
    @GetMapping("inner/findAllCategoryList")
    public List<Category> findAllCategoryList(){
        return categoryService.findAllList();
    }

    // 通过skuId 查询skuInfoVo
    @GetMapping("inner/getSkuInfoVo/{skuId}")
    public SkuInfoVo getSkuInfoVo(@PathVariable(value = "skuId") Long skuId){
        SkuInfoVo skuInfoVo = skuInfoService.getSkuInfoVo(skuId);
        return skuInfoVo;
    }




}
