package com.atnjupt.sqyxgo.acl.service.impl;

import com.atnjupt.sqyxgo.acl.mapper.PermissionMapper;
import com.atnjupt.sqyxgo.acl.service.PermissionService;
import com.atnjupt.sqyxgo.acl.utils.PermissonHelper;
import com.atnjupt.sqyxgo.model.acl.Permission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ClassName:PermissionServiceImpl
 * Package: com.atnjupt.sqyxgo.acl.service.impl
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/10 15:37
 * @Version 1.0
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {



    //获取权限(菜单/功能)列表
    @Override
    public List<Permission> queryAllPermisson() {
        //先把所有对象取出来，封装进List
        List<Permission> allPermissonList = baseMapper.selectList(null);

        //格式转换
        List<Permission> result = PermissonHelper.buildPermisson(allPermissonList);

        return result;
    }


    //递归删除一个权限项
    @Override
    public Boolean removeChildById(Long id) {
        List<Long> deleteIdList = new ArrayList<>();
        this.selectChildListById(id,deleteIdList);
        deleteIdList.add(id);
        boolean is_success = this.removeByIds(deleteIdList);
        return is_success;

    }

    private void selectChildListById(Long id,List<Long> deleteIdList){
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();

        List<Permission> permissionsList = baseMapper.selectList(wrapper.eq(Permission::getPid, id));
        permissionsList.stream().forEach(item -> {
            deleteIdList.add(item.getId());
            selectChildListById(item.getId(),deleteIdList);
        });

    }
}
