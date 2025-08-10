package com.atnjupt.sqyxgo.home.controller;

import com.atnjupt.sqyxgo.common.result.Result;
import com.atnjupt.sqyxgo.common.security.AuthContextHolder;
import com.atnjupt.sqyxgo.home.service.ItemService;
import com.atnjupt.sqyxgo.home.service.impl.ItemServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * ClassName:ItemApiController
 * Package: com.atnjupt.sqyxgo.home.controller
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/29 21:08
 * @Version 1.0
 */
@Api(tags = "商品详情")
@RestController
@RequestMapping("api/home")
@RequiredArgsConstructor
public class ItemApiController {

    private final ItemService itemService;

    //通过skuid获取sku详细信息
    @ApiOperation("通过skuid获取sku详细信息")
    @GetMapping("item/{id}")
    public Result item(@PathVariable(value = "id") Long skuId){
        Long userId = AuthContextHolder.getUserIdThreadLocal();
        Map<String,Object> map = itemService.item(userId,skuId);
        return Result.ok(map);
    }

}
