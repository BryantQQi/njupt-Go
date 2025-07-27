package com.atnjupt.sqyxgo.acl.controller;

import com.atnjupt.sqyxgo.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * ClassName:IndexController
 * Package: com.atnjupt.acl.controller
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/9 14:32
 * @Version 1.0
 */
@Api(tags="登录接口")
@RestController
@RequestMapping("/admin/acl/index")
public class IndexController {
    //登录
    @ApiOperation("登录")
    @PostMapping("login")
    public Result login(){
        Map<String,String> map = new HashMap<>();
        map.put("token","token-admin");
        return Result.ok(map);
    }
    //获取用户信息
    @ApiOperation("获取用户信息")
    @GetMapping("info")
    public Result info(){
        Map<String,Object> map = new HashMap<>();
        map.put("name","atguigu");
        map.put("avatar","https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        return Result.ok(map);
    }

    //退出
    @ApiOperation("退出")
    @PostMapping("logout")
    public Result logout(){

        return Result.ok(null);
    }

}
