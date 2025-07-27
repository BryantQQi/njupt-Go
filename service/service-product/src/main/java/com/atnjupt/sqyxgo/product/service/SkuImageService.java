package com.atnjupt.sqyxgo.product.service;

import com.atnjupt.sqyxgo.model.product.SkuImage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 商品图片 服务类
 * </p>
 *
 * @author atnjupt
 * @since 2025-07-18
 */
public interface SkuImageService extends IService<SkuImage> {
    //通过skuId查商品的图片信息
    List<SkuImage> getSkuImageById(Long id);
}
