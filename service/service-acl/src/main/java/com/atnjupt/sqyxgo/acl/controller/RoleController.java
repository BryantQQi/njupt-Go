package com.atnjupt.sqyxgo.acl.controller;

import com.atnjupt.sqyxgo.acl.service.RoleService;
import com.atnjupt.sqyxgo.common.result.Result;
import com.atnjupt.sqyxgo.model.acl.Role;
import com.atnjupt.sqyxgo.vo.acl.RoleQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Delete;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * ClassName:RoleController
 * Package: com.atnjupt.acl.controller
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/9 14:32
 * @Version 1.0
 */
@Api(tags="角色接口")
@RestController
@RequestMapping("/admin/acl/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    //1 角色列表（条件分页查询）
    @GetMapping("/{current}/{limit}")
    @ApiOperation("角色列表（条件分页查询）")
    public Result pageList(@PathVariable Long current, @PathVariable Long limit,
                           @ModelAttribute  RoleQueryVo roleQueryVo){
        //1 创建page对象，传入当前页current和每页记录数limit
        Page<Role> pageParam = new Page<>(current,limit);

        //2 调用service方法实现条件分页查询，返回分页对象
        IPage<Role> pageModel = roleService.selectRolePage(pageParam,roleQueryVo);

        return Result.ok(pageModel);
    }
    //2 根据id查询角色
    @ApiOperation("根据id查询角色")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id){
        Role role = roleService.getById(id);
        return Result.ok(role);
    }
    //3 添加角色
    @ApiOperation("添加角色")
    @PutMapping("save")
    public Result save(@RequestBody Role role){
        boolean is_success = roleService.save(role);
        if(is_success){
            return Result.ok("Yes");
        }
        return Result.fail(null);

    }
    //4 修改角色
    @ApiOperation("修改角色")
    @PutMapping("update")
    public Result update(@RequestBody Role role){
        boolean is_success = roleService.updateById(role);
        if(is_success){
            return Result.ok("Yes");
        }
        return Result.fail(null);
    }
    //5 根据id删除角色
    @ApiOperation("根据id删除角色")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id){
        boolean is_success = roleService.removeById(id);
        if(is_success){
            return Result.ok("Yes");
        }
        return Result.fail(null);

    }
    //6 批量删除角色
    @ApiOperation("批量删除角色")
    @DeleteMapping("batchRemove")
    public Result deleteList(@RequestBody ArrayList<Long> idsList){
        boolean is_success = roleService.removeByIds(idsList);
        if(is_success){
            return Result.ok("Yes");
        }
        return Result.fail(null);
    }




}
