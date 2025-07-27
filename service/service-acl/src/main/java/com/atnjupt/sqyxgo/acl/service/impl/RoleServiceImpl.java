package com.atnjupt.sqyxgo.acl.service.impl;

import com.atnjupt.sqyxgo.acl.mapper.RoleMapper;
import com.atnjupt.sqyxgo.acl.service.AdminRoleService;
import com.atnjupt.sqyxgo.acl.service.RoleService;
import com.atnjupt.sqyxgo.model.acl.AdminRole;
import com.atnjupt.sqyxgo.model.acl.Role;
import com.atnjupt.sqyxgo.model.sys.Ware;
import com.atnjupt.sqyxgo.vo.acl.RoleQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ClassName:RoleServiceImpl
 * Package: com.atnjupt.acl.service.impl
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/9 14:38
 * @Version 1.0
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    private final AdminRoleService adminRoleService;
    //1 角色列表（条件分页查询）
    @Override
    public IPage<Role> selectRolePage(Page<Role> pageParam, RoleQueryVo roleQueryVo) {
        //获取条件值
        String roleName = roleQueryVo.getRoleName();
        //创建mp条件对象
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        //判断条件值是否为空不为封装查询条件
        if(!StringUtils.isEmpty(roleName)){
            wrapper.like(Role::getRoleName,roleName);
        }
        //调用方法实现条件分页查询
        IPage<Role> rolePage = baseMapper.selectPage(pageParam, wrapper);
        //返回分页对象
        return rolePage;
    }

    //通过adminId查询所有角色列表
    @Override
    public Map<String, Object> getRoleByAdminId(Long adminId) {
        //1 获取所有角色
        List<Role> allRoleList =  baseMapper.selectList(null);
        //2 通过adminId获得为用户分配的角色
        //2.1操作admin_role表，查询出admin_id字段为adminId的所有记录，返回的是一个List<AdminRole>
        LambdaQueryWrapper<AdminRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminRole::getAdminId,adminId);
        List<AdminRole> adminRoleList = adminRoleService.list(wrapper);
        //2.2获取为adminId分配的roleId集合，即 将List<AdminRole>转换成List<Long>
        List<Long> roleList = adminRoleList.stream()
                .map(AdminRole::getRoleId)
                .collect(Collectors.toList());
        //2.3创建新的list集合，用于存储用户配置角色
        List<Role> assignRoleList = new ArrayList<>();
        //2.4遍历所有角色列表allRolesList，得到每个角色
        //判断所有角色里面是否包含已经分配角色id，封装到2.3里面新的list集合
        for (Role role : allRoleList) {
            //判断assignRoleList里面的
            if (roleList.contains(role.getId())){
                assignRoleList.add(role);
            }
        }
        //通过roleid去查询role表获得role_name
        Map<String,Object> map = new HashMap<>();
        map.put("角色列表",allRoleList);
        map.put("用户所拥有的角色",assignRoleList);

        return map;
    }


}
