package com.atnjupt.sqyxgo.product.controller;


import com.atnjupt.sqyxgo.common.result.Result;
import com.atnjupt.sqyxgo.model.product.Attr;
import com.atnjupt.sqyxgo.model.product.Category;
import com.atnjupt.sqyxgo.product.service.CategoryService;
import com.atnjupt.sqyxgo.vo.product.CategoryQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 商品三级分类 前端控制器
 * </p>
 *
 * @author atnjupt
 * @since 2025-07-18
 */
@RestController
@RequestMapping("/admin/product/category")
@RequiredArgsConstructor
@Api(tags = "商品分类管理接口")
public class CategoryController {

    private final CategoryService categoryService;
    //商品分类信息分页查询
    @ApiOperation("商品分类信息分页查询")
    @GetMapping("{page}/{limit}")
    public Result get(@PathVariable(value = "page") Long page,
                      @PathVariable(value = "limit") Long limit,
                      @ModelAttribute CategoryQueryVo categoryQueryVo){
        Page<Category> page1 = new Page<>(page,limit);
        IPage<Category> page2 = categoryService.getPageCategory(page1,categoryQueryVo);
        return Result.ok(page2);
    }
    //通过id查询商品分类信息
    @ApiOperation("通过id查询商品分类信息")
    @GetMapping("get/{id}")
    public Result getById(@PathVariable(value = "id") Long id){
        Category byId = categoryService.getById(id);
        return Result.ok(byId);
    }
    //添加商品分类信息
    @ApiOperation("添加商品分类信息")
    @PostMapping("save")
    public Result save(@RequestBody Category category){
        boolean is_success = categoryService.save(category);
        if(is_success){
            return Result.ok("success");
        }else {
            return Result.fail("fail");
        }

    }

    //更新商品分类信息
    @ApiOperation("更新商品分类信息")
    @PutMapping("udpate")
    public Result update(@RequestBody Category category){
        boolean is_success = categoryService.updateById(category);
        if(is_success){
            return Result.ok("success");
        }else {
            return Result.fail("fail");
        }
    }
    //通过id删除商品分类信息
    @ApiOperation("通过id删除商品分类信息")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable(value = "id") Long id){
        boolean is_success = categoryService.removeById(id);
        if(is_success){
            return Result.ok("success");
        }else {
            return Result.fail("fail");
        }
    }
    //批量删除商品分类信息
    @ApiOperation("批量删除商品分类信息")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList){
        boolean is_success = categoryService.removeByIds(idList);
        if(is_success){
            return Result.ok("success");
        }else {
            return Result.fail("fail");
        }
    }
    //查询所有商品分类信息
    @ApiOperation("查询所有商品分类信息")
    @GetMapping("findAllList")
    public Result findAllList(){
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        List<Category> list = categoryService.list(wrapper);
        return Result.ok(list);
    }

}

