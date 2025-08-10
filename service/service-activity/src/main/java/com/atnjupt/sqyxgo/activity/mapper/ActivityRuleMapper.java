package com.atnjupt.sqyxgo.activity.mapper;

import com.atnjupt.sqyxgo.model.activity.ActivityRule;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 优惠规则 Mapper 接口
 * </p>
 *
 * @author atnjupt
 * @since 2025-07-21
 */
public interface ActivityRuleMapper extends BaseMapper<ActivityRule> {
    //一个sku只能有一个促销活动，一个活动有多个活动规则（如满赠，满100送10，满500送50）
    List<ActivityRule> findActivityRule(Long skuId);
}
