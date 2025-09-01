package com.atnjupt.sqyxgo.user.Service;

import com.atnjupt.sqyxgo.model.user.User;
import com.atnjupt.sqyxgo.vo.user.LeaderAddressVo;
import com.atnjupt.sqyxgo.vo.user.UserLoginVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * ClassName:UserService
 * Package: com.atnjupt.sqyxgo.user.Service
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/24 11:38
 * @Version 1.0
 */
public interface UserService extends IService<User> {

    ////判断是否是第一次使用微信授权登录：如何判断？openId
    User getUserByOpenId(String openid);
    ///5.1 通过userId拿到Leader信息，封装到LeaderAddressVo里面
    LeaderAddressVo getLeaderAddressVoByUserId(Long userId);
    //7 获取当前登录用户信息封装到UserLoginVo，放到Redis里面，设置有效时间
    UserLoginVo getUserLoginVo(Long id);
    /**
     * 根据用户id获取用户的收货信息
     */
    LeaderAddressVo getLeaderAddressByUserId(Long userId);
}
