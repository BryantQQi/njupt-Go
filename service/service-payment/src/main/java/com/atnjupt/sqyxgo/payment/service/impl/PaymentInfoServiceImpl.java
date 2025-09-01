package com.atguigu.ssyx.payment.service.impl;


import com.atguigu.ssyx.client.order.OrderFeignClient;
import com.atguigu.ssyx.common.exception.SsyxException;
import com.atguigu.ssyx.common.result.ResultCodeEnum;
import com.atguigu.ssyx.enums.PaymentStatus;
import com.atguigu.ssyx.enums.PaymentType;
import com.atguigu.ssyx.model.order.OrderInfo;
import com.atguigu.ssyx.model.order.PaymentInfo;
import com.atguigu.ssyx.mq.constant.MqConst;
import com.atguigu.ssyx.mq.service.RabbitService;
import com.atguigu.ssyx.payment.service.PaymentInfoService;
import com.atguigu.ssyx.payment.mapper.PaymentInfoMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
* @author liang
* @description 针对表【payment_info(支付信息表)】的数据库操作Service实现
* @createDate 2024-11-28 20:31:19
*/
@Service
@RequiredArgsConstructor
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo>
    implements PaymentInfoService{
    private final OrderFeignClient orderFeignClient;
    private final PaymentInfoMapper paymentInfoMapper;
    private final RabbitService rabbitService;
    /**
     * 添加支付记录
     * @param orderNo
     * @return
     */
    @Override
    public PaymentInfo savePaymentRecode(String orderNo) {
        //远程调用order模块，更加order唯一标识获得orderInfo
        OrderInfo order = orderFeignClient.getOrderInfoById(orderNo);
        if(order == null){
            throw new SsyxException(ResultCodeEnum.DATA_ERROR);
        }

        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setOrderId(order.getId());
        paymentInfo.setPaymentType(PaymentType.WEIXIN);
        paymentInfo.setUserId(order.getUserId());
        paymentInfo.setOrderNo(order.getOrderNo());
        paymentInfo.setPaymentStatus(PaymentStatus.UNPAID);
        String subject = "test";
        paymentInfo.setSubject(subject);
        //paymentInfo.setTotalAmount(order.getTotalAmount());
        paymentInfo.setTotalAmount(new BigDecimal("0.01"));

        paymentInfoMapper.insert(paymentInfo);
        return paymentInfo;

    }

    /**
     * 支付成功，更改支付状态，订单状态和库存
     * @param
     * @param weixin
     * @param resultMap
     */
    @Override
    public void paySuccess(String orderNo, PaymentType weixin, Map<String, String> resultMap) {
        PaymentInfo paymentInfo = baseMapper.selectOne(new LambdaQueryWrapper<PaymentInfo>().eq(PaymentInfo::getOrderNo, orderNo));
        if(paymentInfo.getPaymentStatus() != PaymentStatus.UNPAID){
            return;
        }
        //更改支付信息
        paymentInfo.setPaymentStatus(PaymentStatus.PAID);
        paymentInfo.setTradeNo(resultMap.get("transaction_id"));
        paymentInfo.setCallbackContent(resultMap.toString());
        paymentInfoMapper.updateById(paymentInfo);

        //减库存和修改订单支付状态
        rabbitService.sendMessage(MqConst.EXCHANGE_PAY_DIRECT,MqConst.ROUTING_PAY_SUCCESS,orderNo);
    }
}

 


