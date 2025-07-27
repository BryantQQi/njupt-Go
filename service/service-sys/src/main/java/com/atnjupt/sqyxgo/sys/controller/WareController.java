package com.atnjupt.sqyxgo.sys.controller;

import com.atnjupt.sqyxgo.common.result.Result;
import com.atnjupt.sqyxgo.model.sys.Ware;
import com.atnjupt.sqyxgo.sys.service.RegionService;
import com.atnjupt.sqyxgo.sys.service.RegionWareService;
import com.atnjupt.sqyxgo.sys.service.WareService;
import com.atnjupt.sqyxgo.vo.product.WareQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.PushbackInputStream;
import java.util.HashMap;
import java.util.List;

/**
 * ClassName:WareController
 * Package: com.atnjupt.sqyxgo.sys.controller
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/17 18:10
 * @Version 1.0
 */
@Api(tags = "仓库管理接口")
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/sys/ware")
public class WareController {
    private final WareService wareService;

    //分页查询仓库
    @ApiOperation("分页查询仓库")
    @GetMapping("{page}/{limit}")
    public Result get(@PathVariable(value = "page") Long page, @PathVariable(value = "limit") Long limit,
                      @ModelAttribute WareQueryVo wareQueryVo){
        Page<Ware> page1 = new Page<>(page,limit);
        IPage<Ware> iPage = wareService.findPageWare(page1,wareQueryVo);

        return Result.ok(iPage);
    }

    //通过id查询仓库
    @ApiOperation("通过id查询仓库")
    @GetMapping("{id}")
    public Result getById(@PathVariable(value = "id") Long id){
        Ware ware = wareService.getWareById(id);
        return Result.ok(ware);
    }

    //添加仓库
    @ApiOperation("添加仓库")
    @PostMapping("save")
    public Result save(@RequestBody Ware ware){
        wareService.saveWare(ware);
        return Result.ok(null);
    }
    //更新仓库
    @ApiOperation("更新仓库")
    @PutMapping("update")
    public Result update(@RequestBody Ware ware){
        wareService.updateWare(ware);
        return Result.ok(null);
    }
    //通过id删除仓库
    @ApiOperation("通过id删除仓库")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable(value = "id") Long id){
        wareService.removeById(id);
        return Result.ok(null);
    }
    //批量删除仓库
    @ApiOperation("批量删除仓库")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList){
        wareService.removeByIds(idList);
        return Result.ok(null);
    }
    //查询所有仓库
    @ApiOperation("查询所有仓库")
    @GetMapping("findAllList")
    public Result findAllList(){
        List<Ware> list = wareService.findAllWareList();
        return Result.ok(list);
    }
}
