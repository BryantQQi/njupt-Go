package com.atnjupt.sqyxgo.user.Service.impl;

import com.atnjupt.sqyxgo.enums.UserType;
import com.atnjupt.sqyxgo.model.user.Leader;
import com.atnjupt.sqyxgo.model.user.User;
import com.atnjupt.sqyxgo.model.user.UserDelivery;
import com.atnjupt.sqyxgo.user.Service.UserDeliveryService;
import com.atnjupt.sqyxgo.user.Service.UserService;
import com.atnjupt.sqyxgo.user.mapper.LeaderMapper;
import com.atnjupt.sqyxgo.user.mapper.UserDeliveryMapper;
import com.atnjupt.sqyxgo.user.mapper.UserMapper;
import com.atnjupt.sqyxgo.vo.user.LeaderAddressVo;
import com.atnjupt.sqyxgo.vo.user.UserLoginVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * ClassName:UserServiceImpl
 * Package: com.atnjupt.sqyxgo.user.Service.impl
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/24 11:38
 * @Version 1.0
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserDeliveryMapper userDeliveryMapper;
    private final LeaderMapper leaderMapper;
    private final UserDeliveryService userDeliveryService;

    ////判断是否是第一次使用微信授权登录：如何判断？openId
    @Override
    public User getUserByOpenId(String openid) {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getOpenId,openid);
        User user = baseMapper.selectOne(lambdaQueryWrapper);
        return user;
    }

    ///5.1 通过userId拿到Leader信息，封装到LeaderAddressVo里面
    @Override
    public LeaderAddressVo getLeaderAddressVoByUserId(Long userId) {
        LeaderAddressVo leaderAddressVo = new LeaderAddressVo();
        //1 获取用户对应的团长的团长id,对应团长表中的自增id
        UserDelivery userDelivery = userDeliveryMapper.selectOne(
                new LambdaQueryWrapper<UserDelivery>().eq(UserDelivery::getUserId, userId)
                        .eq(UserDelivery::getIsDefault,1)
        );
        if (userDelivery == null){
            return null;
        }
        //根据团长id去查团长表
        Leader leader = leaderMapper.selectById(userDelivery.getLeaderId());
        //拷贝信息
        BeanUtils.copyProperties(leader,leaderAddressVo);

        return leaderAddressVo;
    }
    //7 获取当前登录用户信息封装到UserLoginVo，放到Redis里面，设置有效时间
    @Override
    public UserLoginVo getUserLoginVo(Long userId) {
        User user = baseMapper.selectById(userId);
        UserDelivery userDelivery = userDeliveryMapper.selectOne(
                new LambdaQueryWrapper<UserDelivery>().eq(UserDelivery::getUserId, userId)
                        .eq(UserDelivery::getIsDefault,1)
        );
        UserLoginVo userLoginVo = new UserLoginVo();
        BeanUtils.copyProperties(user,userLoginVo);

        //如果是团长，则获取团长对应的仓库id
        if(user.getUserType() == UserType.LEADER){
            LambdaQueryWrapper<Leader> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Leader::getUserId,userId);
            Leader leader = leaderMapper.selectOne(wrapper);
            userLoginVo.setLeaderId(leader.getId());
            userLoginVo.setWareId(userDelivery.getWareId());
        }else { //如果是会员，则获取会员对应的仓库id
            userLoginVo.setLeaderId(userDelivery.getLeaderId());
            userLoginVo.setWareId(userDelivery.getWareId());
        }
        return userLoginVo;
    }
    //根据用户id查询提货点和团长信息
    @Override
    public LeaderAddressVo getLeaderAddressByUserId(Long userId) {
        LambdaQueryWrapper<UserDelivery> userDeliveryLambdaQueryWrapper = new LambdaQueryWrapper<UserDelivery>().
                eq(UserDelivery::getUserId, userId).eq(UserDelivery::getIsDefault, 1);
        UserDelivery userDelivery = userDeliveryService.getOne(userDeliveryLambdaQueryWrapper);//这几行是根据 userId 和 is_default=1 这两个条件，从数据库表 user_delivery 查出这一条完整的 UserDelivery 对象，并赋值给 userDelivery。
        if(userDelivery == null){
            return null;
        }
        Leader leader = leaderMapper.selectById(userDelivery.getLeaderId());

        LeaderAddressVo leaderAddressVo = new LeaderAddressVo();/*Vo（View Object）专门用来给前端的，实体类对应的是数据库中的表，但是表中的一些数据前端并不需要，所以出来了Vo类，把前端需要的数据封装到这个类中，传给前端，这是其一。其二就是前端需要一些数据，这些数据并不是数据库中某一个表能完全覆盖，这些数据可能一部分在A表一部分在B表，这样你传实体类也不行，只能写一个Vo类，把A表中需要的数据放进去，再把B表中需要的数据放进去，然后把这个Vo类转换成json传给前端*/
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
