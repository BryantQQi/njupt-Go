package com.atnjupt.sqyxgo.user.api;

import com.atnjupt.sqyxgo.user.Service.LeaderService;
import com.atnjupt.sqyxgo.vo.user.LeaderAddressVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName:UserInnerController
 * Package: com.atnjupt.sqyxgo.user.api
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/28 16:35
 * @Version 1.0
 */
@Api(tags = "远程调用接口测试")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserInnerController {

    private final LeaderService leaderService;



    //通过userId获得当前登录用户的提货点地址信息
    @ApiOperation("通过userId获得当前登录用户的提货点地址信息")
    @GetMapping("inner/getLeaderAddressVoByUserId/{userId}")
    public LeaderAddressVo getLeaderAddressVoByUserId(@PathVariable(value = "userId") Long userId){
        LeaderAddressVo leaderAddressVo = leaderService.getLeaderAddressVoByUserId(userId);
        return leaderAddressVo;
    }


}
