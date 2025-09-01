package com.atnjupt.sqyxgo.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单超时Demo请求参数
 */
@Data
@ApiModel(description = "订单超时Demo请求参数")
public class OrderTimeoutDemoVo {
    
    @ApiModelProperty(value = "用户昵称")
    private String nickName;
    
    @ApiModelProperty(value = "订单总额")
    private BigDecimal totalAmount;
    
    @ApiModelProperty(value = "订单备注")
    private String remark;
}
