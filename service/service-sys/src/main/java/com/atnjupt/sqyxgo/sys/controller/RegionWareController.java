package com.atnjupt.sqyxgo.sys.controller;

import com.atnjupt.sqyxgo.common.result.Result;
import com.atnjupt.sqyxgo.model.sys.RegionWare;
import com.atnjupt.sqyxgo.sys.service.RegionWareService;
import com.atnjupt.sqyxgo.vo.sys.RegionWareQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * ClassName:RegionWareController
 * Package: com.atnjupt.sqyxgo.sys.controller
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/17 18:21
 * @Version 1.0
 */
@RequiredArgsConstructor
@Api(tags = "区域仓库管理接口")
@RestController
@RequestMapping("/admin/sys/regionWare")
public class RegionWareController {

    private final RegionWareService regionWareService;
    //开通区域列表
    @ApiOperation("开通区域列表")
    @GetMapping("/{page}/{limit}")
    public Result list(@PathVariable(value = "page") Long page,@PathVariable(value = "limit") Long limit,
                       @ModelAttribute RegionWareQueryVo regionWareQueryVo){
        Page<RegionWare> pageParam = new Page<>(page,limit);
        IPage<RegionWare> pageModel = regionWareService.selectPage(pageParam,regionWareQueryVo);
        return Result.ok(pageModel);
    }



    //添加开通区域
    @ApiOperation("添加开通区域")
    @PostMapping("save")
    public Result save(@RequestBody RegionWare regionWare){
        regionWareService.saveRegionWare(regionWare);
        return Result.ok(null);
    }
    //通过区域id查询开通情况
    @ApiOperation("通过区域id查询开通情况")
    @GetMapping("get/{id}")
    public Result get(@PathVariable(value = "id") Long id){
        List<String> list = regionWareService.getRegionById(id);
        return Result.ok(list);
    }

    //通过id更新区域状态，表示该区域是否开通仓库
    @ApiOperation("通过id更新区域状态")
    @PostMapping("updateStatus/{id}/{status}")
    public Result updateStatus(@PathVariable(value = "id") Long id,@PathVariable(value = "status") Integer status){
        if (status >1 || status < 0){
            return Result.fail("输入的转态不合法，请输入0或1");
        }
        Boolean is_success = regionWareService.updateRegionStatus(id,status);
        if (is_success){
            return Result.ok("更新成功");
        }
        return Result.fail("更新失败");
    }

    //通过id删除该区域的仓库，删除全部
    @ApiOperation("通过id删除该区域")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable(value = "id") Long id){
        boolean is_success = regionWareService.removeById(id);
        if (is_success){
            return Result.ok("删除成功");
        }
        return Result.fail("删除失败");
    }

    //通过id批量删除区域里的仓库，删除全部
    @ApiOperation("通过id批量删除该区域的仓库")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList){
        regionWareService.batchRemoveByIdList(idList);
        return Result.ok(null);
    }
}
