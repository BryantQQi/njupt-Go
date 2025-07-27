package com.atnjupt.sqyxgo.product.service.impl;

import com.atnjupt.sqyxgo.model.product.SkuImage;
import com.atnjupt.sqyxgo.product.mapper.SkuImageMapper;
import com.atnjupt.sqyxgo.product.service.SkuImageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 商品图片 服务实现类
 * </p>
 *
 * @author atnjupt
 * @since 2025-07-18
 */
@Service
public class SkuImageServiceImpl extends ServiceImpl<SkuImageMapper, SkuImage> implements SkuImageService {
    //通过skuId查商品的图片信息
    @Override
    public List<SkuImage> getSkuImageById(Long id) {
        LambdaQueryWrapper<SkuImage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SkuImage::getSkuId,id);


        return baseMapper.selectList(wrapper);
    }
}
