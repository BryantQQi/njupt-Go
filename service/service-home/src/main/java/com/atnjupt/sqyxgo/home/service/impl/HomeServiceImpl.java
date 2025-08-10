package com.atnjupt.sqyxgo.home.service.impl;

import com.atnjupt.sqyxgo.client.product.ProductFeignClient;
import com.atnjupt.sqyxgo.client.search.SearchFeignClient;
import com.atnjupt.sqyxgo.client.user.UserFeignClient;
import com.atnjupt.sqyxgo.home.service.HomeService;
import com.atnjupt.sqyxgo.model.product.Category;
import com.atnjupt.sqyxgo.model.product.SkuInfo;
import com.atnjupt.sqyxgo.model.search.SkuEs;
import com.atnjupt.sqyxgo.vo.user.LeaderAddressVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassName:HomeServiceImpl
 * Package: com.atnjupt.sqyxgo.home.service.impl
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/25 17:34
 * @Version 1.0
 */
@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {

    private final ProductFeignClient productFeignClient;
    private final UserFeignClient userFeignClient;
    private final SearchFeignClient searchFeignClient;
    //获取首页数据
    @Override
    public Map<String, Object> getHomeData(Long userId) {
        //当前登录用户的提货点地址信息
        //远程调用service-user接口
        LeaderAddressVo leaderAddressVo = userFeignClient.getLeaderAddressVoByUserId(userId);
        //新人专享商品
        //远程调用service-product接口
        List<SkuInfo> newPersonSkuInfoList = productFeignClient.findNewPersonSkuInfoList();


        //获取分类信息
        //远程调用service-product接口
        List<Category> categoryList = productFeignClient.findAllCategoryList();
        //首页秒杀数据
        //TODO


        //热销商品
        //远程调用service-search接口
        List<SkuEs> hotSkuList = searchFeignClient.findHotSkuList();
        //封装到map返回
        Map<String,Object> map = new HashMap<>();
        map.put("leaderAddressVo",leaderAddressVo);
        map.put("newPersonSkuInfoList",newPersonSkuInfoList);
        map.put("categoryList",categoryList);
        map.put("hotSkuList",hotSkuList);
        return map;
    }
}
