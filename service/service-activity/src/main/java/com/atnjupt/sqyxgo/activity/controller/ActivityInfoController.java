package com.atnjupt.sqyxgo.activity.controller;


import com.atnjupt.sqyxgo.activity.mapper.ActivityRuleMapper;
import com.atnjupt.sqyxgo.activity.service.ActivityInfoService;
import com.atnjupt.sqyxgo.common.result.Result;
import com.atnjupt.sqyxgo.model.activity.ActivityInfo;
import com.atnjupt.sqyxgo.model.activity.ActivityRule;
import com.atnjupt.sqyxgo.model.product.SkuInfo;
import com.atnjupt.sqyxgo.vo.activity.ActivityRuleVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 活动表 前端控制器
 * </p>
 *
 * @author atnjupt
 * @since 2025-07-21
 */
@RestController
@RequestMapping("/admin/activity/activityInfo")
@Api(tags = "营销活动测试接口")
@RequiredArgsConstructor
@Slf4j
public class ActivityInfoController {

    private final ActivityInfoService activityInfoService;



    //分页查询营销活动
    @ApiOperation("分页查询营销活动")
    @GetMapping("{page}/{limit}")
    public Result getPage(@PathVariable(value = "page") Long page,
                          @PathVariable(value = "limit") Long limit){
        Page<ActivityInfo> pageInfo = new Page<>(page,limit);
        IPage<ActivityInfo> pageModel = activityInfoService.getActivityPage(pageInfo);
        return Result.ok(pageModel);
    }
    //通过营销活动id查询营销活动信息
    @ApiOperation("通过营销活动id查询营销活动信息")
    @GetMapping("get/{id}")
    public Result getPageById(@PathVariable(value = "id") Long id){
        ActivityInfo activityInfo = activityInfoService.getActivityById(id);
        activityInfo.setActivityTypeString(activityInfo.getActivityType().getComment());
        return Result.ok(activityInfo);
    }

    //添加营销活动
    @ApiOperation("添加营销活动")
    @PostMapping("save")
    public Result save(@RequestBody ActivityInfo activityInfo){
        activityInfoService.save(activityInfo);
        return Result.ok(null);
    }
    //通过营销活动id更新营销活动信息
    @ApiOperation("通过营销活动id更新营销活动信息")
    @PutMapping("update")
    public Result update(@RequestBody ActivityInfo activityInfo){
        activityInfoService.updateById(activityInfo);
        return Result.ok(null);
    }
    //通过营销活动id删除营销活动信息
    @ApiOperation("通过营销活动id删除营销活动信息")
    @DeleteMapping("remove/{id}")
    public Result removeById(@PathVariable(value = "id") Long id){
        activityInfoService.removeById(id);
        return Result.ok(null);
    }

    //通过营销活动id批量删除营销活动信息
    @ApiOperation("通过营销活动id批量删除营销活动信息")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList){
        activityInfoService.removeByIds(idList);
        return Result.ok(null);
    }

    //营销活动规则相关接口
    //通过营销活动id查询营销活动规则
    @ApiOperation("通过营销活动id查询营销活动规则")
    @GetMapping("findActivityRuleList/{id}")
    public Result findActivityRuleList(@PathVariable(value = "id") Long id){
        Map<String,Object> activityRuleList = activityInfoService.getActivityRuleByActivityId(id);
        return Result.ok(activityRuleList);
    }
    //添加营销活动规则
    @ApiOperation("添加营销活动规则")
    @PostMapping("saveActivityRule")
    public Result saveActivityRule(@RequestBody ActivityRuleVo activityRuleVo){
        activityInfoService.saveActivityRule(activityRuleVo);
        return Result.ok(null);
    }

    //通过关键字查询商品信息,
    @ApiOperation("通过关键字查询商品信息")
    @GetMapping("findSkuInfoByKeyword/{keyword}")
    public Result findSkuInfoByKeyword(@PathVariable(value = "keyword") String keyword){

        List<SkuInfo> list = activityInfoService.findSkuInfoByKeyword(keyword);
        return Result.ok(list);
    }

}

