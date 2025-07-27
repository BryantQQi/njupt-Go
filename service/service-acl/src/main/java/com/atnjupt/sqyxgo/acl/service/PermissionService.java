package com.atnjupt.sqyxgo.acl.service;

import com.atnjupt.sqyxgo.model.acl.Permission;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * ClassName:PermissionService
 * Package: com.atnjupt.sqyxgo.acl.service
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/10 15:37
 * @Version 1.0
 */
public interface PermissionService extends IService<Permission> {
    //获取权限(菜单/功能)列表
    List<Permission> queryAllPermisson();
    //递归删除一个权限项
    Boolean removeChildById(Long id);
}
