package com.atnjupt.sqyxgo.activity.service;

import com.atnjupt.sqyxgo.model.activity.ActivityInfo;
import com.atnjupt.sqyxgo.model.activity.ActivityRule;
import com.atnjupt.sqyxgo.model.product.SkuInfo;
import com.atnjupt.sqyxgo.vo.activity.ActivityRuleVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 活动表 服务类
 * </p>
 *
 * @author atnjupt
 * @since 2025-07-21
 */
public interface ActivityInfoService extends IService<ActivityInfo> {
    //分页查询营销活动
    IPage<ActivityInfo> getActivityPage(Page<ActivityInfo> pageInfo);
    //通过营销活动id查询营销活动信息
    ActivityInfo getActivityById(Long id);
    //通过营销活动id查询营销活动规则
    Map<String,Object> getActivityRuleByActivityId(Long id);
    //添加营销活动规则
    void saveActivityRule(ActivityRuleVo activityRuleVo);
    //通过关键字查询商品信息
    List<SkuInfo> findSkuInfoByKeyword(String keyword);
    //获取skuId对应的促销活动标签
    Map<Long, List<String>> findActivity(List<Long> skuIdList);
    //sku对应的促销与优惠券信息
    Map<String, Object> findActivityAndCoupon(Long skuId, Long userId);
}
