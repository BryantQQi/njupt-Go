package com.atnjupt.sqyxgo.sys.controller;

import com.atnjupt.sqyxgo.common.result.Result;
import com.atnjupt.sqyxgo.model.sys.Region;
import com.atnjupt.sqyxgo.sys.service.RegionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * ClassName:RegionController
 * Package: com.atnjupt.sqyxgo.sys.controller
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/17 18:20
 * @Version 1.0
 */
@Api(tags = "区域管理接口")
@RequestMapping("/admin/sys/region")
@RestController
@RequiredArgsConstructor
public class RegionController {

    private final RegionService regionService;
    //通过关键字查询区域
    @ApiOperation("通过关键字查询区域")
    @GetMapping("findRegionByKeyword/{keyword}")
    public Result findRegionByKeyword(@PathVariable(value = "keyword") String keyword){
        List<Region> list = regionService.findRegionByKeyword(keyword);
        return Result.ok(list);
    }

    //通过parentId查询区域
    @ApiOperation("通过parentId查询区域")
    @GetMapping("findByParentId/{parentId}")
    public Result findRegionByKeyword(@PathVariable(value = "parentId") Long parentId){
        List<String> list = regionService.findByParentId(parentId);
        return Result.ok(list);
    }
}
