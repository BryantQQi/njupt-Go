package com.atnjupt.sqyxgo.acl.service.impl;

import com.atnjupt.sqyxgo.acl.mapper.AdminRoleMapper;
import com.atnjupt.sqyxgo.acl.service.AdminRoleService;
import com.atnjupt.sqyxgo.model.acl.Admin;
import com.atnjupt.sqyxgo.model.acl.AdminRole;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ClassName:AdminRoleServiceImpl
 * Package: com.atnjupt.sqyxgo.acl.service.impl
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/9 20:35
 * @Version 1.0
 */
@Service
@RequiredArgsConstructor
public class AdminRoleServiceImpl extends ServiceImpl<AdminRoleMapper, AdminRole> implements AdminRoleService {

    //8 为用户分配角色
    @Override
    public boolean assignAdminRole(List<Long> roleId, Long adminId) {
        boolean is_success = false;
        //1 获得用户的所有角色集合List<Long>
        LambdaQueryWrapper<AdminRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminRole::getAdminId,adminId);
        List<AdminRole> adminRoleList = this.list(wrapper);
        List<Long> roleIdList = adminRoleList.stream().map(AdminRole::getRoleId).collect(Collectors.toList());
        //2 遍历所有roleId
        for (Long assignRoleId : roleId) {
            //2.1 判断用户是否已经被分配了这个角色,没有则进入if
            if(!roleIdList.contains(assignRoleId)){
                AdminRole adminRole = new AdminRole();
                adminRole.setRoleId(assignRoleId);
                adminRole.setAdminId(adminId);
                //2.1.1 向admin_role表添加记录
                this.save(adminRole);
                is_success = true;
            }

        }
        return is_success;
    }
}
