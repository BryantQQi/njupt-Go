package com.atguigu.ssyx.order.controller;

import com.atguigu.ssyx.common.auth.AuthContextHolder;
import com.atguigu.ssyx.common.result.Result;
import com.atguigu.ssyx.model.order.OrderInfo;
import com.atguigu.ssyx.order.service.OrderInfoService;
import com.atguigu.ssyx.vo.order.OrderConfirmVo;
import com.atguigu.ssyx.vo.order.OrderSubmitVo;
import com.atguigu.ssyx.vo.order.OrderUserQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

@Api(value = "Order管理", tags = "Order管理")
@RestController
@RequestMapping(value="/api/order")
@RequiredArgsConstructor
public class OrderApiController {
	private final OrderInfoService orderService;


	@ApiOperation("确认订单")
	@GetMapping("auth/confirmOrder")
	public Result confirm() {
		OrderConfirmVo orderConfirmVo = orderService.confirmOrder();
		return Result.ok(orderConfirmVo);
	}

	@ApiOperation("生成订单")
	@PostMapping("auth/submitOrder")
	public Result submitOrder(@RequestBody OrderSubmitVo orderParamVo, HttpServletRequest request) {
		// 获取到用户Id
		Long userId = AuthContextHolder.getUserId();
		Long orderId = orderService.submitOrder(orderParamVo);
		return Result.ok(orderId);
	}

	@ApiOperation("获取订单详情")
	@GetMapping("auth/getOrderInfoById/{orderId}")
	public Result getOrderInfoById(@PathVariable("orderId") Long orderId){
		OrderInfo orderInfo = orderService.getOrderInfoById(orderId);
		return Result.ok(orderInfo);
	}
	/**
	 * 根据orderNo获取orderInfo
	 */
	@GetMapping("auth/getOrderInfoByOrderNo/{orderNo}")
	public OrderInfo getOrderInfoById(@PathVariable("orderNo") String orderNo){
		LambdaQueryWrapper<OrderInfo> orderInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
		orderInfoLambdaQueryWrapper.eq(OrderInfo::getOrderNo,orderNo);
		OrderInfo one = orderService.getOne(orderInfoLambdaQueryWrapper);
		return  one;
	}

	@ApiOperation(value = "获取用户订单分页列表")
	@GetMapping("auth/findUserOrderPage/{page}/{limit}")
	public Result findUserOrderPage(
			@ApiParam(name = "page", value = "当前页码", required = true)
			@PathVariable Long page,

			@ApiParam(name = "limit", value = "每页记录数", required = true)
			@PathVariable Long limit,

			@ApiParam(name = "orderVo", value = "查询对象", required = false)
			OrderUserQueryVo orderUserQueryVo) {
		Long userId = AuthContextHolder.getUserId();
		orderUserQueryVo.setUserId(userId);
		Page<OrderInfo> pageParam = new Page<>(page, limit);
		IPage<OrderInfo> pageModel = orderService.findUserOrderPage(pageParam, orderUserQueryVo);
		return Result.ok(pageModel);
	}


}