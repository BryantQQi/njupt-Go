package com.atnjupt.sqyxgo.product.service;

import com.atnjupt.sqyxgo.model.product.SkuAttrValue;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * spu属性值 服务类
 * </p>
 *
 * @author atnjupt
 * @since 2025-07-18
 */
public interface SkuAttrValueService extends IService<SkuAttrValue> {
    //通过skuId查商品的平台属性信息
    List<SkuAttrValue> getSkuAttrById(Long id);
}
