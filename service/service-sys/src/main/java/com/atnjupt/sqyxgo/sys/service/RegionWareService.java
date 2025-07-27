package com.atnjupt.sqyxgo.sys.service;

import com.atnjupt.sqyxgo.model.sys.RegionWare;
import com.atnjupt.sqyxgo.vo.sys.RegionWareQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * ClassName:RegionWare
 * Package: com.atnjupt.sqyxgo.sys.service
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/17 18:13
 * @Version 1.0
 */
public interface RegionWareService extends IService<RegionWare> {
    //开通区域列表
    IPage<RegionWare> selectPage(Page<RegionWare> pageParam, RegionWareQueryVo regionWareQueryVo);
    //添加开通区域
    void saveRegionWare(RegionWare regionWare);

    //通过区域id查询开通情况
    List<String> getRegionById(Long id);
    //通过id更新区域状态，表示该区域是否开通仓库
    Boolean updateRegionStatus(Long id, Integer status);
    //通过id批量删除区域里的仓库，删除全部
    void batchRemoveByIdList(List<Long> idList);
}
