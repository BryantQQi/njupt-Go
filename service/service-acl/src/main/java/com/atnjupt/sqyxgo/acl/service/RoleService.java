package com.atnjupt.sqyxgo.acl.service;

import com.atnjupt.sqyxgo.model.acl.Role;
import com.atnjupt.sqyxgo.vo.acl.RoleQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * ClassName:RoleService
 * Package: com.atnjupt.acl.service
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/9 14:35
 * @Version 1.0
 * service实际中可以不继承mp的接口实现类，直接放到mapper层用mp，根据实际选取，这里service层和mapper层都写了
 */
public interface RoleService extends IService<Role> {

    //1 角色列表（条件分页查询）
    IPage<Role> selectRolePage(Page<Role> pageParam, RoleQueryVo roleQueryVo);
    //通过adminId查询所有角色列表
    Map<String, Object> getRoleByAdminId(Long adminId);
}
