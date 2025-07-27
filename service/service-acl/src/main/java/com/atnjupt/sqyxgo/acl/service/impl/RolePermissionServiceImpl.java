package com.atnjupt.sqyxgo.acl.service.impl;

import com.atnjupt.sqyxgo.acl.mapper.PermissionMapper;
import com.atnjupt.sqyxgo.acl.mapper.RolePermissionMapper;
import com.atnjupt.sqyxgo.acl.service.PermissionService;
import com.atnjupt.sqyxgo.acl.service.RolePermissionService;
import com.atnjupt.sqyxgo.model.acl.Permission;
import com.atnjupt.sqyxgo.model.acl.RolePermission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ClassName:RolePermissionServiceImpl
 * Package: com.atnjupt.sqyxgo.acl.service.impl
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/10 15:41
 * @Version 1.0
 */
@Service
public class RolePermissionServiceImpl extends ServiceImpl<RolePermissionMapper, RolePermission> implements RolePermissionService {
    @Autowired
    private PermissionService permissionService;


    //查看某个角色的权限列表
    @Override
    public List<String> getRolePermissionList(Long roleId) {
        List<String> list = new ArrayList<>();
        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        List<RolePermission> rolePermissions = baseMapper.selectList(wrapper.eq(RolePermission::getRoleId, roleId));
        List<Long> collect = rolePermissions.stream().map(RolePermission::getPermissionId).collect(Collectors.toList());
        collect.stream().forEach(item -> {
            LambdaQueryWrapper<Permission> wrapper1 = new LambdaQueryWrapper<>();
            list.add(permissionService.getOne(wrapper1.eq(Permission::getId,item)).getName());
        });
        return list;
    }

    //给某个角色授权
    @Override
    public void addRolePermission(Long roleId, List<Long> permissionId) {
        List<RolePermission> list = permissionId.stream()
                .map(pid -> {
                    RolePermission rolePermission = new RolePermission();
                    rolePermission.setRoleId(roleId);
                    rolePermission.setPermissionId(pid);
                    return rolePermission;
                })
                .collect(Collectors.toList());

        this.saveBatch(list);
    }
}
