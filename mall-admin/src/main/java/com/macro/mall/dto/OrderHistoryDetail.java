package com.macro.mall.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: fisher
 * @Date: 2023/8/20 10:56
 */

@Data
public class OrderHistoryDetail {

    @ApiModelProperty("订单数")
    private  Integer orderNum;

    @ApiModelProperty(value = "全部租金")
    private BigDecimal rentAmount;

    @ApiModelProperty(value = "归还租金")
    private BigDecimal returnAmount;

    @ApiModelProperty(value = "待归还租金")
    private BigDecimal toAmount;

    @ApiModelProperty(value = "历史商品信息")
    private List<ItemVo> list;

    @ApiModelProperty("逾期数")
    private  Integer overNum;
}
