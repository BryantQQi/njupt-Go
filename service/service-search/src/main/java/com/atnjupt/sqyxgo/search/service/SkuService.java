package com.atnjupt.sqyxgo.search.service;

import com.atnjupt.sqyxgo.model.search.SkuEs;
import com.atnjupt.sqyxgo.vo.search.SkuEsQueryVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ClassName:SkuService
 * Package: com.atnjupt.sqyxgo.search.service
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/20 11:40
 * @Version 1.0
 */
@Service
public interface SkuService {
    //上架商品
    void upperSkuById(Long skuId);
    //下架商品
    void downSkuInfoById(Long skuId);
    //获取热销商品
    List<SkuEs> findHotSkuList();
    //搜索商品，通过分类查询的分类下的商品
    Page<SkuEs> search(Pageable pageable, SkuEsQueryVo skuEsQueryVo);
    //更新商品incrHotScore
    void incrHotScore(Long skuId);
}
