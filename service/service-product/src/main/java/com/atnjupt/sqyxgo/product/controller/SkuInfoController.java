package com.atnjupt.sqyxgo.product.controller;


import com.atnjupt.sqyxgo.common.result.Result;
import com.atnjupt.sqyxgo.model.product.SkuInfo;
import com.atnjupt.sqyxgo.product.service.SkuInfoService;
import com.atnjupt.sqyxgo.vo.product.SkuInfoQueryVo;
import com.atnjupt.sqyxgo.vo.product.SkuInfoVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zaxxer.hikari.util.IsolationLevel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * sku信息 前端控制器
 * </p>
 *
 * @author atnjupt
 * @since 2025-07-18
 */
@RestController
@RequestMapping("/admin/product/skuInfo")
@RequiredArgsConstructor
@Api(tags = "商品信息管理接口")
public class SkuInfoController {

    private final SkuInfoService skuInfoService;

    //分页查询商品信息
    @ApiOperation("分页查询商品信息")
    @GetMapping("{page}/{limit}")
    public Result get(@PathVariable(value = "page") Long page,
                      @PathVariable(value = "limit") Long limit,
                      @ModelAttribute SkuInfoQueryVo skuInfoQueryVo){
        Page<SkuInfo> page1 = new Page<>(page,limit);
        IPage<SkuInfo> page2 = skuInfoService.getSkuInfoPage(page1,skuInfoQueryVo);
        return Result.ok(page2);
    }
    //通过id查询商品信息
    @ApiOperation("通过id查询商品信息")
    @GetMapping("get/{id}")
    public Result getById(@PathVariable(value = "id") Long id){
        SkuInfoVo byId = skuInfoService.getSkuInfoById(id);
        return Result.ok(byId);
    }
    //添加商品信息
    @ApiOperation("添加商品信息")
    @PostMapping("save")
    public Result save(@RequestBody SkuInfoVo skuInfoVo){
        boolean is_success = skuInfoService.saveSkuInfo(skuInfoVo);
        if (is_success) {
            return Result.ok("success");
        }
        return Result.fail("fail");
    }
    //更新商品信息
    @ApiOperation("更新商品信息")
    @PutMapping("update")
    public Result update(@RequestBody SkuInfoVo skuInfoVo){

        boolean is_success = skuInfoService.updateSkuInfo(skuInfoVo);
        if (is_success) {
            return Result.ok("success");
        }
        return Result.fail("fail");
    }
    //通过id删除商品信息,这里简写了代码，只删除了sku_info表，平台属性、海报、图片没有删除
    @ApiOperation("通过id删除商品信息")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable(value = "id") Long id){
        boolean is_success = skuInfoService.removeById(id);
        if (is_success) {
            return Result.ok("success");
        }
        return Result.fail("fail");
    }
    //批量删除商品信息,这里简写了代码，只删除了sku_info表，平台属性、海报、图片没有删除
    @ApiOperation("批量删除商品信息")
    @GetMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList){
        boolean is_success = skuInfoService.removeByIds(idList);
        if (is_success) {
            return Result.ok("success");
        }
        return Result.fail("fail");
    }
    //商品上下架
    @ApiOperation("商品上下架")
    @GetMapping("publish/{id}/{status}")
    public Result publish(@PathVariable(value = "id") Long id,
                          @PathVariable(value = "status") Integer status){
        skuInfoService.publishStatus(id,status);
        return Result.ok(null);
    }
    //商品审核
    @ApiOperation("商品审核")
    @GetMapping("check/{id}/{status}")
    public Result check(@PathVariable(value = "id") Long id,
                        @PathVariable(value = "status") Integer status){
        skuInfoService.checkStatus(id,status);
        return Result.ok(null);
    }

    //是否是新人专享
    @ApiOperation("新人专享")
    @GetMapping("isNewPerson/{id}/{status}")
    public Result isNewPerson(@PathVariable(value = "id") Long id,
                              @PathVariable(value = "status") Integer status){
        skuInfoService.isNewPerson(id,status);
        return Result.ok(null);
    }

}

