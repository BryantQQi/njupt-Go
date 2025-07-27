package com.atnjupt.sqyxgo.product.service;

import com.atnjupt.sqyxgo.model.product.Category;
import com.atnjupt.sqyxgo.vo.product.CategoryQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 商品三级分类 服务类
 * </p>
 *
 * @author atnjupt
 * @since 2025-07-18
 */
public interface CategoryService extends IService<Category> {
    //商品分类信息分页查询
    IPage<Category> getPageCategory(Page<Category> page1, CategoryQueryVo categoryQueryVo);
    //es:根据categoryId获取分类的信息
    Category getCategoryToElasticseacrch(Long categoryId);
    //给一个categoryIdList，返回一个categoryList集合
    List<Category> findCategoryListByCategoryIdList(List<Long> categoryIdList);
}
