package com.atnjupt.sqyxgo.product.service.impl;

import com.atnjupt.sqyxgo.model.product.Category;
import com.atnjupt.sqyxgo.product.mapper.CategoryMapper;
import com.atnjupt.sqyxgo.product.service.CategoryService;
import com.atnjupt.sqyxgo.vo.product.CategoryQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 * 商品三级分类 服务实现类
 * </p>
 *
 * @author atnjupt
 * @since 2025-07-18
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    //商品分类信息分页查询
    @Override
    public IPage<Category> getPageCategory(Page<Category> page1,
                                           CategoryQueryVo categoryQueryVo) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(categoryQueryVo.getName())) {
            wrapper.like(Category::getName, categoryQueryVo.getName());
        }

        Page<Category> categoryPage = baseMapper.selectPage(page1, wrapper);
        return categoryPage;
    }
    //es:根据categoryId获取分类的信息
    @Override
    public Category getCategoryToElasticseacrch(Long categoryId) {
        return baseMapper.selectById(categoryId);
    }


    //给一个categoryIdList，返回一个categoryList集合
    @Override
    public List<Category> findCategoryListByCategoryIdList(List<Long> categoryIdList) {
        return baseMapper.selectBatchIds(categoryIdList);
    }
}
