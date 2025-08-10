package com.atnjupt.sqyxgo.home.controller;

import com.atnjupt.sqyxgo.client.product.ProductFeignClient;
import com.atnjupt.sqyxgo.common.result.Result;
import com.atnjupt.sqyxgo.home.service.HomeService;
import com.atnjupt.sqyxgo.model.search.SkuEs;
import com.atnjupt.sqyxgo.vo.search.SkuEsQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName:CategoryApiController
 * Package: com.atnjupt.sqyxgo.home.controller
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/28 17:46
 * @Version 1.0
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/home")
@Api(tags = "home商品分类测试接口")
public class CategoryApiController {

    private final ProductFeignClient productFeignClient;
    private final HomeService homeService;

    @ApiOperation(value = "获取分类信息")
    @GetMapping("category")
    public Result index() {
        return Result.ok(productFeignClient.findAllCategoryList());
    }



}
