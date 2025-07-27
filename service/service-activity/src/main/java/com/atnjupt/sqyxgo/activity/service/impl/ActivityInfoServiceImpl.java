package com.atnjupt.sqyxgo.activity.service.impl;

import com.atnjupt.sqyxgo.activity.mapper.ActivityRuleMapper;
import com.atnjupt.sqyxgo.activity.mapper.ActivitySkuMapper;
import com.atnjupt.sqyxgo.client.product.ProductFeignClient;
import com.atnjupt.sqyxgo.enums.ActivityType;
import com.atnjupt.sqyxgo.model.activity.ActivityInfo;
import com.atnjupt.sqyxgo.activity.mapper.ActivityInfoMapper;
import com.atnjupt.sqyxgo.activity.service.ActivityInfoService;
import com.atnjupt.sqyxgo.model.activity.ActivityRule;
import com.atnjupt.sqyxgo.model.activity.ActivitySku;
import com.atnjupt.sqyxgo.model.product.SkuInfo;
import com.atnjupt.sqyxgo.model.sys.Ware;
import com.atnjupt.sqyxgo.vo.activity.ActivityRuleVo;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.C;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * 活动表 服务实现类
 * </p>
 *
 * @author atnjupt
 * @since 2025-07-21
 */
@Service
@RequiredArgsConstructor
public class ActivityInfoServiceImpl extends ServiceImpl<ActivityInfoMapper, ActivityInfo> implements ActivityInfoService {


    private final ActivityRuleMapper activityRuleMapper;
    private final ActivitySkuMapper activitySkuMapper;
    private final ProductFeignClient productFeignClient;

    //分页查询营销活动
    @Override
    public IPage<ActivityInfo> getActivityPage(Page<ActivityInfo> pageInfo) {
        Page<ActivityInfo> activityInfoPage = baseMapper.selectPage(pageInfo, null);
        List<ActivityInfo> records = activityInfoPage.getRecords();
        records.stream().forEach(item ->{
            ActivityType type = item.getActivityType();
            if(type != null){
                item.setActivityTypeString(type.getComment());
            }
        });
        return activityInfoPage;
    }
    //通过营销活动id查询营销活动信息
    @Override
    public ActivityInfo getActivityById(Long id) {
        ActivityInfo activityInfo = baseMapper.selectById(id);
        activityInfo.setActivityTypeString(activityInfo.getActivityType().getComment());
        return activityInfo;
    }






    //营销活动规则相关接口
    //通过营销活动id查询营销活动规则
    @Override
    public Map<String,Object> getActivityRuleByActivityId(Long id) {
        Map<String,Object> map = new HashMap<>();
        //通过活动id，查出这个活动规则列表，一个活动可能好几种规则
        LambdaQueryWrapper<ActivityRule> wrapperRule = new LambdaQueryWrapper<>();
        wrapperRule.eq(ActivityRule::getActivityId,id);
        List<ActivityRule> activityRuleList = activityRuleMapper.selectList(wrapperRule);
        map.put("activityRuleList",activityRuleList);
        //通过活动id，查询出参与的商品skuId
        LambdaQueryWrapper<ActivitySku> wrapperActivitySku = new LambdaQueryWrapper<>();
        wrapperActivitySku.eq(ActivitySku::getActivityId,id);
        List<Long> skuIdList = activitySkuMapper.selectList(wrapperActivitySku)
                .stream().map(ActivitySku::getSkuId).collect(Collectors.toList());
        if (skuIdList != null && !skuIdList.isEmpty()) {
            List<SkuInfo> skuInfoList = productFeignClient.findSkuInfoList(skuIdList);
            map.put("skuInfoList",skuInfoList);
        }

        return map;
    }
    //添加营销活动规则
    @Override
    public void saveActivityRule(ActivityRuleVo activityRuleVo) {
        Long activityId = activityRuleVo.getActivityId();
        //把之前的数据先删掉再添加
        LambdaQueryWrapper<ActivityRule> activityRuleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        activityRuleLambdaQueryWrapper.eq(ActivityRule::getActivityId,activityId);
        activityRuleMapper.delete(activityRuleLambdaQueryWrapper);

        activitySkuMapper.delete(
                new LambdaQueryWrapper<ActivitySku>().eq(ActivitySku::getActivityId,activityId)
        );

        //把数据全部取出来
        List<ActivityRule> activityRuleList = activityRuleVo.getActivityRuleList();
        List<ActivitySku> activitySkuList = activityRuleVo.getActivitySkuList();

        List<Long> couponIdList = activityRuleVo.getCouponIdList();
        //添加活动规则
        ActivityInfo activityInfo = baseMapper.selectById(activityId);
        activityRuleList.stream().forEach(item -> {
            item.setActivityId(activityId);
            item.setActivityType(activityInfo.getActivityType());
            activityRuleMapper.insert(item);
        });
        //添加活动参与的商品
        activitySkuList.stream().forEach(item -> {
            item.setActivityId(activityId);
            activitySkuMapper.insert(item);
        });

    }
    //通过关键字查询商品信息
    @Override
    public List<SkuInfo> findSkuInfoByKeyword(String keyword) {

        //第一步根据关键字查询sku匹配内容列表
        ////(1)service-product模块创建接口据关键字查询sku匹配内容列表
        ////(2)service-activity远程调用得到sku内容列表
        List<SkuInfo> skuInfoByKeywordList = productFeignClient.findSkuInfoByKeyword(keyword);
        //如果根据关键字查询不到匹配的内容，直接返回空集合
        if (skuInfoByKeywordList == null || skuInfoByKeywordList.isEmpty()) {
            return null;
        }

        List<Long> skuIdList = skuInfoByKeywordList.stream().map(SkuInfo::getId).collect(Collectors.toList());

        //第二步判断添加商品之前是否参加过活动，如果之前参加过，活动正在进行中，则排除商品
        ////(1)查询两张表判断activity_info和activity_sku，编写SQL语句实现
        List<Long> existSkuIdList = baseMapper.selectSkuIdListExist(skuIdList);
        ////（2）判断逻辑处理
        List<SkuInfo> collect = skuInfoByKeywordList.stream().
                filter(item -> !existSkuIdList.contains(item.getId())).collect(Collectors.toList());
        return collect;
    }
}
