package com.atnjupt.sqyxgo.sys.service.Impl;

import com.atnjupt.sqyxgo.model.sys.Ware;
import com.atnjupt.sqyxgo.sys.mapper.WareMapper;
import com.atnjupt.sqyxgo.sys.service.WareService;
import com.atnjupt.sqyxgo.vo.product.WareQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;

/**
 * ClassName:WareServiceImpl
 * Package: com.atnjupt.sqyxgo.sys.service.Impl
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/17 18:15
 * @Version 1.0
 */
@Service
public class WareServiceImpl extends ServiceImpl<WareMapper,Ware> implements WareService{
    //分页查询仓库
    @Override
    public IPage<Ware> findPageWare(Page<Ware> page1, WareQueryVo wareQueryVo) {
        LambdaQueryWrapper<Ware> wrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(wareQueryVo.getName())||!StringUtils.isEmpty(wareQueryVo.getDistrict())){
            wrapper.like(Ware::getName,wareQueryVo.getName()).or().like(Ware::getDistrict,wareQueryVo.getDistrict());
        }
        IPage<Ware> warePage = baseMapper.selectPage(page1, wrapper);

        return warePage;
    }


    //通过id查询仓库
    @Override
    public Ware getWareById(Long id) {
        Ware ware = baseMapper.selectById(id);

        return ware;
    }

    //添加仓库
    @Override
    public void saveWare(Ware ware) {
        baseMapper.insert(ware);
    }
    //更新仓库
    @Override
    public void updateWare(Ware ware) {
        baseMapper.updateById(ware);
    }
    //查询所有仓库
    @Override
    public List<Ware> findAllWareList() {
        List<Ware> wares = baseMapper.selectList(null);
        return wares;
    }
}
