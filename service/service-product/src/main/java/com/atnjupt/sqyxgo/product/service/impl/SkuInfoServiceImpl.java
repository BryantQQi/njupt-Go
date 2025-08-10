package com.atnjupt.sqyxgo.product.service.impl;

import com.atnjupt.sqyxgo.model.product.*;
import com.atnjupt.sqyxgo.model.product.SkuImage;
import com.atnjupt.sqyxgo.mq.constant.MQConst;
import com.atnjupt.sqyxgo.mq.service.RabbitService;
import com.atnjupt.sqyxgo.product.mapper.SkuAttrValueMapper;
import com.atnjupt.sqyxgo.product.mapper.SkuInfoMapper;
import com.atnjupt.sqyxgo.product.service.*;
import com.atnjupt.sqyxgo.vo.product.SkuInfoQueryVo;
import com.atnjupt.sqyxgo.vo.product.SkuInfoVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * <p>
 * sku信息 服务实现类
 * </p>
 *
 * @author atnjupt
 * @since 2025-07-18
 */
@Service
@RequiredArgsConstructor
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoMapper, SkuInfo> implements SkuInfoService {

    private final SkuAttrValueService skuAttrValueService;
    private final SkuPosterService skuPosterService;
    private final SkuImageService skuImageService;
    private final RabbitService rabbitService;
    private final SkuAttrValueMapper skuAttrValueMapper;
    private final SkuInfoMapper skuInfoMapper;


    //分页查询商品信息
    @Override
    public IPage<SkuInfo> getSkuInfoPage(Page<SkuInfo> page1, SkuInfoQueryVo skuInfoQueryVo) {
        String keyword = skuInfoQueryVo.getKeyword();
        String skuType = skuInfoQueryVo.getSkuType();
        Long categoryId = skuInfoQueryVo.getCategoryId();
        LambdaQueryWrapper<SkuInfo> wrapper = new LambdaQueryWrapper<>();
        if(!StringUtils.isEmpty(skuInfoQueryVo.getSkuType())||
                !StringUtils.isEmpty(skuInfoQueryVo.getKeyword())||
                !StringUtils.isEmpty(skuInfoQueryVo.getCategoryId())){
            if(!StringUtils.isEmpty(keyword)) {
                wrapper.like(SkuInfo::getSkuName,keyword);
            }
            if(!StringUtils.isEmpty(skuType)) {
                wrapper.eq(SkuInfo::getSkuType,skuType);
            }
            if(!StringUtils.isEmpty(categoryId)) {
                wrapper.eq(SkuInfo::getCategoryId,categoryId);
            }
        }
        Page<SkuInfo> skuInfoPage = baseMapper.selectPage(page1, wrapper);
        return skuInfoPage;
    }


    //添加商品信息
    @Override
    public boolean saveSkuInfo(SkuInfoVo skuInfoVo) {
        //商品的基本信息，操作sku_info表
        SkuInfo skuInfo = new SkuInfo();
        BeanUtils.copyProperties(skuInfoVo,skuInfo);
        this.save(skuInfo);
        //平台属性，操作
        List<SkuAttrValue> skuAttrValueList = skuInfoVo.getSkuAttrValueList();
        if (!CollectionUtils.isEmpty(skuAttrValueList)){
            int sort = 1;
            for (SkuAttrValue attrValue : skuAttrValueList) {
                attrValue.setSkuId(skuInfo.getId());
                attrValue.setSort(sort++);
            }
            boolean is_success = skuAttrValueService.saveBatch(skuAttrValueList);
        }

        //商品图片
        List<SkuImage> skuImagesList = skuInfoVo.getSkuImagesList();
        if (!CollectionUtils.isEmpty(skuImagesList)) {
            int sort = 1;
            for (SkuImage skuImage : skuImagesList) {
                skuImage.setSkuId(skuInfo.getId());
                skuImage.setSort(sort++);
            }
            boolean is_success = skuImageService.saveBatch(skuImagesList);
        }

        //商品海报
        List<SkuPoster> skuPosterList = skuInfoVo.getSkuPosterList();
        if (!CollectionUtils.isEmpty(skuPosterList)){
            for (SkuPoster skuPoster : skuPosterList) {
               skuPoster.setSkuId(skuInfo.getId());
            }
            boolean is_success1 = skuPosterService.saveBatch(skuPosterList);
        }
        return true;
    }
    //通过id查询商品信息
    @Override
    public SkuInfoVo getSkuInfoById(Long id) {
        //商品的基本信息
        SkuInfoVo skuInfoVo = new SkuInfoVo();
        SkuInfo byId = this.getById(id);
        BeanUtils.copyProperties(byId,skuInfoVo);
        //通过skuId查商品的平台属性信息
        List<SkuAttrValue> skuAttrValueList = skuAttrValueService.getSkuAttrById(id);
        //通过skuId查商品的图片信息
        List<SkuImage> skuImageList = skuImageService.getSkuImageById(id);
        //通过skuId查商品的海报信息
        List<SkuPoster> skuPosterList = skuPosterService.getSkuPosterById(id);

        skuInfoVo.setSkuPosterList(skuPosterList);
        skuInfoVo.setSkuImagesList(skuImageList);
        skuInfoVo.setSkuAttrValueList(skuAttrValueList);
        return skuInfoVo;
    }

    //更新商品信息
    @Override
    public boolean updateSkuInfo(SkuInfoVo skuInfoVo) {

        //修改sku基本信息
        SkuInfo skuInfo = new SkuInfo();
        BeanUtils.copyProperties(skuInfoVo,skuInfo);
        baseMapper.updateById(skuInfo);
        //修改sku图片信息,先删除旧的，再添加新的，这是业界常用的做法
        Long id = skuInfo.getId();
        LambdaQueryWrapper<SkuImage> imageWrapper = new LambdaQueryWrapper<>();
        imageWrapper.eq(SkuImage::getSkuId,id);
        skuImageService.remove(imageWrapper);

        List<SkuImage> skuImagesList = skuInfoVo.getSkuImagesList();
        if (!CollectionUtils.isEmpty(skuImagesList)) {
            int sort = 1;
            for (SkuImage skuImage : skuImagesList) {
                skuImage.setSkuId(id);
                skuImage.setSort(sort++);
            }
            boolean is_success = skuImageService.saveBatch(skuImagesList);
        }


        //修改sku海报信息
        LambdaQueryWrapper<SkuPoster> posterWrapper = new LambdaQueryWrapper<>();
        posterWrapper.eq(SkuPoster::getSkuId,id);
        skuPosterService.remove(posterWrapper);

        List<SkuPoster> skuPosterList = skuInfoVo.getSkuPosterList();
        if (!CollectionUtils.isEmpty(skuPosterList)){
            for (SkuPoster skuPoster : skuPosterList) {
                skuPoster.setSkuId(skuInfo.getId());
            }
            boolean is_success1 = skuPosterService.saveBatch(skuPosterList);
        }


        //修改sku属性信息,删除旧的，添加新的
        skuAttrValueService.remove(new LambdaQueryWrapper<SkuAttrValue>().eq(SkuAttrValue::getSkuId,id));
        List<SkuAttrValue> skuAttrValueList = skuInfoVo.getSkuAttrValueList();
        if (!CollectionUtils.isEmpty(skuAttrValueList)){
            int sort = 1;
            for (SkuAttrValue attrValue : skuAttrValueList) {
                attrValue.setSkuId(id);
                attrValue.setSort(sort++);
            }
            boolean is_success = skuAttrValueService.saveBatch(skuAttrValueList);
        }


        return true;
    }
    //商品上下架
    @Override
    public void publishStatus(Long id, Integer status) {
        SkuInfo skuInfo = baseMapper.selectById(id);
        skuInfo.setPublishStatus(status);
        baseMapper.updateById(skuInfo);
        if (status == 1) {
            //如果上架，通过mq把skuId发送到es
            rabbitService.sendMessage(MQConst.EXCHANGE_GOODS_DIRECT,MQConst.ROUTING_GOODS_UPPER,id);
        }else {
            //如果是下架，通过mq把skuId发送到es，进行删除
            rabbitService.sendMessage(MQConst.EXCHANGE_GOODS_DIRECT,MQConst.ROUTING_GOODS_LOWER,id);
        }
    }
    //商品审核
    @Override
    public void checkStatus(Long id, Integer status) {
        SkuInfo skuInfo = baseMapper.selectById(id);
        skuInfo.setCheckStatus(status);
        baseMapper.updateById(skuInfo);

    }
    //是否是新人专享
    @Override
    public void isNewPerson(Long id, Integer status) {
        SkuInfo skuInfo = baseMapper.selectById(id);
        skuInfo.setIsNewPerson(status);
        baseMapper.updateById(skuInfo);
    }

    //es:根据skuid获取sku信息
    @Override
    public SkuInfo getSkuInfoByIdToElasticsearch(Long skuId) {
        return baseMapper.selectById(skuId);
    }


    //activity：通过关键字获得skuInfo集合
    @Override
    public List<SkuInfo> findSkuInfoByKeyword(String keyword) {
        LambdaQueryWrapper<SkuInfo> skuInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        skuInfoLambdaQueryWrapper.like(SkuInfo::getSkuName,keyword);
        List<SkuInfo> skuInfoList = baseMapper.selectList(skuInfoLambdaQueryWrapper);
        return skuInfoList;
    }
    //获取新人专享
    @Override
    public List<SkuInfo> findNewPersonSkuInfoList() {
        LambdaQueryWrapper<SkuInfo> skuInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        skuInfoLambdaQueryWrapper.eq(SkuInfo::getIsNewPerson,1);
        List<SkuInfo> isNewPersonSkuInfoList = baseMapper.selectList(skuInfoLambdaQueryWrapper);
        return isNewPersonSkuInfoList;
    }


    // 通过skuId 查询skuInfoVo
    @Override
    public SkuInfoVo getSkuInfoVo(Long skuId) {

        SkuInfoVo skuInfoVo = new SkuInfoVo();
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        List<SkuImage> skuImageList = skuImageService.getSkuImageById(skuId);
        List<SkuPoster> skuPosterList = skuPosterService.getSkuPosterById(skuId);
        List<SkuAttrValue> skuAttrValueList = skuAttrValueService.getSkuAttrById(skuId);

        BeanUtils.copyProperties(skuInfo, skuInfoVo);
        skuInfoVo.setSkuImagesList(skuImageList);
        skuInfoVo.setSkuPosterList(skuPosterList);
        skuInfoVo.setSkuAttrValueList(skuAttrValueList);
        return skuInfoVo;
    }
}
