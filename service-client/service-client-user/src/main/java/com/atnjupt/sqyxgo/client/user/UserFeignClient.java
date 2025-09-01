package com.atnjupt.sqyxgo.client.user;

import com.atnjupt.sqyxgo.vo.user.LeaderAddressVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * ClassName:UserFeignClient
 * Package: com.atnjupt.sqyxgo.client.user
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/28 16:26
 * @Version 1.0
 */
@FeignClient(value = "service-user")
public interface UserFeignClient {

    //当前登录用户的提货点地址信息
    @GetMapping("/api/user/inner/getLeaderAddressVoByUserId/{userId}")
    LeaderAddressVo getLeaderAddressVoByUserId(@PathVariable(value = "userId") Long userId);

    @GetMapping("api/user/leader/inner/getUserAddressByUserId/{userId}")
    public LeaderAddressVo getUserAddressByUserId(@PathVariable Long userId);
}
