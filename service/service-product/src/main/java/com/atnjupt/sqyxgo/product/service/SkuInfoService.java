package com.atnjupt.sqyxgo.product.service;

import com.atnjupt.sqyxgo.model.product.SkuInfo;
import com.atnjupt.sqyxgo.vo.product.SkuInfoQueryVo;
import com.atnjupt.sqyxgo.vo.product.SkuInfoVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * sku信息 服务类
 * </p>
 *
 * @author atnjupt
 * @since 2025-07-18
 */
public interface SkuInfoService extends IService<SkuInfo> {
    //分页查询商品信息
    IPage<SkuInfo> getSkuInfoPage(Page<SkuInfo> page1, SkuInfoQueryVo skuInfoQueryVo);
    //添加商品信息
    boolean saveSkuInfo(SkuInfoVo skuInfoVo);
    //通过id查询商品信息
    SkuInfoVo getSkuInfoById(Long id);
    //更新商品信息
    boolean updateSkuInfo(SkuInfoVo skuInfoVo);
    //商品上下架
    void publishStatus(Long id, Integer status);
    //商品审核
    void checkStatus(Long id, Integer status);
    //是否是新人专享
    void isNewPerson(Long id, Integer status);
    //es:根据skuid获取sku信息
    SkuInfo getSkuInfoByIdToElasticsearch(Long skuId);
    //activity：通过关键字获得skuInfo集合
    List<SkuInfo> findSkuInfoByKeyword(String keyword);
    //获取新人专享
    List<SkuInfo> findNewPersonSkuInfoList();
    // 通过skuId 查询skuInfoVo
    SkuInfoVo getSkuInfoVo(Long skuId);
}
