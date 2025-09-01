package com.atguigu.ssyx.cart.service.impl;

import com.atguigu.ssyx.cart.service.CartInfoService;
import com.atguigu.ssyx.client.product.ProductFeignClient;
import com.atguigu.ssyx.common.constant.RedisConst;
import com.atguigu.ssyx.common.exception.SsyxException;
import com.atguigu.ssyx.common.result.ResultCodeEnum;
import com.atguigu.ssyx.enums.SkuType;
import com.atguigu.ssyx.model.order.CartInfo;
import com.atguigu.ssyx.model.product.SkuInfo;
import com.atguigu.ssyx.vo.order.CartInfoVo;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * ClassName: CartInfoServiceImpl
 * Package: com.atguigu.ssyx.service.impl
 * Description:
 *
 * @Author liang
 * @Create 2024/11/21 22:19
 * @Version jdk17.0
 */
@Service
@RequiredArgsConstructor
public class CartInfoServiceImpl implements CartInfoService {
    private final ProductFeignClient productFeignClient;
    private final RedisTemplate redisTemplate;
    /**
     * 添加商品到购物车
     * @param skuId
     * @param userId
     * @param skuNum
     */
    @Override
    public void addToCart(Long skuId, Long userId, Integer skuNum) {
        String key = getCartKey(userId);
        BoundHashOperations<String,String, CartInfo> boundHashOperations = redisTemplate.boundHashOps(key);
        CartInfo cartInfo = null;
        if(boundHashOperations.hasKey(skuId.toString())){
            //redis中有这个商品
            cartInfo = boundHashOperations.get(skuId.toString());
            int currentSkuNum = cartInfo.getSkuNum() + skuNum;
            if(currentSkuNum < 1){
               return;
            }
            cartInfo.setSkuNum(currentSkuNum);
            cartInfo.setCurrentBuyNum(currentSkuNum);
            Integer perLimit = cartInfo.getPerLimit();
            if(currentSkuNum>perLimit){
                throw new SsyxException(ResultCodeEnum.SKU_LIMIT_ERROR);
            }
            cartInfo.setIsChecked(1);
            cartInfo.setUpdateTime(new Date());
        }else{
            //没有这个商品
            cartInfo =new CartInfo();
            skuNum = 1;
            //远程调用获取值
            SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
            if(skuInfo == null){
                throw  new SsyxException(ResultCodeEnum.DATA_ERROR);
            }
            cartInfo.setSkuId(skuId);
            cartInfo.setCategoryId(skuInfo.getCategoryId());
            cartInfo.setSkuType(skuInfo.getSkuType());
            cartInfo.setIsNewPerson(skuInfo.getIsNewPerson());
            cartInfo.setUserId(userId);
            cartInfo.setCartPrice(skuInfo.getPrice());
            cartInfo.setSkuNum(skuNum);
            cartInfo.setCurrentBuyNum(skuNum);
            cartInfo.setSkuType(SkuType.COMMON.getCode());
            cartInfo.setPerLimit(skuInfo.getPerLimit());
            cartInfo.setImgUrl(skuInfo.getImgUrl());
            cartInfo.setSkuName(skuInfo.getSkuName());
            cartInfo.setWareId(skuInfo.getWareId());
            cartInfo.setIsChecked(1);
            cartInfo.setStatus(1);
            cartInfo.setCreateTime(new Date());
            cartInfo.setUpdateTime(new Date());

        }
        //更新redis
        boundHashOperations.put(skuId.toString(), cartInfo);
        // 设置过期时间

    this.setCartKeyExpire(key);
    }

    /**
     * 根据skuId删除redis中购物车
     * @param skuId
     * @param userId
     */
    @Override
    public void deleteCart(Long skuId, Long userId) {
        String cartKey = this.getCartKey(userId);
        BoundHashOperations<String,String,CartInfo> boundHashOperations = redisTemplate.boundHashOps(cartKey);
        if(boundHashOperations.hasKey(skuId.toString())){
            //有购物车进行删除
            boundHashOperations.delete(skuId.toString());
        }

    }

    /**
     * 清空购物车
     * @param userId
     */
    @Override
    public void deleteAllCart(Long userId) {
        String cartKey = getCartKey(userId);
        //获取缓存对象
        BoundHashOperations<String, String, CartInfo> hashOperations = redisTemplate.boundHashOps(cartKey);
        hashOperations.values().forEach(cartInfo -> {
            hashOperations.delete(cartInfo.getSkuId().toString());
        });
    }

    /**
     * 批量删除购物车
     * @param skuIdList
     * @param userId
     */
    @Override
    public void batchDeleteCart(List<Long> skuIdList, Long userId) {
        String cartKey = this.getCartKey(userId);
        BoundHashOperations<String,String,CartInfo> boundHashOperations = redisTemplate.boundHashOps(cartKey);
        for (Long aLong : skuIdList) {
            boundHashOperations.delete(aLong.toString());
        }

    }

    /**
     * 获取购物车列表
     * @param userId
     * @return
     */
    @Override
    public List<CartInfo> getCartList(Long userId) {
        String cartKey = this.getCartKey(userId);
        BoundHashOperations<String,String,CartInfo> boundHashOperations = redisTemplate.boundHashOps(cartKey);
        List<CartInfo> cartInfoList = boundHashOperations.values();
        //根据时间降序
        if(CollectionUtils.isEmpty(cartInfoList)){
        cartInfoList.sort((cartInfo1, cartInfo2) -> {
            return cartInfo2.getUpdateTime().compareTo(cartInfo1.getUpdateTime());
        });}

        return  cartInfoList;
    }

    /**
     * 更新购物车的选中状态
     * @param userId
     * @param isChecked
     * @param skuId
     */
    @Override
    public void checkCart(Long userId, Integer isChecked, Long skuId) {
        String cartKey = this.getCartKey(userId);
        BoundHashOperations<String,String,CartInfo> boundHashOperations = redisTemplate.boundHashOps(cartKey);
        CartInfo cartInfo = boundHashOperations.get(skuId.toString());
        if(cartInfo != null ){
            cartInfo.setIsChecked(isChecked);
            boundHashOperations.put(skuId.toString(), cartInfo);
            this.setCartKeyExpire(cartKey);
        }


    }

    /**
     * 选中所有购物车
     * @param userId
     * @param isChecked
     */
    @Override
    public void checkAllCart(Long userId, Integer isChecked) {
        String cartKey = this.getCartKey(userId);
        BoundHashOperations<String,String,CartInfo> boundHashOperations = redisTemplate.boundHashOps(cartKey);
        List<CartInfo> cartInfoList = boundHashOperations.values();
        cartInfoList.stream().forEach(cartInfo -> {
            cartInfo.setIsChecked(isChecked);
            boundHashOperations.put(cartInfo.getSkuId().toString(), cartInfo);
        });
        this.setCartKeyExpire(cartKey);

    }

    /**
     * 批量选择购物车
     * @param skuIdList
     * @param userId
     * @param isChecked
     */
    @Override
    public void batchCheckCart(List<Long> skuIdList, Long userId, Integer isChecked) {
        String cartKey = this.getCartKey(userId);
        BoundHashOperations<String,String,CartInfo> boundHashOperations = redisTemplate.boundHashOps(cartKey);
        skuIdList.stream().forEach(skuId->{
            CartInfo cartInfo = boundHashOperations.get(skuId);
            if(cartInfo!=null){
                cartInfo.setIsChecked(isChecked);
                boundHashOperations.put(skuId.toString(), cartInfo);
            }
        });
        this.setCartKeyExpire(cartKey);
    }

    /**
     * 查询已经选中的购物车列表
     * @param userId
     * @return
     */
    @Override
    public List<CartInfo> getCartCheckedList(Long userId) {
        BoundHashOperations<String, String, CartInfo> boundHashOps = this.redisTemplate.boundHashOps(this.getCartKey(userId));
        List<CartInfo> cartInfoCheckList = boundHashOps.values().stream().filter((cartInfo) -> {
            return cartInfo.getIsChecked().intValue() == 1;
        }).collect(Collectors.toList());
        CartInfoVo cartInfoVo = new CartInfoVo();
        cartInfoVo.setActivityRule(null);
        //CartInfoVo cartInfoVo = new CartInfoVo(Lists.newArrayList(), null);
        //CartInfoVo cartInfoVo1 = cartInfoVo.
        //        builder()
        //        .activityRule(null)
        //        .cartInfoList(null)
        //        .build();
        //cartInfoVo.setCartInfoList(cartInfoCheckList);
        //cartInfoVo.setActivityRule(activityRuleService.getCartActivityRule(cartInfoCheckList));

        //new CartInfo().(BigDecimal.ONE).setIsChecked(1);
        BeanUtils.copyProperties(cartInfoVo, cartInfoCheckList);
        return cartInfoCheckList;
    }

    /**
     * 删除已经购买了的购物项
     * @param userId
     */
    @Override
    public void deleteBuyCart(Long userId) {
        String cartKey = this.getCartKey(userId);
        BoundHashOperations<String,String,CartInfo> boundHashOperations = redisTemplate.boundHashOps(cartKey);
        List<CartInfo> cartCheckedList = this.getCartCheckedList(userId);
        List<Long> skuIds = cartCheckedList.stream().map(CartInfo::getSkuId).collect(Collectors.toList());
        for (Long skuId : skuIds) {
            boundHashOperations.delete(skuIds.toString());
        }

    }

    /**
     * 获得redis中的key根据userId
     */
    private  String getCartKey(Long userId){
        return RedisConst.USER_KEY_PREFIX+userId+RedisConst.USER_CART_KEY_SUFFIX;
    }

    //  设置key 的过期时间！
    private void setCartKeyExpire(String cartKey) {
        redisTemplate.expire(cartKey, RedisConst.USER_CART_EXPIRE, TimeUnit.SECONDS);
    }

}
