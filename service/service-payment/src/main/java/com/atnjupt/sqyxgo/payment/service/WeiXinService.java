package com.atguigu.ssyx.payment.service;

import java.util.Map;

/**
 * ClassName: WeiXinService
 * Package: com.atguigu.ssyx.service
 * Description:
 *
 * @Author liang
 * @Create 2024/11/28 20:23
 * @Version jdk17.0
 */
public interface WeiXinService {
    Map<String, String> createJsapi(String orderNo);

    Map<String, String> queryPayStatus(String orderNo, String name);
}
