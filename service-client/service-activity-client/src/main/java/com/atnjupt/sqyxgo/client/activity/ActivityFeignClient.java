package com.atnjupt.sqyxgo.client.activity;

import com.atnjupt.sqyxgo.model.activity.CouponInfo;
import com.atnjupt.sqyxgo.model.order.CartInfo;
import com.atnjupt.sqyxgo.vo.order.CartInfoVo;
import com.atnjupt.sqyxgo.vo.order.OrderConfirmVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * ClassName:ActivityFeignClient
 * Package: com.atnjupt.sqyxgo.client.activity
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/22 9:42
 * @Version 1.0
 */
@FeignClient(value = "service-activity")
public interface ActivityFeignClient {

    //获取skuId对应的促销活动标签
    @PostMapping("/api/activity/inner/findActivity")
    Map<Long, List<String>> findActivity(@RequestBody List<Long> skuIdList);
    //sku对应的促销与优惠券信息
    @GetMapping("/api/activity/inner/findActivityAndCoupon/{skuId}/{userId}")
    Map<String, Object> findActivityAndCoupon(@PathVariable(value = "skuId") Long skuId,@PathVariable(value = "userId") Long userId);

    //获取购物车中对应得优惠卷
    @PostMapping("api/activity/inner/findRangeSkuIdList/{couponId}")
    CouponInfo findRangeSkuIdList(@RequestBody List<CartInfo> cartInfoList, @PathVariable Long couponId);

    //获取购物车规则数据
    @PostMapping("api/activity/inner/findCartActivityList")
    List<CartInfoVo> findCartActivityList(@RequestBody List<CartInfo> cartInfoList);
    /**
     * 更新优惠卷得使用状态
     */
    @GetMapping("api/activity/inner/updateCouponUserStatus/{couponId}/{userId}/{orderId}")
    boolean updateCouponUserStatus(@PathVariable Long couponId, @PathVariable Long userId, @PathVariable Long orderId);
    /**
     * 查询带优惠卷的购物车
     *
     * @param
     * @return
     */
    @ApiOperation(value = "获取购物车满足条件的促销与优惠券信息")
    @PostMapping("api/activity/inner/findCartActivityAndCoupon/{userId}")
    public OrderConfirmVo findCartActivityAndCoupon(@RequestBody List<CartInfo> cartInfoList,
                                                    @PathVariable("userId") Long userId);
}
