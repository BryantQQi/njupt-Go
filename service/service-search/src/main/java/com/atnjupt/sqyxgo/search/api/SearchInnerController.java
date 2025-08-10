package com.atnjupt.sqyxgo.search.api;

import com.atnjupt.sqyxgo.model.search.SkuEs;
import com.atnjupt.sqyxgo.search.service.SkuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * ClassName:SearchInnerController
 * Package: com.atnjupt.sqyxgo.search.api
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/28 17:22
 * @Version 1.0
 */
@RestController
@Api(tags = "search远程调用接口测试")
@RequestMapping("/api/search/sku")
@RequiredArgsConstructor
public class SearchInnerController {

    private final SkuService skuService;

    //获取热销商品
    @ApiOperation("获取热销商品")
    @GetMapping("inner/findHotSkuList")
    public List<SkuEs> findHotSkuList(){
        List<SkuEs> skuEs = skuService.findHotSkuList();
        return skuEs;
    }

    //更新商品incrHotScore
    @GetMapping("inner/incrHotScore/{skuId}")
    public void incrHotScore(@PathVariable(value = "skuId") Long skuId){
        // 调用服务层
        skuService.incrHotScore(skuId);

    }
}
