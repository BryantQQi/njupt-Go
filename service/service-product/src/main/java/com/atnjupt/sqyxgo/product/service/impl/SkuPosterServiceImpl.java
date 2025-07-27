package com.atnjupt.sqyxgo.product.service.impl;

import com.atnjupt.sqyxgo.model.product.SkuPoster;
import com.atnjupt.sqyxgo.product.mapper.SkuPosterMapper;
import com.atnjupt.sqyxgo.product.service.SkuPosterService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 商品海报表 服务实现类
 * </p>
 *
 * @author atnjupt
 * @since 2025-07-18
 */
@Service
public class SkuPosterServiceImpl extends ServiceImpl<SkuPosterMapper, SkuPoster> implements SkuPosterService {
    //通过skuId查商品的海报信息
    @Override
    public List<SkuPoster> getSkuPosterById(Long id) {
        LambdaQueryWrapper<SkuPoster> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SkuPoster::getSkuId,id);
        return baseMapper.selectList(wrapper);
    }
}
