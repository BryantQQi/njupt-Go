package com.atnjupt.sqyxgo.sys.service;

import com.atnjupt.sqyxgo.model.sys.Ware;
import com.atnjupt.sqyxgo.vo.product.WareQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.HashMap;
import java.util.List;

/**
 * ClassName:WareService
 * Package: com.atnjupt.sqyxgo.sys.service
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/17 18:12
 * @Version 1.0
 */
public interface WareService extends IService<Ware> {
    //分页查询仓库
    IPage<Ware> findPageWare(Page<Ware> page1, WareQueryVo wareQueryVo);
    //通过id查询仓库
    Ware getWareById(Long id);
    //添加仓库
    void saveWare(Ware ware);
    //更新仓库
    void updateWare(Ware ware);
    //查询所有仓库
    List<Ware> findAllWareList();
}
