package com.atnjupt.sqyxgo.client.cart;

import com.atnjupt.sqyxgo.model.order.CartInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * ClassName: CartFeignClient
 * Package: com.atguigu.ssyx.client.cart
 * Description:
 *
 * @Author liang
 * @Create 2024/11/26 18:11
 * @Version jdk17.0
 */
@FeignClient("service-cart")
public interface CartFeignClient {

    /**
     * 根据用户Id 查询已经选中购物车列表
     *
     * @param userId
     * @return
     */
    @GetMapping("api/cart/inner/getCartCheckedList/{userId}")
    public List<CartInfo> getCartCheckedList(@PathVariable("userId") Long userId);
}
