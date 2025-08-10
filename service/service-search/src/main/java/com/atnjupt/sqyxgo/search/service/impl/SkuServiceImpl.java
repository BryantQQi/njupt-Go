package com.atnjupt.sqyxgo.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.atnjupt.sqyxgo.client.activity.ActivityFeignClient;
import com.atnjupt.sqyxgo.client.product.ProductFeignClient;
import com.atnjupt.sqyxgo.common.security.AuthContextHolder;
import com.atnjupt.sqyxgo.enums.SkuType;
import com.atnjupt.sqyxgo.model.product.Category;
import com.atnjupt.sqyxgo.model.product.SkuInfo;
import com.atnjupt.sqyxgo.model.search.SkuEs;
import com.atnjupt.sqyxgo.search.repository.SkuRepository;
import com.atnjupt.sqyxgo.search.service.SkuService;
import com.atnjupt.sqyxgo.vo.search.SkuEsQueryVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final RedisTemplate redisTemplate;
    private final ProductFeignClient productFeignClient;
    private final ActivityFeignClient activityFeignClient;

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


    //获取热销商品
    @Override
    public List<SkuEs> findHotSkuList() {

        Pageable pageable = PageRequest.of(0,10);

        Page<SkuEs> pageModel = skuRepository.findByOrderByHotScoreDesc(pageable);
        List<SkuEs> skuEsList = pageModel.getContent();
        return skuEsList;
    }

    //搜索商品
    @Override
    public Page<SkuEs> search(Pageable pageable, SkuEsQueryVo skuEsQueryVo) {
        //先设置SkuEsQueryVo的值，有可能为空，CategoryId不会空，因为是通过分类查询的分类下的商品
        skuEsQueryVo.setWareId(AuthContextHolder.getWareIdThreadLocal());
        Page<SkuEs> skuEsPage = null;
        //通过判断keyword是否为空来查询
        ////如果keyword为空，通过CategoryId和WareId查询商品信息
        ////如果keyword不为空，通过WareId和keyword查询商品信息
        if(StringUtils.isEmpty(skuEsQueryVo.getKeyword())){
            skuEsPage = skuRepository.findByCategoryIdAndWareId(skuEsQueryVo.getCategoryId(),skuEsQueryVo.getWareId(),pageable);
        }else {
            skuEsPage = skuRepository.findByKeywordAndWareId(skuEsQueryVo.getKeyword(),skuEsQueryVo.getWareId(),pageable);
        }
        List<SkuEs> skuEsList = skuEsPage.getContent();
        List<Long> skuIdList = skuEsList.stream().map(SkuEs::getId).collect(Collectors.toList());
        //判断是否商品是否参与活动，获取skuId对应的促销活动标签
        if(!CollectionUtils.isEmpty(skuIdList)){
            Map<Long,List<String>> skuIdToRuleListMap = activityFeignClient.findActivity(skuIdList);
            if(skuIdToRuleListMap != null){
                skuEsList.forEach(item -> item.setRuleList(skuIdToRuleListMap.get(item.getId())));
            }
        }


        return skuEsPage;
    }


    //更新商品incrHotScore
    @Override
    public void incrHotScore(Long skuId) {
        String hotKey = "hotScore";
        Double hotScore = redisTemplate.opsForZSet().incrementScore(hotKey,"skuId" + skuId,1);
        if(hotScore%10==0){
            // 更新es
            Optional<SkuEs> optional = skuRepository.findById(skuId);
            SkuEs skuEs = optional.get();
            skuEs.setHotScore(Math.round(hotScore));
            skuRepository.save(skuEs);
           

        }

    }


}
