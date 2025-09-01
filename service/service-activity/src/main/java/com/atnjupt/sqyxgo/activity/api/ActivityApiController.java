package com.atnjupt.sqyxgo.activity.api;

import com.atnjupt.sqyxgo.activity.service.ActivityInfoService;
import com.atnjupt.sqyxgo.activity.service.CouponInfoService;
import com.atnjupt.sqyxgo.model.activity.CouponInfo;
import com.atnjupt.sqyxgo.model.order.CartInfo;
import com.atnjupt.sqyxgo.vo.order.CartInfoVo;
import com.atnjupt.sqyxgo.vo.order.OrderConfirmVo;
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
    private final CouponInfoService couponInfoService;
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

    /**
     * 获取购物车中对应得优惠卷
     */
    @PostMapping("inner/findRangeSkuIdList/{couponId}")
    public CouponInfo findRangeSkuIdList(@RequestBody List<CartInfo> cartInfoList, @PathVariable Long couponId){
        CouponInfo couponInfo = couponInfoService.findRangeSkuIdList(cartInfoList,couponId);
        return  couponInfo;
    }

    /**
     * 获取购物车规则数据
     * @param cartInfoList
     * @return
     *
     */
    @PostMapping("inner/findCartActivityList")
    public List<CartInfoVo> findCartActivityList(@RequestBody List<CartInfo> cartInfoList) {
        return activityInfoService.findCartActivityList(cartInfoList);
    }
    /**
     * 更新优惠卷得使用状态
     */
    @GetMapping("inner/updateCouponUserStatus/{couponId}/{userId}/{orderId}")
    public boolean updateCouponUserStatus(@PathVariable Long couponId, @PathVariable Long userId, @PathVariable Long orderId){
        return couponInfoService.updateCouponUserStatus(couponId, userId, orderId);
    }
    //获取购物车满足条件的促销与优惠券信息
    @ApiOperation(value = "获取购物车满足条件的促销与优惠券信息")
    @PostMapping("inner/findCartActivityAndCoupon/{userId}")
    public OrderConfirmVo findCartActivityAndCoupon(@RequestBody List<CartInfo> cartInfoList, @PathVariable("userId") Long userId) {
        OrderConfirmVo data =activityInfoService.findCartActivityAndCoupon(cartInfoList, userId);
        return  data;
    }
}
