package com.atnjupt.sqyxgo.sys.service;

import com.atnjupt.sqyxgo.model.sys.Region;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * ClassName:RegionService
 * Package: com.atnjupt.sqyxgo.sys.service
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/17 18:13
 * @Version 1.0
 */
public interface RegionService extends IService<Region> {
    //通过关键字查询区域
    List<Region> findRegionByKeyword(String keyword);

    //通过parentId查询区域
    List<String> findByParentId(Long parentId);
}
