package com.atnjupt.sqyxgo.search.repository;

import com.atnjupt.sqyxgo.model.search.SkuEs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * ClassName:SkuRepository
 * Package: com.atnjupt.sqyxgo.search.repository
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/20 11:43
 * @Version 1.0
 */
public interface SkuRepository extends ElasticsearchRepository<SkuEs, Long> {

    //获取热销商品
    Page<SkuEs> findByOrderByHotScoreDesc(Pageable pageable);
    ////如果keyword为空，通过CategoryId和WareId查询商品信息
    Page<SkuEs> findByCategoryIdAndWareId(Long categoryId, Long wareId, Pageable pageable);
    ////如果keyword为空，通过CategoryId和WareId查询商品信息
    Page<SkuEs> findByKeywordAndWareId(String keyword, Long wareId, Pageable pageable);
}
