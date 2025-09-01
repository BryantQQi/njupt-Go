package com.atnjupt.sqyxgo.activity.mapper;


import com.atnjupt.sqyxgo.model.activity.ActivityInfo;
import com.atnjupt.sqyxgo.model.activity.ActivityRule;
import com.atnjupt.sqyxgo.model.activity.ActivitySku;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import feign.Param;

import java.util.List;

/**
 * <p>
 * 活动表 Mapper 接口
 * </p>
 *
 * @author atnjupt
 * @since 2025-07-21
 */
public interface ActivityInfoMapper extends BaseMapper<ActivityInfo> {
    ////查询两张表判断activity_info和activity_sku，判断skuId是否参加过活动，如果参加过，并且在进行中，则在集合中剔除这个skuId，因为
    ////每一个商品只参加一个活动。
    List<Long> selectSkuIdListExist(@Param("skuIdList") List<Long> skuIdList);


    //通过skuId查询到activityRule
    List<ActivityRule> findActivityRule(Long item);
    //先获得cartInfoList中得skuid
    List<ActivitySku> selectCartActivity(List<Long> skuIds);
}
