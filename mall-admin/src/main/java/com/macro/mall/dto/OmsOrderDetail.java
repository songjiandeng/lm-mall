package com.macro.mall.dto;

import com.macro.mall.model.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单详情信息
 * fisher
 */
@Data
public class OmsOrderDetail extends OmsOrder {

    @ApiModelProperty("订单商品列表")
    private List<OmsOrderItem> orderItemList;

    @ApiModelProperty("订单操作记录列表")
    private List<OmsOrderOperateHistory> historyList;

    @ApiModelProperty("用户信息")
    private UmsMember umsMember;

    @ApiModelProperty("在租订单数")
    private  Integer usedNum;

    @ApiModelProperty("订单完成数")
    private Integer completeNum;

    @ApiModelProperty(value = "在租订单详情")
    private OrderHistoryDetail orderUsedDetail;

    @ApiModelProperty(value = "完结订单详情")
    private OrderHistoryDetail orderComplateDetail;

    @ApiModelProperty(value = "账单信息")
    private OmsOrderStageDetail omsOrderStageDetail;

    @ApiModelProperty(value = "首期租金")
    private BigDecimal firstPrice;

    @ApiModelProperty(value = "已支付金额")
    private BigDecimal payPrice;

    @ApiModelProperty(value = "已经支付期数")
    private Integer payNum;
}
