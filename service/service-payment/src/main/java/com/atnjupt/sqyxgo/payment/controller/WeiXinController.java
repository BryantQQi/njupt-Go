package com.atguigu.ssyx.payment.controller;

import com.atguigu.ssyx.common.result.Result;
import com.atguigu.ssyx.common.result.ResultCodeEnum;
import com.atguigu.ssyx.enums.PaymentType;
import com.atguigu.ssyx.payment.service.PaymentInfoService;
import com.atguigu.ssyx.payment.service.WeiXinService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * ClassName: WeiXinController
 * Package: com.atguigu.ssyx.controller
 * Description:
 *
 * @Author liang
 * @Create 2024/11/28 20:22
 * @Version jdk17.0
 */
@Api(tags = "微信支付接口")
@RestController
@RequestMapping("/api/payment/weixin")
@Slf4j
@RequiredArgsConstructor
public class WeiXinController {
    private final PaymentInfoService paymentInfoService;
    private final WeiXinService weiXinService;

    /**
     * 调用微信支付系统，生成一个预付单
     * @param orderNo
     * @return
     */
    @ApiOperation(value = "下单 小程序支付")
    @GetMapping("/createJsapi/{orderNo}")
    public Result createJsapi(
            @ApiParam(name = "orderNo", value = "订单No", required = true)
            @PathVariable("orderNo") String orderNo) {
     Map<String,String> result = weiXinService.createJsapi(orderNo);
        return Result.ok(result);
    }

    /**
     * 判断微信支付是否成功，并做出相应的处理
     */
    @ApiOperation(value = "查询支付状态")
    @GetMapping("/queryPayStatus/{orderNo}")
    public Result queryPayStatus(
            @ApiParam(name = "orderNo", value = "订单No", required = true)
            @PathVariable("orderNo") String orderNo) {
        //调用查询接口
        Map<String, String> resultMap = weiXinService.queryPayStatus(orderNo, PaymentType.WEIXIN.name());
        if (resultMap == null) {//出错
            return Result.fail(null);
        }
        if ("SUCCESS".equals(resultMap.get("trade_state"))) {//如果成功
            //更改订单状态，处理支付结果
            String out_trade_no = resultMap.get("out_trade_no");
           paymentInfoService.paySuccess(out_trade_no, PaymentType.WEIXIN, resultMap);
            return Result.ok(null);
        }
        return Result.ok(ResultCodeEnum.PAY_WAIT);
    }
}
