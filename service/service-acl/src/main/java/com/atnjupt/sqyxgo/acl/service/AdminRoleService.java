package com.atnjupt.sqyxgo.acl.service;

import com.atnjupt.sqyxgo.model.acl.AdminRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * ClassName:AdminRoleService
 * Package: com.atnjupt.sqyxgo.acl.service
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/9 20:34
 * @Version 1.0
 */
public interface AdminRoleService extends IService<AdminRole> {
    //8 为用户分配角色
    boolean assignAdminRole(List<Long> roleId, Long adminId);
}
