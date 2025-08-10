package com.atnjupt.sqyxgo.user.Service.impl;

import com.atnjupt.sqyxgo.model.user.Leader;
import com.atnjupt.sqyxgo.model.user.UserDelivery;
import com.atnjupt.sqyxgo.user.Service.LeaderService;
import com.atnjupt.sqyxgo.user.Service.UserService;
import com.atnjupt.sqyxgo.user.mapper.LeaderMapper;
import com.atnjupt.sqyxgo.user.mapper.UserDeliveryMapper;
import com.atnjupt.sqyxgo.user.mapper.UserMapper;
import com.atnjupt.sqyxgo.vo.user.LeaderAddressVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * ClassName:LeaderServiceImpl
 * Package: com.atnjupt.sqyxgo.user.Service.impl
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/24 12:18
 * @Version 1.0
 */
@Service
@RequiredArgsConstructor
public class LeaderServiceImpl extends ServiceImpl<LeaderMapper, Leader> implements LeaderService {

    private final UserMapper userMapper;
    private final UserDeliveryMapper userDeliveryMapper;


    //通过userId获得当前登录用户的提货点地址信息
    @Override
    public LeaderAddressVo getLeaderAddressVoByUserId(Long userId) {
        //用户可能有很多Leader，我们先从userdeliver查出user默认的LeaderId
        LambdaQueryWrapper<UserDelivery> userDeliveryLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userDeliveryLambdaQueryWrapper.eq(UserDelivery::getUserId,userId)
                .eq(UserDelivery::getIsDefault,1);
        UserDelivery userDelivery = userDeliveryMapper.selectOne(userDeliveryLambdaQueryWrapper);
        Long leaderId = userDelivery.getLeaderId();

        //通过默认的LeaderId查出地址，并封装到LeaderAddressVo里面
        Leader leader = baseMapper.selectById(leaderId);
        LeaderAddressVo leaderAddressVo = new LeaderAddressVo();
        BeanUtils.copyProperties(leader, leaderAddressVo);
        leaderAddressVo.setUserId(userId);
        leaderAddressVo.setLeaderId(leader.getId());
        leaderAddressVo.setLeaderName(leader.getName());
        leaderAddressVo.setLeaderPhone(leader.getPhone());
        leaderAddressVo.setWareId(userDelivery.getWareId());
        leaderAddressVo.setStorePath(leader.getStorePath());

        return leaderAddressVo;
    }
}
