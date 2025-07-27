package com.atnjupt.sqyxgo.acl.service;

import com.atnjupt.sqyxgo.model.acl.RolePermission;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * ClassName:RolePermissionService
 * Package: com.atnjupt.sqyxgo.acl.service
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/10 15:41
 * @Version 1.0
 */
public interface RolePermissionService extends IService<RolePermission> {
    //查看某个角色的权限列表
    List<String> getRolePermissionList(Long roleId);

    //给某个角色授权
    void addRolePermission(Long roleId, List<Long> permissionId);
}
