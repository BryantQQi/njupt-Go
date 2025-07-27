package com.atnjupt.sqyxgo.product.service.impl;

import com.atnjupt.sqyxgo.model.product.SkuAttrValue;
import com.atnjupt.sqyxgo.product.mapper.SkuAttrValueMapper;
import com.atnjupt.sqyxgo.product.service.SkuAttrValueService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * spu属性值 服务实现类
 * </p>
 *
 * @author atnjupt
 * @since 2025-07-18
 */
@Service
public class SkuAttrValueServiceImpl extends ServiceImpl<SkuAttrValueMapper, SkuAttrValue> implements SkuAttrValueService {
    //通过skuId查商品的平台属性信息
    @Override
    public List<SkuAttrValue> getSkuAttrById(Long id) {
        LambdaQueryWrapper<SkuAttrValue> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SkuAttrValue::getSkuId,id);
        return baseMapper.selectList(wrapper);
    }
}
