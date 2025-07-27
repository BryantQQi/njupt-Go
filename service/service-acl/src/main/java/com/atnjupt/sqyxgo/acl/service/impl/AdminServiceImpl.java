package com.atnjupt.sqyxgo.acl.service.impl;

import com.atnjupt.sqyxgo.acl.mapper.AdminMapper;
import com.atnjupt.sqyxgo.acl.service.AdminService;
import com.atnjupt.sqyxgo.model.acl.Admin;
import com.atnjupt.sqyxgo.vo.acl.AdminQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * ClassName:AdminServiceImpl
 * Package: com.atnjupt.sqyxgo.acl.service.impl
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/9 17:38
 * @Version 1.0
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService{


    //1 用户列表（条件分页查询）
    @Override
    public IPage<Admin> selectAdminPage(Page<Admin> pageParem, AdminQueryVo adminQueryVo) {

        //获取条件值
        String userName = adminQueryVo.getUsername();
        String name = adminQueryVo.getName();
        //创建条件查询构造器对象
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        //判断条件值是否为空不为封装查询条件
        if (!StringUtils.isEmpty(userName)){
            wrapper.eq(Admin::getUsername,userName);
        }
        if(!StringUtils.isEmpty(name)){
            wrapper.like(Admin::getName,name);
        }
        //调用方法实现条件分页查询
        IPage<Admin> adminPage = baseMapper.selectPage(pageParem, wrapper);
        //返回分页对象

        return adminPage;
    }
}
