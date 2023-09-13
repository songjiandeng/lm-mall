package com.macro.mall.dto;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: huangzc
 * @Date: 2023/8/20 11:10
 */
@Data
public class ItemVo {

    @ApiModelProperty(value = "订单id")
    private Long orderId;

    @ApiModelProperty(value = "订单编号")
    private String orderSn;

    private Long productId;//商品id

    private String productPic;//图片

    private String productName;//商品名称

    private String productSn;//商品货号

    @ApiModelProperty(value = "销售价格，租金")
    private BigDecimal productPrice;

    @ApiModelProperty(value = "下单时间")
    private String orderTime;

    @ApiModelProperty(value = "订单状态")
    private Integer status;

    @ApiModelProperty(value = "归还期数")
    private Integer returnNum;

    @ApiModelProperty(value = "未还期数")
    private Integer toNum;

    @ApiModelProperty(value = "逾期期数")
    private Integer overNum;

}
