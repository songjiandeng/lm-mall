package com.macro.mall.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OmsOrderHeadDataVo {

    @ApiModelProperty("订单总额")
    private BigDecimal totalAmount;

    @ApiModelProperty("已回款")
    private BigDecimal received;

    @ApiModelProperty("待回款")
    private BigDecimal pending;

    @ApiModelProperty("增值服务")
    private BigDecimal addedPrice;

    @ApiModelProperty("历史总订单")
    private Integer historyNum;

    @ApiModelProperty("今日新增")
    private Integer todayNum;

    @ApiModelProperty("本月新增")
    private Integer monthNum;

    @ApiModelProperty("今日到期订单")
    private Integer todayDueNum;

    @ApiModelProperty("今日到期订单金额")
    private BigDecimal todayDuePrice;

    @ApiModelProperty("今日已回收")
    private Integer recoveryNum;

    @ApiModelProperty("今日已回收金额")
    private BigDecimal recoveryPrice;

    @ApiModelProperty("未回收")
    private Integer unrecycledNum;

    @ApiModelProperty("未回收金额")
    private BigDecimal unrecycledPrice;

    @ApiModelProperty("逾期未归还")
    private Integer overUnrecycledNum;

    @ApiModelProperty("逾期金额")
    private BigDecimal overPrice;

    @ApiModelProperty("坏账率")
    private BigDecimal badRate;
}
