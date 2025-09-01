package com.atguigu.ssyx.order.service.impl;


import com.atguigu.ssyx.client.activity.ActivityFeignClient;
import com.atguigu.ssyx.client.cart.CartFeignClient;
import com.atguigu.ssyx.client.product.ProductFeignClient;
import com.atguigu.ssyx.client.user.UserFeignClient;
import com.atguigu.ssyx.common.auth.AuthContextHolder;
import com.atguigu.ssyx.common.constant.RedisConst;
import com.atguigu.ssyx.common.exception.SsyxException;
import com.atguigu.ssyx.common.result.ResultCodeEnum;
import com.atguigu.ssyx.common.utils.DateUtil;
import com.atguigu.ssyx.enums.*;
import com.atguigu.ssyx.model.activity.ActivityRule;
import com.atguigu.ssyx.model.activity.CouponInfo;
import com.atguigu.ssyx.model.order.CartInfo;
import com.atguigu.ssyx.model.order.OrderInfo;
import com.atguigu.ssyx.model.order.OrderItem;
import com.atguigu.ssyx.mq.constant.MqConst;
import com.atguigu.ssyx.mq.service.RabbitService;
import com.atguigu.ssyx.order.mapper.OrderItemMapper;
import com.atguigu.ssyx.order.service.OrderInfoService;
import com.atguigu.ssyx.order.mapper.OrderInfoMapper;
import com.atguigu.ssyx.vo.order.CartInfoVo;
import com.atguigu.ssyx.vo.order.OrderConfirmVo;
import com.atguigu.ssyx.vo.order.OrderSubmitVo;
import com.atguigu.ssyx.vo.order.OrderUserQueryVo;
import com.atguigu.ssyx.vo.product.SkuStockLockVo;
import com.atguigu.ssyx.vo.user.LeaderAddressVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
* @author liang
* @description 针对表【order_info(订单)】的数据库操作Service实现
* @createDate 2024-11-26 15:31:06
*/
@Service
@RequiredArgsConstructor
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo>
    implements OrderInfoService{
    private final RabbitService rabbitService;
    private final ProductFeignClient productFeignClient;
    private final UserFeignClient userFeignClient;
    private final CartFeignClient cartFeignClient;
    private final ActivityFeignClient activityFeignClient;
    private final RedisTemplate redisTemplate;
    private final OrderItemMapper orderItemMapper;
    /**
     * 确认订单
     * @return
     */
    @Override
    public OrderConfirmVo confirmOrder() {
        Long userId = AuthContextHolder.getUserId();
        //获取团长信息
        LeaderAddressVo leaderAddressVo = userFeignClient.getUserAddressByUserId(userId);
        //获取购物车中选中了的商品
        List<CartInfo> cartCheckedList = cartFeignClient.getCartCheckedList(userId);
        String orderNo = System.currentTimeMillis()+"";
         redisTemplate.opsForValue().set(RedisConst.ORDER_REPEAT+orderNo,orderNo,24, TimeUnit.HOURS);
        //获取购物车满足条件的促销与优惠券信息
        OrderConfirmVo orderTradeVo = activityFeignClient.findCartActivityAndCoupon(cartCheckedList, userId);
        orderTradeVo.setLeaderAddressVo(leaderAddressVo);
        orderTradeVo.setOrderNo(orderNo);
        return orderTradeVo;

    }

    /**
     * 生成订单
     * @param
     * @return
     */

    @Override
    public Long submitOrder(OrderSubmitVo orderSubmitVo) {
        Long userId = AuthContextHolder.getUserId();
        orderSubmitVo.setUserId(userId);
        // 1 订单不能重复提交，重复提交验证   lua脚本+redis实现
        String orderNo = orderSubmitVo.getOrderNo();
        if (StringUtils.isEmpty(orderNo)){
            throw new SsyxException(ResultCodeEnum.ILLEGAL_REQUEST);
        }
        String script = "if(redis.call('get', KEYS[1]) == ARGV[1]) then return redis.call('del', KEYS[1]) else return 0 end";
        Boolean flag = (Boolean)redisTemplate.execute(new DefaultRedisScript<>(script, Boolean.class),
                Arrays.asList(RedisConst.ORDER_REPEAT + orderNo), orderNo);
        if(!flag){
            throw new SsyxException(ResultCodeEnum.REPEAT_SUBMIT);
        }
        // 2 验证库存，并且锁定库存
        List<CartInfo> cartCheckedList = cartFeignClient.getCartCheckedList(userId);
        List<CartInfo> commonSkuList = cartCheckedList.stream().
                filter(cartInfo -> cartInfo.getSkuType() == SkuType.COMMON.getCode()).collect(Collectors.toList());
        List<SkuStockLockVo> skuStockLockVoList = null;
        if(!CollectionUtils.isEmpty(commonSkuList)){
                skuStockLockVoList = commonSkuList.stream().map(cartInfo -> {
                SkuStockLockVo skuStockLockVo = new SkuStockLockVo();
                skuStockLockVo.setSkuId(cartInfo.getSkuId());
                skuStockLockVo.setSkuNum(cartInfo.getSkuNum());
                return skuStockLockVo;
            }).collect(Collectors.toList());
        }
             //进行锁定
        Boolean isLockSuccess = productFeignClient.checkAndLock(skuStockLockVoList, orderNo);
        if(!isLockSuccess){
             //锁定失败，抛出异常
            throw new SsyxException(ResultCodeEnum.ORDER_STOCK_FALL);
        }
        // 3 保存订单，往表中添加数据
        Long orderId = this.saveOrder(orderSubmitVo,cartCheckedList);
        // 4 删除 购物车中被购买过的购物项
        rabbitService.sendMessage(MqConst.EXCHANGE_ORDER_DIRECT,MqConst.ROUTING_DELETE_CART,orderSubmitVo.getUserId());
        return orderId;
    }

    /**
     * 返回订单详情
     * @param orderId
     * @return
     */
    @Override
    public OrderInfo getOrderInfoById(Long orderId) {
        OrderInfo orderInfo = baseMapper.selectById(orderId);
        if(orderInfo == null){
            throw  new SsyxException(ResultCodeEnum.DATA_ERROR);
        }
        List<OrderItem> orderItems = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId));
        orderInfo.setOrderItemList(orderItems);
        return orderInfo;
    }

    /**
     * 修改订单的支付状态，和删减库存
     */
    @Override
    public void updateOrderInfoStatus(String orderNo) {
        LambdaQueryWrapper<OrderInfo> orderInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        orderInfoLambdaQueryWrapper.eq(OrderInfo::getOrderNo, orderNo);
        OrderInfo orderInfo = baseMapper.selectOne(orderInfoLambdaQueryWrapper);
        if(orderInfo == null || orderInfo.getOrderStatus() != OrderStatus.UNPAID){
            throw  new SsyxException(ResultCodeEnum.DATA_ERROR);
        }
        orderInfo.setOrderStatus(OrderStatus.WAITING_DELEVER);
        baseMapper.insert(orderInfo);
        //删减库存
        LambdaQueryWrapper<OrderItem> orderItemLambdaQueryWrapper = new LambdaQueryWrapper<>();
        orderItemLambdaQueryWrapper.eq(OrderItem::getOrderId,orderInfo.getId());
        List<OrderItem> orderItems = orderItemMapper.selectList(orderItemLambdaQueryWrapper);
        Map<Long,Integer> skuIdToSkuNum = null;
        skuIdToSkuNum  = orderItems.stream().collect(Collectors.toMap(OrderItem::getSkuId, OrderItem::getSkuNum));
        rabbitService.sendMessage(MqConst.EXCHANGE_ORDER_DIRECT,MqConst.ROUTING_PAY_SUCCESS,skuIdToSkuNum);


    }

    /**
     * 根据订单状态分页查询订单
     * @param pageParam
     * @param orderUserQueryVo
     * @return
     */
    @Override
    public IPage<OrderInfo> findUserOrderPage(Page<OrderInfo> pageParam, OrderUserQueryVo orderUserQueryVo) {
        Long userId = orderUserQueryVo.getUserId();
        if(userId == null ){
            throw  new SsyxException(ResultCodeEnum.LOGIN_AUTH);
        }
        OrderStatus orderStatus = orderUserQueryVo.getOrderStatus();
        LambdaQueryWrapper<OrderInfo> orderInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        orderInfoLambdaQueryWrapper.eq(OrderInfo::getUserId, userId)
                .eq(orderStatus!=null,OrderInfo::getOrderStatus,orderStatus);
        Page<OrderInfo> orderInfoPage = baseMapper.selectPage(pageParam, orderInfoLambdaQueryWrapper);
        List<OrderInfo> orderInfos = orderInfoPage.getRecords();
        if(!CollectionUtils.isEmpty(orderInfos)){
            for (OrderInfo orderInfo : orderInfos) {
                LambdaQueryWrapper<OrderItem> orderItemLambdaQueryWrapper = new LambdaQueryWrapper<>();
                orderItemLambdaQueryWrapper.eq(OrderItem::getOrderId,orderInfo.getId());
                List<OrderItem> orderItems = orderItemMapper.selectList(orderItemLambdaQueryWrapper);
                orderInfo.setOrderItemList(orderItems);
            }
        }
        return  orderInfoPage;

    }

    /**
     * 保存订单
     * @param orderSubmitVo
     * @param cartCheckedList
     * @return
     */
    //要开启事务，这里多次进行insert操作
    @Transactional
    public Long saveOrder(OrderSubmitVo orderSubmitVo, List<CartInfo> cartCheckedList) {
        Long userId = AuthContextHolder.getUserId();
        if(cartCheckedList == null){
            throw  new SsyxException(ResultCodeEnum.DATA_ERROR);
        }
        LeaderAddressVo leaderAddressVo = userFeignClient.getUserAddressByUserId(userId);
        if(null == leaderAddressVo) {
            throw  new SsyxException(ResultCodeEnum.DATA_ERROR);
        }


        /**
         * 封装orderItem
         */

        //计算购物项分摊的优惠减少金额，按比例分摊，退款时按实际支付金额退款
        Map<String, BigDecimal> activitySplitAmountMap = this.computeActivitySplitAmount(cartCheckedList);
        Map<String, BigDecimal> couponInfoSplitAmountMap = this.computeCouponInfoSplitAmount(cartCheckedList, orderSubmitVo.getCouponId());
        //sku对应的订单明细
        List<OrderItem> orderItemList = new ArrayList<>();
        for (CartInfo cartInfo : cartCheckedList) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(null);
            orderItem.setCategoryId(cartInfo.getCategoryId());
            if(cartInfo.getSkuType() == SkuType.COMMON.getCode()) {
                orderItem.setSkuType(SkuType.COMMON);
            } else {
                orderItem.setSkuType(SkuType.SECKILL);
            }
            orderItem.setSkuId(cartInfo.getSkuId());
            orderItem.setSkuName(cartInfo.getSkuName());
            orderItem.setSkuPrice(cartInfo.getCartPrice());
            orderItem.setImgUrl(cartInfo.getImgUrl());
            orderItem.setSkuNum(cartInfo.getSkuNum());
            orderItem.setLeaderId(orderSubmitVo.getLeaderId());
            //活动分解到该商品得优惠金额
            BigDecimal activitySplitAmount = activitySplitAmountMap.get("activity:" + orderItem.getSkuId());
            if(activitySplitAmount ==null ){
                activitySplitAmount = new BigDecimal(0);
            }
            orderItem.setSplitActivityAmount(activitySplitAmount);
            BigDecimal  couponInfoSplitAmount = couponInfoSplitAmountMap.get("coupon:" + orderItem.getSkuId());
            if(couponInfoSplitAmount == null){
                couponInfoSplitAmount = new BigDecimal(0);
            }
            orderItem.setSplitCouponAmount(couponInfoSplitAmount);
            //优惠后的总金额
            BigDecimal skuTotalAmount = orderItem.getSkuPrice().multiply(new BigDecimal(orderItem.getSkuNum()));
            BigDecimal splitTotalAmount = skuTotalAmount.subtract(activitySplitAmount).subtract(couponInfoSplitAmount);
            orderItem.setSplitTotalAmount(splitTotalAmount);
            orderItemList.add(orderItem);
        }


        /**
         * 封装orderInfo
         */
        OrderInfo order = new OrderInfo();
        order.setUserId(userId);
//		private String nickName;
        order.setOrderNo(orderSubmitVo.getOrderNo());
        order.setOrderStatus(OrderStatus.UNPAID);
        order.setProcessStatus(ProcessStatus.UNPAID);
        order.setCouponId(orderSubmitVo.getCouponId());
        order.setLeaderId(orderSubmitVo.getLeaderId());
        order.setLeaderName(leaderAddressVo.getLeaderName());
        order.setLeaderPhone(leaderAddressVo.getLeaderPhone());
        order.setTakeName(leaderAddressVo.getTakeName());
        order.setReceiverName(orderSubmitVo.getReceiverName());
        order.setReceiverPhone(orderSubmitVo.getReceiverPhone());
        order.setReceiverProvince(leaderAddressVo.getProvince());
        order.setReceiverCity(leaderAddressVo.getCity());
        order.setReceiverDistrict(leaderAddressVo.getDistrict());
        order.setReceiverAddress(leaderAddressVo.getDetailAddress());
        order.setWareId(cartCheckedList.get(0).getWareId());
        //计算订单金额
        BigDecimal originalTotalAmount = this.computeTotalAmount(cartCheckedList);
        BigDecimal activityAmount = activitySplitAmountMap.get("activity:total");
        if(null == activityAmount) activityAmount = new BigDecimal(0);
        BigDecimal couponAmount = couponInfoSplitAmountMap.get("coupon:total");
        if(null == couponAmount) couponAmount = new BigDecimal(0);
        BigDecimal totalAmount = originalTotalAmount.subtract(activityAmount).subtract(couponAmount);
        //计算订单金额
        order.setOriginalTotalAmount(originalTotalAmount);
        order.setActivityAmount(activityAmount);
        order.setCouponAmount(couponAmount);
        order.setTotalAmount(totalAmount);
        //计算团长佣金
        BigDecimal profitRate = new BigDecimal(1);
        BigDecimal commissionAmount = order.getTotalAmount().multiply(profitRate);
        order.setCommissionAmount(commissionAmount);

        baseMapper.insert(order);

        /**
         * 添加购物项
         */
        orderItemList.stream().forEach(orderItem -> {
            orderItem.setOrderId(order.getId());
            orderItemMapper.insert(orderItem);
        });
        //修改优惠卷使用状态
        activityFeignClient.updateCouponUserStatus(order.getCouponId(),userId,order.getId());
   //下单成功，记录用户商品购买个数
        String orderSkuKey = RedisConst.ORDER_SKU_MAP + orderSubmitVo.getUserId();
        BoundHashOperations<String, String, Integer> hashOperations = redisTemplate.boundHashOps(orderSkuKey);
        cartCheckedList.forEach(cartInfo -> {
            if(hashOperations.hasKey(cartInfo.getSkuId().toString())) {
                Integer orderSkuNum = hashOperations.get(cartInfo.getSkuId().toString()) + cartInfo.getSkuNum();
                hashOperations.put(cartInfo.getSkuId().toString(), orderSkuNum);
            }
        });
        redisTemplate.expire(orderSkuKey, DateUtil.getCurrentExpireTimes(), TimeUnit.SECONDS);

    return  order.getId();

    }



    //计算总金额
    private BigDecimal computeTotalAmount(List<CartInfo> cartInfoList) {
        BigDecimal total = new BigDecimal(0);
        for (CartInfo cartInfo : cartInfoList) {
            BigDecimal itemTotal = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
            total = total.add(itemTotal);
        }
        return total;
    }

    /**
     * 计算购物项分摊的优惠减少金额
     * 打折：按折扣分担
     * 现金：按比例分摊
     * @param cartInfoParamList
     * @return
     */
    private Map<String, BigDecimal> computeActivitySplitAmount(List<CartInfo> cartInfoParamList) {
        Map<String, BigDecimal> activitySplitAmountMap = new HashMap<>();

        //促销活动相关信息
        List<CartInfoVo> cartInfoVoList = activityFeignClient.findCartActivityList(cartInfoParamList);

        //活动总金额
        BigDecimal activityReduceAmount = new BigDecimal(0);
        if(!CollectionUtils.isEmpty(cartInfoVoList)) {
            for(CartInfoVo cartInfoVo : cartInfoVoList) {
                ActivityRule activityRule = cartInfoVo.getActivityRule();
                List<CartInfo> cartInfoList = cartInfoVo.getCartInfoList();
                if(null != activityRule) {
                    //优惠金额， 按比例分摊
                    BigDecimal reduceAmount = activityRule.getReduceAmount();
                    activityReduceAmount = activityReduceAmount.add(reduceAmount);
                    if(cartInfoList.size() == 1) {
                        activitySplitAmountMap.put("activity:"+cartInfoList.get(0).getSkuId(), reduceAmount);
                    } else {
                        //总金额
                        BigDecimal originalTotalAmount = new BigDecimal(0);
                        for(CartInfo cartInfo : cartInfoList) {
                            BigDecimal skuTotalAmount = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
                            originalTotalAmount = originalTotalAmount.add(skuTotalAmount);
                        }
                        //记录除最后一项是所有分摊金额， 最后一项=总的 - skuPartReduceAmount
                        BigDecimal skuPartReduceAmount = new BigDecimal(0);
                        if (activityRule.getActivityType() == ActivityType.FULL_REDUCTION) {
                            for(int i=0, len=cartInfoList.size(); i<len; i++) {
                                CartInfo cartInfo = cartInfoList.get(i);
                                if(i < len -1) {
                                    BigDecimal skuTotalAmount = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
                                    //sku分摊金额
                                    BigDecimal skuReduceAmount = skuTotalAmount.divide(originalTotalAmount, 2, RoundingMode.HALF_UP).multiply(reduceAmount);
                                    activitySplitAmountMap.put("activity:"+cartInfo.getSkuId(), skuReduceAmount);

                                    skuPartReduceAmount = skuPartReduceAmount.add(skuReduceAmount);
                                } else {
                                    BigDecimal skuReduceAmount = reduceAmount.subtract(skuPartReduceAmount);
                                    activitySplitAmountMap.put("activity:"+cartInfo.getSkuId(), skuReduceAmount);
                                }
                            }
                        } else {
                            for(int i=0, len=cartInfoList.size(); i<len; i++) {
                                CartInfo cartInfo = cartInfoList.get(i);
                                if(i < len -1) {
                                    BigDecimal skuTotalAmount = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));

                                    //sku分摊金额
                                    BigDecimal skuDiscountTotalAmount = skuTotalAmount.multiply(activityRule.getBenefitDiscount().divide(new BigDecimal("10")));
                                    BigDecimal skuReduceAmount = skuTotalAmount.subtract(skuDiscountTotalAmount);
                                    activitySplitAmountMap.put("activity:"+cartInfo.getSkuId(), skuReduceAmount);

                                    skuPartReduceAmount = skuPartReduceAmount.add(skuReduceAmount);
                                } else {
                                    BigDecimal skuReduceAmount = reduceAmount.subtract(skuPartReduceAmount);
                                    activitySplitAmountMap.put("activity:"+cartInfo.getSkuId(), skuReduceAmount);
                                }
                            }
                        }
                    }
                }
            }
        }
        activitySplitAmountMap.put("activity:total", activityReduceAmount);
        return activitySplitAmountMap;
    }

    //优惠卷优惠金额
    private Map<String, BigDecimal> computeCouponInfoSplitAmount(List<CartInfo> cartInfoList, Long couponId) {
        Map<String, BigDecimal> couponInfoSplitAmountMap = new HashMap<>();

        if(null == couponId) return couponInfoSplitAmountMap;
        CouponInfo couponInfo = activityFeignClient.findRangeSkuIdList(cartInfoList, couponId);

        if(null != couponInfo) {
            //sku对应的订单明细
            Map<Long, CartInfo> skuIdToCartInfoMap = new HashMap<>();
            for (CartInfo cartInfo : cartInfoList) {
                skuIdToCartInfoMap.put(cartInfo.getSkuId(), cartInfo);
            }
            //优惠券对应的skuId列表
            List<Long> skuIdList = couponInfo.getSkuIdList();
            if(CollectionUtils.isEmpty(skuIdList)) {
                return couponInfoSplitAmountMap;
            }
            //优惠券优化总金额
            BigDecimal reduceAmount = couponInfo.getAmount();
            if(skuIdList.size() == 1) {
                //sku的优化金额
                couponInfoSplitAmountMap.put("coupon:"+skuIdToCartInfoMap.get(skuIdList.get(0)).getSkuId(), reduceAmount);
            } else {
                //总金额
                BigDecimal originalTotalAmount = new BigDecimal(0);
                for (Long skuId : skuIdList) {
                    CartInfo cartInfo = skuIdToCartInfoMap.get(skuId);
                    BigDecimal skuTotalAmount = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
                    originalTotalAmount = originalTotalAmount.add(skuTotalAmount);
                }
                //记录除最后一项是所有分摊金额， 最后一项=总的 - skuPartReduceAmount
                BigDecimal skuPartReduceAmount = new BigDecimal(0);
                if (couponInfo.getCouponType() == CouponType.CASH || couponInfo.getCouponType() == CouponType.FULL_REDUCTION) {
                    for(int i=0, len=skuIdList.size(); i<len; i++) {
                        CartInfo cartInfo = skuIdToCartInfoMap.get(skuIdList.get(i));
                        if(i < len -1) {
                            BigDecimal skuTotalAmount = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
                            //sku分摊金额
                            BigDecimal skuReduceAmount = skuTotalAmount.divide(originalTotalAmount, 2, RoundingMode.HALF_UP).multiply(reduceAmount);
                            couponInfoSplitAmountMap.put("coupon:"+cartInfo.getSkuId(), skuReduceAmount);

                            skuPartReduceAmount = skuPartReduceAmount.add(skuReduceAmount);
                        } else {
                            BigDecimal skuReduceAmount = reduceAmount.subtract(skuPartReduceAmount);
                            couponInfoSplitAmountMap.put("coupon:"+cartInfo.getSkuId(), skuReduceAmount);
                        }
                    }
                }
            }
            couponInfoSplitAmountMap.put("coupon:total", couponInfo.getAmount());
        }
        return couponInfoSplitAmountMap;
    }
}




