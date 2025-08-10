package com.atnjupt.sqyxgo.activity.service.impl;

import com.atnjupt.sqyxgo.activity.mapper.CouponRangeMapper;
import com.atnjupt.sqyxgo.client.product.ProductFeignClient;
import com.atnjupt.sqyxgo.common.security.AuthContextHolder;
import com.atnjupt.sqyxgo.enums.CouponRangeType;
import com.atnjupt.sqyxgo.model.activity.CouponInfo;
import com.atnjupt.sqyxgo.activity.mapper.CouponInfoMapper;
import com.atnjupt.sqyxgo.activity.service.CouponInfoService;
import com.atnjupt.sqyxgo.model.activity.CouponRange;
import com.atnjupt.sqyxgo.model.product.Category;
import com.atnjupt.sqyxgo.model.product.SkuInfo;
import com.atnjupt.sqyxgo.vo.activity.CouponRuleVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sun.org.apache.bcel.internal.generic.NEW;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    //优惠卷信息分页查询
    @Override
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
}
