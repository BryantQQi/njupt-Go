package com.atnjupt.sqyxgo.product.service;

import com.atnjupt.sqyxgo.model.product.AttrGroup;
import com.atnjupt.sqyxgo.vo.product.AttrGroupQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 属性分组 服务类
 * </p>
 *
 * @author atnjupt
 * @since 2025-07-18
 */
public interface AttrGroupService extends IService<AttrGroup> {
    //条件分页查询商品属性分组信息
    IPage<AttrGroup> getAttrGroupPage(Page<AttrGroup> page1, AttrGroupQueryVo attrGroupQueryVo);
}
