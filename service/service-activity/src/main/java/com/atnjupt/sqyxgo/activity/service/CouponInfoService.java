package com.atnjupt.sqyxgo.activity.service;

import com.atnjupt.sqyxgo.model.activity.CouponInfo;
import com.atnjupt.sqyxgo.vo.activity.CouponRuleVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 优惠券信息 服务类
 * </p>
 *
 * @author atnjupt
 * @since 2025-07-21
 */
public interface CouponInfoService extends IService<CouponInfo> {
    //优惠卷信息分页查询
    IPage<CouponInfo> getPage(Page<CouponInfo> couponInfoPage);
    //通过id查询优惠卷信息
    CouponInfo getCouponInfoById(Long id);
    //根据优惠卷id获取优惠券规则信息CouponRuleVo，用于展示,进入页面会自动调用和这个接口
    Map<String,Object> finCouponRuleList(Long id);
    //通过优惠卷id为优惠卷新增一个规则，前端展示的添加规则按钮
    void saveCouponRule(CouponRuleVo couponRuleVo);
    //根据关键字获取sku列表，活动使用，就是把商品添加到这个优惠卷面下，表示此商品可以使用这个优惠卷，前端展示的添加活动范围按钮
    List<CouponInfo> findCouponByKeyword(String keyword);
    //获取优惠券信息
    List<CouponInfo> findCouponInfo(Long skuId, Long userId);
}
