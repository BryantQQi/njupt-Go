package com.atnjupt.sqyxgo.acl.controller;

import com.atnjupt.sqyxgo.acl.service.AdminRoleService;
import com.atnjupt.sqyxgo.acl.service.AdminService;
import com.atnjupt.sqyxgo.acl.service.RoleService;
import com.atnjupt.sqyxgo.common.result.Result;
import com.atnjupt.sqyxgo.common.utils.MD5;
import com.atnjupt.sqyxgo.model.acl.Admin;
import com.atnjupt.sqyxgo.vo.acl.AdminQueryVo;
import com.atnjupt.sqyxgo.vo.user.UserQueryVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * ClassName:AdminController
 * Package: com.atnjupt.sqyxgo.acl.controller
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/9 17:29
 * @Version 1.0
 */
@RequestMapping("/admin/acl/user")
@RequiredArgsConstructor
@RestController
@Api(tags = "用户接口")
public class AdminController {

    private final AdminService adminService;
    private final AdminRoleService adminRoleService;
    private final RoleService roleService;

    //1 用户列表（条件分页查询）
    @ApiOperation("用户列表（条件分页查询）")
    @GetMapping("{current}/{limit}")
    public Result get(@PathVariable Long current, @PathVariable Long limit,
                      @ModelAttribute AdminQueryVo adminQueryVo){
        //创建分页对象
        Page<Admin> pageParem = new Page<>(current,limit);

        //调用service方法实现分页查询
        IPage<Admin> pageModel = adminService.selectAdminPage(pageParem,adminQueryVo);
        //返回
        return Result.ok(pageModel);
    }

    //2 用户添加
    @ApiOperation("用户添加")
    @PutMapping("save")
    public Result save(@RequestBody Admin admin){
        //获取用户密码
        String passWord = admin.getPassword();
        //对密码进行加密
        String encryptPassWord = MD5.encrypt(passWord);
        //将加密的密码保存到admin
        admin.setPassword(encryptPassWord);

        boolean is_success = adminService.save(admin);
        if (is_success){
            return Result.ok("添加成功");
        }
        return Result.fail("添加失败");
    }
    //3 根据id查询用户
    @ApiOperation("根据id查询用户")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id){
        Admin admin = adminService.getById(id);
        return Result.ok(admin);
    }
    //4 修改用户
    @ApiOperation("修改用户")
    @PutMapping("update")
    public Result updateById(@RequestBody Admin user){
        boolean is_success = adminService.updateById(user);
        if (is_success){
            return Result.ok("修改成功");
        }
        return Result.fail("修改失败");
    }


    //6 通过id删除用户
    @ApiOperation("通过id删除用户")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id){
        boolean is_success = adminService.removeById(id);
        if (is_success){
            return Result.ok("删除成功");
        }
        return Result.fail("删除失败");
    }
    //7 批量删除用户
    @ApiOperation("批量删除用户")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> ids){
        boolean is_success = adminService.removeByIds(ids);
        if (is_success){
            return Result.ok("删除成功");
        }
        return Result.fail("删除失败");
    }


    //5 查询所有角色列表,根据用户id查询用户已经分配的角色列表
    @ApiOperation("获取用户所有角色")
    @GetMapping("toAssign/{adminId}")
    public Result getAdminRole(@PathVariable Long adminId){
        //通过adminId查询所有角色列表
        Map<String,Object> map = roleService.getRoleByAdminId(adminId);
        //返回的map集合包含两部分数据，一部分是所有角色的角色列表，一部分是为adminId分配的角色
        return Result.ok(map);
    }
    //8 为用户分配角色
    @ApiOperation("为用户分配角色")
    @PostMapping("doAssign")
    public Result doAssign(@RequestParam List<Long> roleId,@RequestParam Long adminId){
        boolean is_success = adminRoleService.assignAdminRole(roleId,adminId);
        if(is_success){
            return Result.ok("分配成功");
        }
        return Result.fail("分配失败");
    }
}
