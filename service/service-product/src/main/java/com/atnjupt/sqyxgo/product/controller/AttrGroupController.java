package com.atnjupt.sqyxgo.product.controller;


import com.atnjupt.sqyxgo.common.result.Result;
import com.atnjupt.sqyxgo.model.product.AttrGroup;
import com.atnjupt.sqyxgo.product.service.AttrGroupService;
import com.atnjupt.sqyxgo.vo.product.AttrGroupQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 属性分组 前端控制器
 * </p>
 *
 * @author atnjupt
 * @since 2025-07-18
 */
@RestController
@RequestMapping("/admin/product/attrGroup")
@RequiredArgsConstructor
@Api(tags = "商品属性分组接口")
public class AttrGroupController {

    private final AttrGroupService attrGroupService;


    //条件分页查询商品属性分组信息
    @ApiOperation("条件分页查询商品属性分组信息")
    @GetMapping("{page}/{limit}")
    public Result get(@PathVariable(value = "page") Long page,
                      @PathVariable(value = "limit") Long limit,
                      @ModelAttribute AttrGroupQueryVo attrGroupQueryVo){
        Page<AttrGroup> page1 = new Page<>(page,limit);
        IPage<AttrGroup> page2 = attrGroupService.getAttrGroupPage(page1,attrGroupQueryVo);
        return Result.ok(page2);
    }

    //通过id查询商品属性分组信息
    @ApiOperation("通过id查询商品属性分组信息")
    @GetMapping("get/{id}")
    public Result get(@PathVariable(value = "id") Long id){
        AttrGroup byId = attrGroupService.getById(id);
        return Result.ok(byId);
    }
    //添加商品属性分组信息
    @ApiOperation("添加商品属性分组信息")
    @PostMapping("save")
    public Result save(@RequestBody AttrGroup attrGroup){
        boolean is_success = attrGroupService.save(attrGroup);
        if(is_success){
            return Result.ok("success");
        }else
        return Result.fail("fail");
    }
    //更新商品属性分组信息
    @ApiOperation("更新商品属性分组信息")
    @PutMapping("update")
    public Result update(@RequestBody AttrGroup attrGroup){
        boolean is_success = attrGroupService.updateById(attrGroup);
        if(is_success){
            return Result.ok("success");
        }else
            return Result.fail("fail");
    }
    //通过id删除商品属性分组信息
    @ApiOperation("通过id删除商品属性分组信息")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable(value = "id") Long id){
        boolean is_success = attrGroupService.removeById(id);
        if(is_success){
            return Result.ok("success");
        }else
            return Result.fail("fail");
    }
    //批量删除商品属性分组信息
    @ApiOperation("批量删除商品属性分组信息")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList){
        boolean is_success = attrGroupService.removeByIds(idList);
        if(is_success){
            return Result.ok("success");
        }else
            return Result.fail("fail");
    }
    //查询所有商品属性分组信息
    @ApiOperation("查询所有商品属性分组信息")
    @GetMapping("findAllList")
    public Result findAllList(){
        List<AttrGroup> list = attrGroupService.list(null);
        return Result.ok(list);
    }



}

