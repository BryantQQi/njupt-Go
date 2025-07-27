package com.atnjupt.sqyxgo.product.controller;

import com.atnjupt.sqyxgo.common.result.Result;
import com.atnjupt.sqyxgo.model.product.Attr;
import com.atnjupt.sqyxgo.product.service.AttrService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * ClassName:AttrController
 * Package: com.atnjupt.sqyxgo.product.controller
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/18 15:58
 * @Version 1.0
 */
@RestController
@RequestMapping("/admin/product/attr")
@RequiredArgsConstructor
@Api(tags = "属性管理接口")
public class AttrController {

    private final AttrService attrService;

    //根据平台属性分组id查询商品属性信息
    @ApiOperation("根据平台属性分组id查询商品属性信息")
    @GetMapping("{groupId}")
    public Result get(@PathVariable(value = "groupId") @NotNull Long groupId){
        List<Attr> list = attrService.getByGroupId(groupId);
        return Result.ok(list);
    }
    //通过属性id查询商品属性信息
    @ApiOperation("通过属性id查询商品属性信息")
    @GetMapping("get/{id}")
    public Result getById(@PathVariable(value = "id") Long id){
        Attr byId = attrService.getById(id);
        return Result.ok(byId);
    }
    //添加商品属性信息
    @ApiOperation("添加商品属性信息")
    @PostMapping("save")
    public Result save(@RequestBody Attr attr){
        boolean is_success = attrService.save(attr);
        if(is_success){
            return Result.ok("success");
        }else
        return Result.fail("fail");
    }
    //更新商品属性信息
    @ApiOperation("更新商品属性信息")
    @PutMapping("update")
    public Result update(@RequestBody Attr attr){
        boolean is_success = attrService.updateById(attr);
        if(is_success){
            return Result.ok("success");
        }else
            return Result.fail("fail");
    }
    //通过id删除商品属性信息
    @ApiOperation("通过id删除商品属性信息")
    @DeleteMapping("remove/{id}")
    public Result delete(@PathVariable(value = "id") Long id){
        boolean is_success = attrService.removeById(id);
        if(is_success){
            return Result.ok("success");
        }else
            return Result.fail("fail");
    }
    //批量删除商品属性信息
    @ApiOperation("批量删除商品属性信息")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList){
        boolean is_success = attrService.removeByIds(idList);
        if(is_success){
            return Result.ok("success");
        }else
            return Result.fail("fail");
    }



}