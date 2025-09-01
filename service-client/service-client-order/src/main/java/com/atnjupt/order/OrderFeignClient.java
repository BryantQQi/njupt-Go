package com.atnjupt.order;

import com.atnjupt.sqyxgo.model.order.OrderInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * ClassName: OrderFeignClient
 * Package: com.atguigu.ssyx.client.order
 * Description:
 *
 * @Author liang
 * @Create 2024/11/28 23:01
 * @Version jdk17.0
 */
@FeignClient("service-order")
public interface OrderFeignClient {

    /**
     * 根据orderNo获取orderInfo
     */
    @GetMapping("api/order/auth/getOrderInfoByOrderNo/{orderNo}")
    public OrderInfo getOrderInfoById(@PathVariable("orderNo") String orderNo);
}
