package com.atnjupt.sqyxgo.search.repository;

import com.atnjupt.sqyxgo.model.search.SkuEs;
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



}
