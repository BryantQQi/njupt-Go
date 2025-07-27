package com.atnjupt.sqyxgo.product.service.impl;

import com.atnjupt.sqyxgo.model.product.Attr;
import com.atnjupt.sqyxgo.product.mapper.AttrMapper;
import com.atnjupt.sqyxgo.product.service.AttrService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 * 商品属性 服务实现类
 * </p>
 *
 * @author atnjupt
 * @since 2025-07-18
 */
@Service
public class AttrServiceImpl extends ServiceImpl<AttrMapper, Attr> implements AttrService {
    //根据平台属性分组id查询商品属性信息
    @Override
    public List<Attr> getByGroupId(Long groupId) {
        LambdaQueryWrapper<Attr> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Attr::getAttrGroupId,groupId);
        List<Attr> attrs = baseMapper.selectList(wrapper);
        return attrs;
    }
}
