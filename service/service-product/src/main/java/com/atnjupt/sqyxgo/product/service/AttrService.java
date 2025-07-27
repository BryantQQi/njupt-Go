package com.atnjupt.sqyxgo.product.service;

import com.atnjupt.sqyxgo.model.product.Attr;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 商品属性 服务类
 * </p>
 *
 * @author atnjupt
 * @since 2025-07-18
 */
public interface AttrService extends IService<Attr> {
    //根据平台属性分组id查询商品属性信息
    List<Attr> getByGroupId(Long groupId);
}
