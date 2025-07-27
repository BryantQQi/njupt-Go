package com.atnjupt.sqyxgo.activity.controller;


import com.atnjupt.sqyxgo.activity.service.CouponInfoService;
import com.atnjupt.sqyxgo.common.result.Result;
import com.atnjupt.sqyxgo.model.activity.CouponInfo;
import com.atnjupt.sqyxgo.vo.activity.CouponRuleVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 优惠券信息 前端控制器
 * </p>
 *
 * @author atnjupt
 * @since 2025-07-21
 */
@RestController
@RequestMapping("/admin/activity/couponInfo")
@Api(tags = "优惠卷测试接口")
@RequiredArgsConstructor
public class CouponInfoController {

    private final CouponInfoService couponInfoService;

    //优惠卷信息分页查询
    @ApiOperation("优惠卷信息分页查询")
    @GetMapping("{page}/{limit}")
    public Result getPage(@PathVariable(value = "page") Long page,
                          @PathVariable(value = "limit") Long limit){
        Page<CouponInfo> couponInfoPage = new Page<>(page,limit);
        IPage<CouponInfo> pageModel = couponInfoService.getPage(couponInfoPage);
        return Result.ok(pageModel);
    }
    //通过id查询优惠卷信息
    @ApiOperation("通过id查询优惠卷信息")
    @GetMapping("get/{id}")
    public Result get(@PathVariable(value = "id") Long id){
        CouponInfo couponInfo = couponInfoService.getCouponInfoById(id);
        return Result.ok(couponInfo);
    }
    //添加优惠卷信息
    @ApiOperation("添加优惠卷信息")
    @PostMapping("save")
    public Result save(@RequestBody CouponInfo couponInfo){
        couponInfoService.save(couponInfo);
        return Result.ok(null);
    }
    //通过id更新优惠卷信息
    @ApiOperation("通过id更新优惠卷信息")
    @PutMapping("update")
    public Result update(@RequestBody CouponInfo couponInfo){
        couponInfoService.updateById(couponInfo);
        return Result.ok(null);
    }
    //通过id删除优惠卷信息
    @ApiOperation("通过id删除优惠卷信息")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable(value = "id") Long id){
        couponInfoService.removeById(id);
        return Result.ok(null);
    }

    //通过idList批量删除优惠卷
    @ApiOperation("通过idList批量删除优惠卷")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList){
        couponInfoService.removeByIds(idList);
        return Result.ok(null);
    }


    //优惠卷规则功能模块，分页查询之后，每个优惠卷右侧有一个规则按钮，下面的接口是规则按钮下的功能
    //根据优惠卷id获取优惠券规则信息CouponRuleVo，用于展示,进入页面会自动调用和这个接口
    @ApiOperation("获取优惠券信息")
    @GetMapping("findCouponRuleList/{id}")
    public Result findCouponRuleList(@PathVariable(value = "id") Long id){
        Map<String,Object> map = couponInfoService.finCouponRuleList(id);
        return Result.ok(map);
    }
    //通过优惠卷id为优惠卷新增一个规则，前端展示的添加规则按钮
    @ApiOperation("新增活动")
    @PostMapping("saveCouponRule")
    public Result saveCouponRule(@RequestBody CouponRuleVo couponRuleVo){
        couponInfoService.saveCouponRule(couponRuleVo);
        return Result.ok(null);
    }
    //根据关键字获取sku列表，活动使用，就是把商品添加到这个优惠卷面下，表示此商品可以使用这个优惠卷，前端展示的添加活动范围按钮
    @ApiOperation("根据关键字获取sku列表，活动使用")
    @GetMapping("findCouponByKeyword/{keyword}")
    public Result findCouponByKeyword(@PathVariable(value = "keyword") String keyword){
        List<CouponInfo> couponInfoList = couponInfoService.findCouponByKeyword(keyword);
        return Result.ok(null);
    }



}

