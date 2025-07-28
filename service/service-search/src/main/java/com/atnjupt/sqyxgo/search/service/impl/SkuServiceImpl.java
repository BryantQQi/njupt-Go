package com.atnjupt.sqyxgo.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.atnjupt.sqyxgo.client.product.ProductFeignClient;
import com.atnjupt.sqyxgo.enums.SkuType;
import com.atnjupt.sqyxgo.model.product.Category;
import com.atnjupt.sqyxgo.model.product.SkuInfo;
import com.atnjupt.sqyxgo.model.search.SkuEs;
import com.atnjupt.sqyxgo.search.repository.SkuRepository;
import com.atnjupt.sqyxgo.search.service.SkuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.stereotype.Service;

/**
 * ClassName:SkuServiceImpl
 * Package: com.atnjupt.sqyxgo.search.service.impl
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/20 11:40
 * @Version 1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SkuServiceImpl implements SkuService {

    private final SkuRepository skuRepository;

    private final ProductFeignClient productFeignClient;

    //上架商品
    @Override
    public void upperSkuById(Long skuId) {
        log.info("upperSku："+skuId);
        //通过远程调用获取skuId的所有信息
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        if (skuInfo == null) {
            return;
        }
        Category category = productFeignClient.getCategory(skuInfo.getCategoryId());

        //获取的数据封装到skuEs实体类
        SkuEs skuEs = new SkuEs();
        if (category != null) {
            skuEs.setCategoryId(category.getId());
            skuEs.setCategoryName(category.getName());
        }
        skuEs.setId(skuInfo.getId());
        skuEs.setKeyword(skuInfo.getSkuName()+","+skuEs.getCategoryName());
        skuEs.setWareId(skuInfo.getWareId());
        skuEs.setIsNewPerson(skuInfo.getIsNewPerson());
        skuEs.setImgUrl(skuInfo.getImgUrl());
        skuEs.setTitle(skuInfo.getSkuName());
        if(skuInfo.getSkuType() == SkuType.COMMON.getCode()) {//先按照普通商品进行封装
            skuEs.setSkuType(0);
            skuEs.setPrice(skuInfo.getPrice().doubleValue());
            skuEs.setStock(skuInfo.getStock());
            skuEs.setSale(skuInfo.getSale());
            skuEs.setPerLimit(skuInfo.getPerLimit());
        } else {
            //TODO 待完善-秒杀商品

        }
        log.info("upperSku：保存前");
        //保存到es(落库到es)
        SkuEs save = skuRepository.save(skuEs);
        log.info("upperSku："+ JSON.toJSONString(save));

    }
    //下架商品
    @Override
    public void downSkuInfoById(Long skuId) {
        //skuRepository.deleteById(skuId);
    }


}
