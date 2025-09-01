package com.atnjupt.sqyxgo.order.controller;

import com.atnjupt.sqyxgo.common.result.Result;
import com.atnjupt.sqyxgo.order.service.OrderDemoService;
import com.atnjupt.sqyxgo.vo.order.OrderTimeoutDemoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 订单超时Demo控制器
 */
@Api(value = "订单超时Demo", tags = "订单超时Demo")
@RestController
@RequestMapping("/api/order/demo")
@RequiredArgsConstructor
public class OrderDemoController {

    private final OrderDemoService orderDemoService;

    @ApiOperation("创建超时订单Demo")
    @PostMapping("/createTimeoutOrder")
    public Result<Long> createTimeoutOrder(@RequestBody OrderTimeoutDemoVo orderDemoVo) {
        Long orderId = orderDemoService.createTimeoutOrder(orderDemoVo);
        return Result.ok(orderId);
    }

    @ApiOperation("获取订单状态")
    @GetMapping("/getOrderStatus/{orderNo}")
    public Result<String> getOrderStatus(@PathVariable String orderNo) {
        String status = orderDemoService.getOrderStatus(orderNo);
        return Result.ok(status);
    }
}
