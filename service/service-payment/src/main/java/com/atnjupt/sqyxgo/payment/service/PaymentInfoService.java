package com.atnjupt.sqyxgo.payment.service;


import com.atnjupt.sqyxgo.enums.PaymentType;
import com.atnjupt.sqyxgo.model.order.PaymentInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
* @author liang
* @description 针对表【payment_info(支付信息表)】的数据库操作Service
* @createDate 2024-11-28 20:31:19
*/
public interface PaymentInfoService extends IService<PaymentInfo> {

    PaymentInfo savePaymentRecode(String orderNo);

    void paySuccess(String out_trade_no, PaymentType weixin, Map<String, String> resultMap);
}
