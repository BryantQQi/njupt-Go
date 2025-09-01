package com.atnjupt.sqyxgo.activity.service.impl;

import com.atnjupt.sqyxgo.activity.mapper.CouponRangeMapper;
import com.atnjupt.sqyxgo.activity.mapper.CouponUseMapper;
import com.atnjupt.sqyxgo.client.product.ProductFeignClient;
import com.atnjupt.sqyxgo.common.security.AuthContextHolder;
import com.atnjupt.sqyxgo.enums.CouponRangeType;
import com.atnjupt.sqyxgo.enums.CouponStatus;
import com.atnjupt.sqyxgo.model.activity.CouponInfo;
import com.atnjupt.sqyxgo.activity.mapper.CouponInfoMapper;
import com.atnjupt.sqyxgo.activity.service.CouponInfoService;
import com.atnjupt.sqyxgo.model.activity.CouponRange;
import com.atnjupt.sqyxgo.model.activity.CouponUse;
import com.atnjupt.sqyxgo.model.order.CartInfo;
import com.atnjupt.sqyxgo.model.product.Category;
import com.atnjupt.sqyxgo.model.product.SkuInfo;
import com.atnjupt.sqyxgo.vo.activity.CouponRuleVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sun.org.apache.bcel.internal.generic.NEW;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 优惠券信息 服务实现类
 * </p>
 *
 * @author atnjupt
 * @since 2025-07-21
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CouponInfoServiceImpl extends ServiceImpl<CouponInfoMapper, CouponInfo> implements CouponInfoService {

    private final CouponRangeMapper couponRangeMapper;
    private final ProductFeignClient productFeignClient;
    private final CouponInfoMapper couponInfoMapper;
    private final CouponUseMapper couponUseMapper;

    //优惠卷信息分页查询
    @Override
    /*@Transactional(rollbackFor = Exception.class, propagation = Propagation.)
    @Cacheable(value = "couponInfoPage", key = "#pageNum + '-' + #pageSize")
    @CacheEvict(value = "couponInfoPage", allEntries = true)*/
    public IPage<CouponInfo> getPage(Page<CouponInfo> couponInfoPage) {
        Page<CouponInfo> couponInfoPage1 = baseMapper.selectPage(couponInfoPage, null);
        couponInfoPage1.getRecords().stream().forEach(item -> {
            item.setCouponTypeString(item.getCouponType().getComment());
        });
        log.info("userName={}",AuthContextHolder.getUserLoginVoThreadLocal().getNickName());
        couponInfoPage1.getRecords().stream().forEach(item -> {
            item.setRangeTypeString(item.getRangeType().getComment());
        });
        if (couponInfoPage1 == null) {
            return null;
        }
        return couponInfoPage1;
    }
    //通过id查询优惠卷信息
    @Override
    @Cacheable(value = "couponInfoPage", key = "#id")
    public CouponInfo getCouponInfoById(Long id) {
        CouponInfo couponInfo = baseMapper.selectById(id);
        couponInfo.setCouponTypeString(couponInfo.getCouponType().getComment());
        couponInfo.setRangeTypeString(couponInfo.getRangeType().getComment());
        return couponInfo;
    }


    //根据优惠卷id获取优惠券规则信息CouponRuleVo，用于展示,进入页面会自动调用和这个接口
    @Override
    public Map<String,Object> finCouponRuleList(Long id) {
        if(id == null){
            return null;
        }
        Map<String,Object> map = new HashMap<>();
        //第一步先获得优惠卷信息
        CouponInfo couponInfo = baseMapper.selectById(id);

        //第二步根据优惠卷id查询coupon_range查询里面对应range_id
        //如果规则类型SKU  range_id就是skuId值
        ////如果规则类型CATEGORY   range_id就是分类Id值
        LambdaQueryWrapper<CouponRange> couponRangeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        couponRangeLambdaQueryWrapper.eq(CouponRange::getCouponId,id);
        List<CouponRange> couponRanges = couponRangeMapper.selectList(couponRangeLambdaQueryWrapper);
        List<Long> rangeIdList = couponRanges.stream().map(CouponRange::getRangeId).collect(Collectors.toList());
        //第三步分别判断封装不同数据
        ////如果规则类型是SKU，得到skuId，远程调用根据多个skuId值获取对应sku信息
        ////如果规则类型是分类，得到分类Id，远程调用根据多个分类Id值获取对应分类信息
        if(couponInfo.getRangeType() == CouponRangeType.SKU){
            List<SkuInfo> skuInfoList = productFeignClient.findSkuInfoList(rangeIdList);
            map.put("skuInfoList",skuInfoList);
        }if (couponInfo.getRangeType() == CouponRangeType.CATEGORY){
            List<Category> categoryList = productFeignClient.findCategoryListByCategoryIdList(rangeIdList);
            map.put("category",categoryList);
        }else {

        }

        return map;
    }


    //通过优惠卷id为优惠卷新增一个规则，前端展示的在分类里面的添加优惠卷范围按钮
    @Override
    public void saveCouponRule(CouponRuleVo couponRuleVo) {
        //删掉旧数据
        couponRangeMapper.delete(
                new LambdaQueryWrapper<CouponRange>().eq(CouponRange::getCouponId,couponRuleVo.getCouponId())
        );
        //修改基本数据
        CouponInfo couponInfo = baseMapper.selectById(couponRuleVo.getCouponId());

        couponInfo.setRangeType(couponRuleVo.getRangeType());
        couponInfo.setAmount(couponRuleVo.getAmount());
        couponInfo.setConditionAmount(couponRuleVo.getConditionAmount());
        couponInfo.setRangeDesc(couponRuleVo.getRangeDesc());

        baseMapper.updateById(couponInfo);
        //添加新数据
        List<CouponRange> couponRangeList = couponRuleVo.getCouponRangeList();

        couponRangeList.stream().forEach(item -> {
            couponRangeMapper.insert(item);
        });


    }


    //根据关键字获取sku列表，活动使用，就是把商品添加到这个优惠卷面下，表示此商品可以使用这个优惠卷，前端展示的添加活动范围按钮
    @Override
    public List<CouponInfo> findCouponByKeyword(String keyword) {
        LambdaQueryWrapper<CouponInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(CouponInfo::getCouponName,keyword);
        return baseMapper.selectList(wrapper);
    }


    //通过skuId,categoryId,userId获取优惠券信息
    @Override
    public List<CouponInfo> findCouponInfo(Long skuId, Long userId) {
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        Long categoryId = skuInfo.getCategoryId();
        List<CouponInfo> couponInfoList = couponInfoMapper.findCouponInfo(skuId,categoryId,userId);
        return  couponInfoList;
    }

    /**
     * 获取购物车中对应得优惠卷
     * @param cartInfoList
     * @param couponId
     * @return
     */
    @Override
    public CouponInfo findRangeSkuIdList(List<CartInfo> cartInfoList, Long couponId) {
        CouponInfo couponInfo = baseMapper.selectById(couponId);
        if(couponInfo == null){
            return  null;
        }
        List<CouponRange> couponRangeList = couponRangeMapper.selectList(new LambdaQueryWrapper<CouponRange>().eq(CouponRange::getCouponId, couponId));
        Map<Long, List<Long>> couponIdToSkuId = this.findCouponIdToSkuId(cartInfoList, couponRangeList);
        List<Long> skuIds = null;
        Iterator<Map.Entry<Long, List<Long>>> iterator = couponIdToSkuId.entrySet().iterator();
        if(iterator.hasNext()){
            Map.Entry<Long, List<Long>> next = iterator.next();
            skuIds = next.getValue();
        }
        couponInfo.setSkuIdList(skuIds);
        return  couponInfo;
    }

    /**
     * 更新优惠卷的使用状态
     * @param couponId
     * @param userId
     * @param orderId
     * @return
     */
    @Override
    public boolean updateCouponUserStatus(Long couponId, Long userId, Long orderId) {
        LambdaUpdateWrapper<CouponUse> couponUseLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        couponUseLambdaUpdateWrapper.eq(CouponUse::getCouponId, couponId)
                .eq(CouponUse::getUserId, userId).eq(CouponUse::getOrderId,orderId).set(CouponUse::getCouponStatus, CouponStatus.USED);
        int rows = couponUseMapper.update(null, couponUseLambdaUpdateWrapper);
        if(rows == 1){
            return true;
        }
        return false;
    }
    /**
     * 查询购物车中的可用优惠卷
     * @param cartInfoList
     * @param userId
     * @return
     */
    @Override
    public List<CouponInfo> findCartCouponInfoList(List<CartInfo> cartInfoList, Long userId) {
        //1先查询该用户有的全部优惠卷
        List<CouponInfo> userAllCouponInfoList= couponInfoMapper.findCouponInfoByUserId(userId);
        if(CollectionUtils.isEmpty(userAllCouponInfoList)){
            return new ArrayList<CouponInfo>();
        }
        List<Long> couponIdsList = userAllCouponInfoList.stream().map(CouponInfo::getId).collect(Collectors.toList());
        LambdaQueryWrapper<CouponRange> couponRangeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        couponRangeLambdaQueryWrapper.in(CouponRange::getCouponId,couponIdsList);
        //2获取该用户优惠卷的使用范围
        List<CouponRange> couponRangeList = couponRangeMapper.selectList(couponRangeLambdaQueryWrapper);
        //3 获取优惠卷对应skuId  key 为couponId,value 为对应skuId集合
        Map<Long, List<Long>> couponIdToSkuIdMap= this.findCouponIdToSkuId(cartInfoList,couponRangeList);
        //4 优惠后减少金额
        BigDecimal reduceAmount = new BigDecimal("0");
        //5记录最优优惠券
        CouponInfo optimalCouponInfo = null;
        for (CouponInfo couponInfo : userAllCouponInfoList) {
            if(CouponRangeType.ALL == couponInfo.getRangeType()) {
                //全场通用
                //判断是否满足优惠使用门槛
                //计算购物车商品的总价
                BigDecimal totalAmount = this.computeTotalAmount(cartInfoList);
                if(totalAmount.subtract(couponInfo.getConditionAmount()).doubleValue() >= 0){
                    couponInfo.setIsSelect(1);
                } else {
                    //优惠券id对应的满足使用范围的购物项skuId列表
                    List<Long> skuIdList = couponIdToSkuIdMap.get(couponInfo.getId());
                    //当前满足使用范围的购物项
                    List<CartInfo> currentCartInfoList = cartInfoList.stream().filter(cartInfo -> skuIdList.contains(cartInfo.getSkuId())).collect(Collectors.toList());
                    BigDecimal totalAmount1 = computeTotalAmount(currentCartInfoList);
                    if(totalAmount1.subtract(couponInfo.getConditionAmount()).doubleValue() >= 0){
                        couponInfo.setIsSelect(1);
                    }
                }
                if (couponInfo.getIsSelect().intValue() == 1 && couponInfo.getAmount().subtract(reduceAmount).doubleValue() > 0) {
                    reduceAmount = couponInfo.getAmount();
                    optimalCouponInfo = couponInfo;
                }
            }
        }

        if(null != optimalCouponInfo) {
            optimalCouponInfo.setIsOptimal(1);
        }
        return userAllCouponInfoList;

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


    //获取优惠卷对应skuId  key 为couponId,value 为对应skuId集合
    private Map<Long, List<Long>> findCouponIdToSkuId(List<CartInfo> cartInfoList, List<CouponRange> couponRangeList) {
        Map<Long, List<Long>> couponIdToSkuIdMap = new HashMap<>();
        //把couponRangList 根据couponId进行分组
        Map<Long, List<CouponRange>> couponIdToCouponRangMap = couponRangeList.stream().collect(Collectors.groupingBy(CouponRange::getCouponId));
        Iterator<Map.Entry<Long, List<CouponRange>>> iterator = couponIdToCouponRangMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, List<CouponRange>> entry = iterator.next();
            Long couponIdTerm = entry.getKey();
            List<CouponRange> couponRangeListTerm = entry.getValue();
            //属于该优惠卷的skuId
            Set<Long> skuIdsTerm = new HashSet<>();
            for (CartInfo cartInfo : cartInfoList) {
                for (CouponRange couponRange : couponRangeListTerm) {
                    if(couponRange.getRangeType()==CouponRangeType.SKU
                            && cartInfo.getSkuId().longValue()==couponRange.getRangeId().longValue()){
                        skuIdsTerm.add(cartInfo.getSkuId());
                    }else if(couponRange.getRangeType()==CouponRangeType.CATEGORY
                            && cartInfo.getCategoryId().longValue()==couponRange.getRangeId()){
                        skuIdsTerm.add(cartInfo.getSkuId());
                    }
                }
            }
            //进行封装
            couponIdToSkuIdMap.put(couponIdTerm,new ArrayList<>(skuIdsTerm));
        }
        return couponIdToSkuIdMap;
    }
}
