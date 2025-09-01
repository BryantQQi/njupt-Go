package com.atnjupt.sqyxgo.cart.controller;


import com.atnjupt.sqyxgo.cart.service.CartInfoService;
import com.atnjupt.sqyxgo.common.result.Result;
import com.atnjupt.sqyxgo.model.order.CartInfo;
import com.atnjupt.sqyxgo.common.security.AuthContextHolder;
import com.atnjupt.sqyxgo.vo.order.OrderConfirmVo;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.atnjupt.sqyxgo.client.activity.ActivityFeignClient;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * ClassName: CartApiController
 * Package: com.atguigu.ssyx.controller
 * Description:
 *
 * @Author liang
 * @Create 2024/11/21 21:38
 * @Version jdk17.0
 */
@RestController
@RequestMapping("api/cart")
@RequiredArgsConstructor
public class CartApiController {
    private final ActivityFeignClient activityFeignClient;
    private final CartInfoService cartInfoService;


    /**
     * 添加购物车
     *
     * @param skuId
     * @param skuNum
     * @return
     */
    @GetMapping("addToCart/{skuId}/{skuNum}")
    public Result addToCart(@PathVariable("skuId") Long skuId,
                            @PathVariable("skuNum") Integer skuNum) {
        // 获取userId
        Long userId = AuthContextHolder.getUserIdThreadLocal();
        cartInfoService.addToCart(skuId, userId, skuNum);
        return Result.ok(null);
    }


    /**
     * 删除
     *
     * @param skuId
     * @param request
     * @return
     */
    @DeleteMapping("deleteCart/{skuId}")
    public Result deleteCart(@PathVariable("skuId") Long skuId,
                             HttpServletRequest request) {
        // 如何获取userId
        Long userId = AuthContextHolder.getUserIdThreadLocal();
        cartInfoService.deleteCart(skuId, userId);
        return Result.ok(null);
    }

    @ApiOperation(value="清空购物车")
    @DeleteMapping("deleteAllCart")
    public Result deleteAllCart(HttpServletRequest request){
        // 获取userId
        Long userId = AuthContextHolder.getUserIdThreadLocal();
        cartInfoService.deleteAllCart(userId);
        return Result.ok(null);
    }

    @ApiOperation(value="批量删除购物车")
    @PostMapping("batchDeleteCart")
    public Result batchDeleteCart(@RequestBody List<Long> skuIdList, HttpServletRequest request){
        // 取userId
        Long userId = AuthContextHolder.getUserIdThreadLocal();
        cartInfoService.batchDeleteCart(skuIdList, userId);
        return Result.ok(null);
    }

    /**
     * 购物车列表功能
     */

    /**
     * 查询购物车列表
     *
     * @param request
     * @return
     */
    @GetMapping("cartList")
    public Result cartList(HttpServletRequest request) {
        // 获取用户Id
        Long userId = AuthContextHolder.getUserIdThreadLocal();
        List<CartInfo> cartInfoList = cartInfoService.getCartList(userId);
        return Result.ok(cartInfoList);
    }

    /**
     * 查询带优惠卷的购物车
     *
     * @param
     * @return
     */
    @GetMapping("activityCartList")
    public Result activityCartList() {
        // 获取用户Id
        Long userId = AuthContextHolder.getUserIdThreadLocal();
        List<CartInfo> cartInfoList = cartInfoService.getCartList(userId);
        OrderConfirmVo orderTradeVo = activityFeignClient.findCartActivityAndCoupon(cartInfoList, userId);
        return Result.ok(orderTradeVo);
    }

    /**
     * 更新选中状态
     *
     * @param skuId
     * @param isChecked
     * @return
     */
    @GetMapping("checkCart/{skuId}/{isChecked}")
    public Result checkCart(@PathVariable(value = "skuId") Long skuId,
                            @PathVariable(value = "isChecked") Integer isChecked) {
        // 获取用户Id
        Long userId = AuthContextHolder.getUserIdThreadLocal();
        // 调用更新方法
        cartInfoService.checkCart(userId, isChecked, skuId);
        return Result.ok(null);
    }

    /**
     * 选中所有购物车
     * @param isChecked
     * @return
     */
    @GetMapping("checkAllCart/{isChecked}")
    public Result checkAllCart(@PathVariable(value = "isChecked") Integer isChecked) {
        // 获取用户Id
        Long userId = AuthContextHolder.getUserIdThreadLocal();
        // 调用更新方法
        cartInfoService.checkAllCart(userId, isChecked);
        return Result.ok(null);
    }

    @ApiOperation(value="批量选择购物车")
    @PostMapping("batchCheckCart/{isChecked}")
    public Result batchCheckCart(@RequestBody List<Long> skuIdList, @PathVariable(value = "isChecked") Integer isChecked, HttpServletRequest request){
        // 如何获取userId
        Long userId = AuthContextHolder.getUserIdThreadLocal();
        cartInfoService.batchCheckCart(skuIdList, userId, isChecked);
        return Result.ok(null);
    }

    /**
     * 根据用户Id 查询已经选中购物车列表
     *
     * @param userId
     * @return
     */
    @GetMapping("inner/getCartCheckedList/{userId}")
    public List<CartInfo> getCartCheckedList(@PathVariable("userId") Long userId) {
        return cartInfoService.getCartCheckedList(userId);
    }


}
