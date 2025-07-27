package com.atnjupt.sqyxgo.home.controller;

import com.atnjupt.sqyxgo.common.result.Result;
import com.atnjupt.sqyxgo.common.security.AuthContextHolder;
import com.atnjupt.sqyxgo.home.service.HomeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * ClassName:HomeApiController
 * Package: com.atnjupt.sqyxgo.home.controller
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/25 17:31
 * @Version 1.0
 */
@Api(tags = "首页面显示接口测试")
@RestController
@RequestMapping("api/home")
@RequiredArgsConstructor
public class HomeApiController {

    private final HomeService homeService;

    //获取首页数据
    @ApiOperation("获取首页数据")
    @GetMapping("index")
    public Result index(HttpRequestHandler request){
        Long userId = AuthContextHolder.getUserIdThreadLocal();

        Map<String,Object> map = homeService.getHomeData(userId);

        return Result.ok(null);
    }

    //获取分类


    //商品详情
}
