package com.atnjupt.sqyxgo.acl.controller;

import com.atnjupt.sqyxgo.acl.service.PermissionService;
import com.atnjupt.sqyxgo.acl.service.RolePermissionService;
import com.atnjupt.sqyxgo.common.result.Result;
import com.atnjupt.sqyxgo.model.acl.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * ClassName:PermissionController
 * Package: com.atnjupt.sqyxgo.acl.controller
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/10 15:35
 * @Version 1.0
 */
@RestController
@Api(tags="菜单管理")
@RequiredArgsConstructor
@RequestMapping("/admin/acl/permission")
public class PermissionController {

    private final PermissionService permissionService;

    private final RolePermissionService rolePermissionService;

    //获取权限(菜单/功能)列表
    @ApiOperation("获取权限(菜单/功能)列表")
    @GetMapping
    public Result get(){
        //List<Permission> list = permissionService.list();
        List<Permission> list = permissionService.queryAllPermisson();
        return Result.ok(list);
    }

    //保存一个权限项
    @ApiOperation("保存一个权限项")
    @PostMapping("/save")
    public Result save(@RequestBody Permission permission){
        permissionService.save(permission);
        return Result.ok(permission);
    }

    //递归删除一个权限项
    @ApiOperation("递归删除一个权限项")
    @DeleteMapping("/remove/{id}")
    public Result remove(@PathVariable Long id){
        Boolean is_success = permissionService.removeChildById(id);
        return Result.ok(null);
    }

    //修改一个权限项
    @ApiOperation("修改一个权限项")
    @PutMapping("/update")
    public Result update(@RequestBody Permission permission){
        permissionService.updateById(permission);
        return Result.ok(permission);
    }

    //查看某个角色的权限列表
    @ApiOperation("查看某个角色的权限列表")
    @GetMapping("/toAssign/{roleId}")
    public Result toAssign(@PathVariable Long roleId){
        List<String> list = rolePermissionService.getRolePermissionList(roleId);
        return Result.ok(list);
    }
    //给某个角色授权
    @ApiOperation("给某个角色授权")
    @PostMapping("/doAssign")
    public Result doAssign(@RequestParam Long roleId,@RequestParam List<Long> permissionId){
        rolePermissionService.addRolePermission(roleId,permissionId);
        return Result.ok(null);
    }

}


