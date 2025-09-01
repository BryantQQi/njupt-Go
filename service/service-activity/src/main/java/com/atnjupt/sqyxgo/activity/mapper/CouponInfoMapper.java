package com.atnjupt.sqyxgo.activity.mapper;

import com.atnjupt.sqyxgo.model.activity.CouponInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 优惠券信息 Mapper 接口
 * </p>
 *
 * @author atnjupt
 * @since 2025-07-21
 */
public interface CouponInfoMapper extends BaseMapper<CouponInfo> {
    //通过skuId,categoryId,userId获取优惠券信息
    List<CouponInfo> findCouponInfo(Long skuId, Long categoryId, Long userId);

    List<CouponInfo> findCouponInfoByUserId(Long userId);
}
