package com.atnjupt.sqyxgo.acl.service;

import com.atnjupt.sqyxgo.model.acl.Admin;
import com.atnjupt.sqyxgo.vo.acl.AdminQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * ClassName:AdminService
 * Package: com.atnjupt.sqyxgo.acl.service
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/9 17:37
 * @Version 1.0
 */
public interface AdminService extends IService<Admin> {
    //1 用户列表（条件分页查询）
    IPage<Admin> selectAdminPage(Page<Admin> pageParem, AdminQueryVo adminQueryVo);
}
