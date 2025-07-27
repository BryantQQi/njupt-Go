package com.atnjupt.sqyxgo.sys.service.Impl;

import com.atnjupt.sqyxgo.common.exception.SqyxgoException;
import com.atnjupt.sqyxgo.common.result.ResultCodeEnum;
import com.atnjupt.sqyxgo.model.sys.RegionWare;
import com.atnjupt.sqyxgo.sys.mapper.RegionWareMapper;
import com.atnjupt.sqyxgo.sys.service.RegionWareService;
import com.atnjupt.sqyxgo.vo.sys.RegionWareQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ClassName:RegionWareServiceImpl
 * Package: com.atnjupt.sqyxgo.sys.service.Impl
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/17 18:19
 * @Version 1.0
 */
@Service
public class RegionWareServiceImpl extends ServiceImpl<RegionWareMapper, RegionWare> implements RegionWareService {

    //开通区域列表
    @Override
    public IPage<RegionWare> selectPage(Page<RegionWare> pageParam, RegionWareQueryVo regionWareQueryVo) {
        LambdaQueryWrapper<RegionWare> wrapper = new LambdaQueryWrapper<>();
        if(!StringUtils.isEmpty(regionWareQueryVo.getKeyword())){
            wrapper.like(RegionWare::getRegionName,regionWareQueryVo.getKeyword()).or()
                    .like(RegionWare::getWareName,regionWareQueryVo.getKeyword());
        }
        IPage<RegionWare> regionWarePage = baseMapper.selectPage(pageParam, wrapper);

        return regionWarePage;
    }

    //添加开通区域
    @Override
    public void saveRegionWare(RegionWare regionWare) {
        LambdaQueryWrapper<RegionWare> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RegionWare::getRegionId,regionWare.getRegionId()).eq(RegionWare::getWareId,regionWare.getWareId());
        Integer integer = baseMapper.selectCount(wrapper);
        if (integer == 0){
            RegionWare saveRegionWare = new RegionWare();
            saveRegionWare.setRegionId(regionWare.getRegionId());
            saveRegionWare.setRegionName(regionWare.getRegionName());
            saveRegionWare.setWareId(regionWare.getWareId());
            saveRegionWare.setWareName(regionWare.getWareName());
            baseMapper.insert(saveRegionWare);
        }else {
            throw new SqyxgoException(ResultCodeEnum.REGION_OPEN);
        }

    }


    //通过区域id查询开通情况
    @Override
    public List<String> getRegionById(Long id) {
        LambdaQueryWrapper<RegionWare> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RegionWare::getRegionId,id);
        List<String> list = baseMapper.selectList(wrapper).stream()
                .map(RegionWare::getWareName).collect(Collectors.toList());
        return list;
    }

    //通过id更新区域状态，表示该区域是否开通仓库
    @Override
    public Boolean updateRegionStatus(Long id, Integer status) {
        LambdaQueryWrapper<RegionWare> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RegionWare::getId,id);
        RegionWare regionWare = baseMapper.selectOne(wrapper);
        regionWare.setStatus(status);
        boolean is_success = this.updateById(regionWare);
        return is_success;
    }

    //通过id批量删除区域里的仓库，删除全部
    @Override
    public void batchRemoveByIdList(List<Long> idList) {
        baseMapper.deleteBatchIds(idList);
    }
}
