package com.atnjupt.sqyxgo.product.service.impl;

import com.atnjupt.sqyxgo.model.product.AttrGroup;
import com.atnjupt.sqyxgo.product.mapper.AttrGroupMapper;
import com.atnjupt.sqyxgo.product.service.AttrGroupService;
import com.atnjupt.sqyxgo.vo.product.AttrGroupQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 属性分组 服务实现类
 * </p>
 *
 * @author atnjupt
 * @since 2025-07-18
 */
@Service
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupMapper, AttrGroup> implements AttrGroupService {
    //条件分页查询商品属性分组信息
    @Override
    public IPage<AttrGroup> getAttrGroupPage(Page<AttrGroup> page1, AttrGroupQueryVo attrGroupQueryVo) {
        LambdaQueryWrapper<AttrGroup> wrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(attrGroupQueryVo.getName())){
            wrapper.like(AttrGroup::getName,attrGroupQueryVo.getName());
        }
        Page<AttrGroup> attrGroupPage = baseMapper.selectPage(page1, wrapper);
        return attrGroupPage;
    }
}
