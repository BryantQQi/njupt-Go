package com.atnjupt.sqyxgo.user.Service;

import com.atnjupt.sqyxgo.model.user.Leader;
import com.atnjupt.sqyxgo.vo.user.LeaderAddressVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * ClassName:LeaderService
 * Package: com.atnjupt.sqyxgo.user.Service
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/24 12:17
 * @Version 1.0
 */
public interface LeaderService extends IService<Leader> {
    //通过userId获得当前登录用户的提货点地址信息
    LeaderAddressVo getLeaderAddressVoByUserId(Long userId);
}
