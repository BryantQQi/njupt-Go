package com.atnjupt.sqyxgo.sys.service.Impl;

import com.atnjupt.sqyxgo.model.sys.Region;
import com.atnjupt.sqyxgo.sys.mapper.RegionMapper;
import com.atnjupt.sqyxgo.sys.service.RegionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ClassName:RegionServiceImpl
 * Package: com.atnjupt.sqyxgo.sys.service.Impl
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/17 18:18
 * @Version 1.0
 */
@Service
public class RegionServiceImpl extends ServiceImpl<RegionMapper, Region> implements RegionService {

    //通过关键字查询区域
    @Override
    public List<Region> findRegionByKeyword(String keyword) {
        LambdaQueryWrapper<Region> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Region::getName,keyword);
        List<Region> regions = baseMapper.selectList(wrapper);
        return regions ;
    }

    //通过parentId查询区域
    @Override
    public List<String> findByParentId(Long parentId) {
        LambdaQueryWrapper<Region> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Region::getParentId,parentId);
        List<String> list = baseMapper.selectList(wrapper).stream().map(Region::getName).collect(Collectors.toList());
        return list;
    }

}
