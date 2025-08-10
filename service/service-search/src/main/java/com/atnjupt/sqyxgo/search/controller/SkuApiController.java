package com.atnjupt.sqyxgo.search.controller;

import com.atnjupt.sqyxgo.common.result.Result;
import com.atnjupt.sqyxgo.model.search.SkuEs;
import com.atnjupt.sqyxgo.search.service.SkuService;
import com.atnjupt.sqyxgo.vo.search.SkuEsQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName:SkuApiController
 * Package: com.atnjupt.sqyxgo.search.controller
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/20 11:39
 * @Version 1.0
 */
@RestController
@RequestMapping("api/search/sku")
@RequiredArgsConstructor
@Api(tags = "es搜索接口测试")
public class SkuApiController {

    private final SkuService skuService;

    //搜索商品，通过分类查询的分类下的商品
    @ApiOperation("搜索商品")
    @GetMapping("{page}/{limit}")
    public Result search(@PathVariable(value = "page") Integer page,
                         @PathVariable(value = "limit") Integer limit,
                         SkuEsQueryVo skuEsQueryVo){
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<SkuEs> pageModel = skuService.search(pageable,skuEsQueryVo);

        return Result.ok(pageModel);
    }


    //上架商品，后门接口
    @GetMapping("inner/upperSku/{skuId}")
    @ApiOperation("上架商品")
    public Result upperGoods(@PathVariable(value = "skuId") Long skuId){
        skuService.upperSkuById(skuId);
        return Result.ok(null);
    }


    //下架商品
    @GetMapping("inner/downSku/{skuId}")
    @ApiOperation("下架商品")
    public Result downGoods(@PathVariable(value = "skuId") Long skuId){
        skuService.downSkuInfoById(skuId);
        return Result.ok(null);
    }
}
