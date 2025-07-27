package com.atnjupt.sqyxgo.product.service;

import com.atnjupt.sqyxgo.model.product.SkuPoster;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 商品海报表 服务类
 * </p>
 *
 * @author atnjupt
 * @since 2025-07-18
 */
public interface SkuPosterService extends IService<SkuPoster> {
    //通过skuId查商品的海报信息
    List<SkuPoster> getSkuPosterById(Long id);
}
