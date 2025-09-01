package com.atnjupt.sqyxgo.activity.service.impl;

import com.atnjupt.sqyxgo.activity.mapper.ActivityRuleMapper;
import com.atnjupt.sqyxgo.activity.mapper.ActivitySkuMapper;
import com.atnjupt.sqyxgo.activity.service.CouponInfoService;
import com.atnjupt.sqyxgo.client.product.ProductFeignClient;
import com.atnjupt.sqyxgo.enums.ActivityType;
import com.atnjupt.sqyxgo.model.activity.ActivityInfo;
import com.atnjupt.sqyxgo.activity.mapper.ActivityInfoMapper;
import com.atnjupt.sqyxgo.activity.service.ActivityInfoService;
import com.atnjupt.sqyxgo.model.activity.ActivityRule;
import com.atnjupt.sqyxgo.model.activity.ActivitySku;
import com.atnjupt.sqyxgo.model.activity.CouponInfo;
import com.atnjupt.sqyxgo.model.order.CartInfo;
import com.atnjupt.sqyxgo.model.product.SkuInfo;
import com.atnjupt.sqyxgo.model.sys.Ware;
import com.atnjupt.sqyxgo.vo.activity.ActivityRuleVo;
import com.atnjupt.sqyxgo.vo.order.CartInfoVo;
import com.atnjupt.sqyxgo.vo.order.OrderConfirmVo;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.C;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
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
    private final CouponInfoService couponInfoService;

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


    //获取skuId对应的促销活动标签
    @Override
    public Map<Long, List<String>> findActivity(List<Long> skuIdList) {
        Map<Long, List<String>> map = new HashMap<>();
        //通过skuId查询到activityRule
        skuIdList.forEach(item ->{
            List<ActivityRule> activityRuleList = baseMapper.findActivityRule(item);
            if(!CollectionUtils.isEmpty(activityRuleList)){
                List<String> ruleList = new ArrayList<>();
                activityRuleList.forEach(item1 -> {
                    ruleList.add(this.getRuleDesc(item1));
                });
                map.put(item,ruleList);
            }
        });

        return map;
    }

    //sku对应的促销与优惠券信息
    @Override
    public Map<String, Object> findActivityAndCoupon(Long skuId, Long userId) {
        //一个sku只能有一个促销活动，一个活动有多个活动规则（如满赠，满100送10，满500送50）
        List<ActivityRule> activityRuleList = activityRuleMapper.findActivityRule(skuId);

        //获取优惠券信息
        List<CouponInfo> couponInfoList = couponInfoService.findCouponInfo(skuId, userId);

        Map<String, Object> map = new HashMap<>();
        map.put("activityRuleList", activityRuleList);
        map.put("couponInfoList", couponInfoList);
        return map;

    }

    /**
     * 获取购物车规则数据
     * @param cartInfoList
     * @return
     *
     */
    @Override
    public List<CartInfoVo> findCartActivityList(List<CartInfo> cartInfoList) {
        List<CartInfoVo> cartInfoVoList = new ArrayList<>();
        //先获得cartInfoList中得skuid
        List<Long> skuIds = cartInfoList.stream().map(CartInfo::getSkuId).collect(Collectors.toList());
        List<ActivitySku> activitySkuList = baseMapper.selectCartActivity(skuIds);
        //根据活动Id进行分组  map中key是activityId,其中set集合是参与该活动得skuId
        Map<Long, Set<Long>> activityIdToSkuIdListMap = activitySkuList.stream().collect(Collectors.groupingBy(ActivitySku::getActivityId, Collectors.mapping(
                ActivitySku::getSkuId, Collectors.toSet()
        )));
        //得到活动id和其对应得活动规则
        List<Long> activityIdsList = activitySkuList.stream().map(ActivitySku::getActivityId).collect(Collectors.toList());
        Map<Long, List<ActivityRule>> activityIdToActivityRuleListMap = new HashMap<>();
        LambdaQueryWrapper<ActivityRule> activityRuleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        activityRuleLambdaQueryWrapper.in(ActivityRule::getActivityId, activityIdsList).orderByDesc(ActivityRule::getConditionAmount);
        List<ActivityRule> activityRuleList = activityRuleMapper.selectList(activityRuleLambdaQueryWrapper);
        //根据activity对活动规则进行分组
        activityIdToActivityRuleListMap = activityRuleList.stream().collect(Collectors.groupingBy(ActivityRule::getActivityId));
        //记录那些购物项是参与了活动
        Set<Long> activitySkuIdSet = new HashSet<>();
        //遍历activityIdToActivityIdsListMap 得到每个活动对应得购物项，和每个活动对应购物项得总金额，总数量
        if(!CollectionUtils.isEmpty(activityIdToSkuIdListMap)){
            Set<Map.Entry<Long, Set<Long>>> entries = activityIdToSkuIdListMap.entrySet();
            Iterator<Map.Entry<Long, Set<Long>>> iterator = entries.iterator();
            //用迭代器进行遍历map
            while (iterator.hasNext()) {
                Map.Entry<Long, Set<Long>> entry = iterator.next();
                Long activityIdTerm = entry.getKey();
                Set<Long> skuIdsTerm = entry.getValue();
                List<CartInfo> cartInfoListTerm = cartInfoList.stream().filter(cartInfo -> skuIdsTerm.contains(cartInfo.getSkuId())).collect(Collectors.toList());
                //计算购物项总金额和总数目
                BigDecimal computeTotalAmount = this.computeTotalAmount(cartInfoListTerm);
                int computeCartNum = this.computeCartNum(cartInfoListTerm);
                //根据活动id获取活动规则，并且计算最优规则
                List<ActivityRule> activityRuleListTerm = activityIdToActivityRuleListMap.get(activityIdTerm);
                ActivityType activityType = activityRuleListTerm.get(0).getActivityType();
                ActivityRule lastActivityRule = null;
                if(activityType == ActivityType.FULL_REDUCTION){
                    //满减
                    lastActivityRule = this.computeFullReduction(computeTotalAmount, activityRuleListTerm);

                }else {
                    //满量
                    lastActivityRule = this.computeFullDiscount(computeCartNum, computeTotalAmount, activityRuleListTerm);
                }
                //进行封装
                CartInfoVo cartInfoVo =new CartInfoVo();
                cartInfoVo.setCartInfoList(cartInfoListTerm);
                cartInfoVo.setActivityRule(lastActivityRule);
                cartInfoVoList.add(cartInfoVo);
                activitySkuIdSet.addAll(skuIdsTerm);
            }
        }
        //处理没有参加活动sku
        skuIds.removeAll(activitySkuIdSet);
        if(!CollectionUtils.isEmpty(skuIds)){
            Map<Long, CartInfo> skuIdToCartInfoMap = cartInfoList.stream().collect(Collectors.toMap(CartInfo::getSkuId, CartInfo -> CartInfo));
            List<CartInfo>  noActivityCartInfoList = new ArrayList<>();
            for (Long skuId : skuIds) {
                noActivityCartInfoList.add(skuIdToCartInfoMap.get(skuId));
            }
            CartInfoVo noActivityCartInfoVo = new CartInfoVo();
            noActivityCartInfoVo.setActivityRule(null);
            noActivityCartInfoVo.setCartInfoList(noActivityCartInfoList);
            cartInfoVoList.add(noActivityCartInfoVo);

        }
        return cartInfoVoList;
    }

    /**
     * 获取购物车满足条件的促销与优惠券信息
     * @param cartInfoList
     * @param userId
     * @return
     */
    @Override
    public OrderConfirmVo findCartActivityAndCoupon(List<CartInfo> cartInfoList, Long userId) {
        List<CartInfoVo> cartInfoVoList = this.findCartActivityList(cartInfoList);
        //1 获取活动后减少的总金额
        BigDecimal activityReduceAmount = cartInfoVoList.stream().filter(cartInfoVo -> cartInfoVo.getActivityRule() != null).map(cartInfoVo ->
                cartInfoVo.getActivityRule().getReduceAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);
        //2 获取购物车可以使用得优惠卷列表，一次只能使用一张优惠卷
        List<CouponInfo> couponInfoList =couponInfoService.findCartCouponInfoList(cartInfoList,userId);
        //3 获取优惠卷减少的总金额
        BigDecimal couponReduceAmount =new BigDecimal(0);
        if(!CollectionUtils.isEmpty(couponInfoList)){
            couponReduceAmount = couponInfoList.stream().filter(couponInfo -> couponInfo.getIsOptimal().intValue()==1).map(couponInfo ->
                    couponInfo.getAmount()).reduce(BigDecimal.ZERO,BigDecimal::add);
        }
        //4计算购物车原始价格
        BigDecimal originalTotalAmount = cartInfoList.stream().filter(cartInfo -> cartInfo.getIsChecked() == 1).
                map(cartInfo -> cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()))).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        //5最终总金额
        BigDecimal totalAmount = originalTotalAmount.subtract(activityReduceAmount).subtract(couponReduceAmount);

        OrderConfirmVo orderTradeVo = new OrderConfirmVo();
        orderTradeVo.setCarInfoVoList(cartInfoVoList);
        orderTradeVo.setActivityReduceAmount(activityReduceAmount);
        orderTradeVo.setCouponInfoList(couponInfoList);
        orderTradeVo.setCouponReduceAmount(couponReduceAmount);
        orderTradeVo.setOriginalTotalAmount(originalTotalAmount);
        orderTradeVo.setTotalAmount(totalAmount);
        return orderTradeVo;
    }

    /**
     * 计算满量打折最优规则
     * @param totalNum
     * @param activityRuleList //该活动规则skuActivityRuleList数据，已经按照优惠折扣从大到小排序了
     */
    private ActivityRule computeFullDiscount(Integer totalNum, BigDecimal totalAmount, List<ActivityRule> activityRuleList) {
        ActivityRule optimalActivityRule = null;
        //该活动规则skuActivityRuleList数据，已经按照优惠金额从大到小排序了
        for (ActivityRule activityRule : activityRuleList) {
            //如果订单项购买个数大于等于满减件数，则优化打折
            if (totalNum.intValue() >= activityRule.getConditionNum()) {
                BigDecimal skuDiscountTotalAmount = totalAmount.multiply(activityRule.getBenefitDiscount().divide(new BigDecimal("10")));
                BigDecimal reduceAmount = totalAmount.subtract(skuDiscountTotalAmount);
                activityRule.setReduceAmount(reduceAmount);
                optimalActivityRule = activityRule;
                break;
            }
        }
        if(null == optimalActivityRule) {
            //如果没有满足条件的取最小满足条件的一项
            optimalActivityRule = activityRuleList.get(activityRuleList.size()-1);
            optimalActivityRule.setReduceAmount(new BigDecimal("0"));
            optimalActivityRule.setSelectType(1);

            StringBuffer ruleDesc = new StringBuffer()
                    .append("满")
                    .append(optimalActivityRule.getConditionNum())
                    .append("元打")
                    .append(optimalActivityRule.getBenefitDiscount())
                    .append("折，还差")
                    .append(totalNum-optimalActivityRule.getConditionNum())
                    .append("件");
            optimalActivityRule.setRuleDesc(ruleDesc.toString());
        } else {
            StringBuffer ruleDesc = new StringBuffer()
                    .append("满")
                    .append(optimalActivityRule.getConditionNum())
                    .append("元打")
                    .append(optimalActivityRule.getBenefitDiscount())
                    .append("折，已减")
                    .append(optimalActivityRule.getReduceAmount())
                    .append("元");
            optimalActivityRule.setRuleDesc(ruleDesc.toString());
            optimalActivityRule.setSelectType(2);
        }
        return optimalActivityRule;
    }
    /**
     * 计算满减最优规则
     * @param totalAmount
     * @param activityRuleList //该活动规则skuActivityRuleList数据，已经按照优惠金额从大到小排序了
     */
    private ActivityRule computeFullReduction(BigDecimal totalAmount, List<ActivityRule> activityRuleList) {
        ActivityRule optimalActivityRule = null;
        //该活动规则skuActivityRuleList数据，已经按照优惠金额从大到小排序了
        for (ActivityRule activityRule : activityRuleList) {
            //如果订单项金额大于等于满减金额，则优惠金额
            if (totalAmount.compareTo(activityRule.getConditionAmount()) > -1) {
                //优惠后减少金额
                activityRule.setReduceAmount(activityRule.getBenefitAmount());
                optimalActivityRule = activityRule;
                break;
            }
        }
        if(null == optimalActivityRule) {
            //如果没有满足条件的取最小满足条件的一项
            optimalActivityRule = activityRuleList.get(activityRuleList.size()-1);
            optimalActivityRule.setReduceAmount(new BigDecimal("0"));
            optimalActivityRule.setSelectType(1);

            StringBuffer ruleDesc = new StringBuffer()
                    .append("满")
                    .append(optimalActivityRule.getConditionAmount())
                    .append("元减")
                    .append(optimalActivityRule.getBenefitAmount())
                    .append("元，还差")
                    .append(totalAmount.subtract(optimalActivityRule.getConditionAmount()))
                    .append("元");
            optimalActivityRule.setRuleDesc(ruleDesc.toString());
        } else {
            StringBuffer ruleDesc = new StringBuffer()
                    .append("满")
                    .append(optimalActivityRule.getConditionAmount())
                    .append("元减")
                    .append(optimalActivityRule.getBenefitAmount())
                    .append("元，已减")
                    .append(optimalActivityRule.getReduceAmount())
                    .append("元");
            optimalActivityRule.setRuleDesc(ruleDesc.toString());
            optimalActivityRule.setSelectType(2);
        }
        return optimalActivityRule;
    }
    private int computeCartNum(List<CartInfo> cartInfoList) {
        int total = 0;
        for (CartInfo cartInfo : cartInfoList) {
            //是否选中
            if(cartInfo.getIsChecked().intValue() == 1) {
                total += cartInfo.getSkuNum();
            }
        }
        return total;
    }
    private BigDecimal computeTotalAmount(List<CartInfo> cartInfoList) {
        BigDecimal total = new BigDecimal("0");
        for (CartInfo cartInfo : cartInfoList) {
            //是否选中
            if(cartInfo.getIsChecked().intValue() == 1) {
                BigDecimal itemTotal = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
                total = total.add(itemTotal);
            }
        }
        return total;
    }


    //构造规则名称的方法
    private String getRuleDesc(ActivityRule activityRule) {
        //通过不同活动类型来进行活动名字拼接
        ActivityType activityType = activityRule.getActivityType();
        StringBuffer ruleDesc = new StringBuffer();
        if(activityType == ActivityType.FULL_REDUCTION){//满多少元减多少元
            ruleDesc.append("满")
                    .append(activityRule.getConditionAmount())
                    .append("元减")
                    .append(activityRule.getBenefitAmount())
                    .append("元");
        }else {//满量打折
            ruleDesc.append("满")
                    .append(activityRule.getConditionNum())
                    .append("元打")
                    .append(activityRule.getBenefitDiscount())
                    .append("折");
        }
        return ruleDesc.toString();
    }

}
