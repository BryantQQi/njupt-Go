package com.atnjupt.sqyxgo.activity.api;

import com.atnjupt.sqyxgo.activity.service.ActivityInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * ClassName:ActivityApiController
 * Package: com.atnjupt.sqyxgo.activity.api
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/28 19:06
 * @Version 1.0
 */
@Api(tags = "远程调用测试接口")
@RestController
@RequestMapping("/api/activity")
@RequiredArgsConstructor
public class ActivityApiController {

    private final ActivityInfoService activityInfoService;

    //获取skuId对应的促销活动标签
    @ApiOperation("//获取skuId对应的促销活动标签")
    @PostMapping("inner/findActivity")
    public Map<Long, List<String>> findActivity(@RequestBody List<Long> skuIdList){
        return activityInfoService.findActivity(skuIdList);
    }

    //sku对应的促销与优惠券信息
    @ApiOperation("sku对应的促销与优惠券信息")
    @PostMapping("inner/findActivityAndCoupon/{skuId}/{userId}")
    public Map<String, Object> findActivityAndCoupon(@PathVariable(value = "skuId") Long skuId
            ,@PathVariable(value = "userId") Long userId){

        return activityInfoService.findActivityAndCoupon(skuId, userId);
    }
}
