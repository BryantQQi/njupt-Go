package com.atnjupt.sqyxgo.client.activity;

import org.springframework.cloud.openfeign.FeignClient;

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




}
