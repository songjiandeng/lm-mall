package com.macro.mall.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OmsDueOrOverOrderDetail {

    @ApiModelProperty("订单ID")
    private Long id;

//    @ApiModelProperty("账单id")
//    private List<Long> stageId;

    @ApiModelProperty(value = "订单编号")
    private String orderSn;

    @ApiModelProperty(value = "商品货号")
    private String productSn;

    private String productPic;//图片

    private String productName;//商品名称

    @ApiModelProperty(value = "订单总金额，及租金")
    private BigDecimal totalAmount;

    @ApiModelProperty(value = "租赁分期数")
    private Integer stageNum;

    @ApiModelProperty(value = "支付期数")
    private Integer payNum;

    @ApiModelProperty(value = "本期金额")
    private BigDecimal currentPrice;

    @ApiModelProperty(value = "支付金额")
    private BigDecimal payPrice;

    @ApiModelProperty(value = "还款状态")
    private Integer payStatus;

    @ApiModelProperty(value = "还款时间")
    private String payTime;

    @ApiModelProperty(value = "到期时间")
    private String repaymentTime;

    @ApiModelProperty(value = "用户帐号")
    private String memberUsername;

    @ApiModelProperty(value = "用户手机号")
    private String userPhone;

    @ApiModelProperty(value = "渠道来源，渠道名称")
    private String channelName;

    @ApiModelProperty(value = "归属成员名称")
    private String adminName;

    @ApiModelProperty(value = "操作人员")
    private String operateMan;

    @ApiModelProperty(value = "剩余未还总金额，包括租金，违约金")
    private BigDecimal reducePrice;

    @ApiModelProperty(value = "租金")
    private BigDecimal rentPrice;

    @ApiModelProperty(value = "违约金")
    private BigDecimal overPrice;

    @ApiModelProperty(value = "逾期次数")
    private Integer overNum;

    @ApiModelProperty(value = "逾期天数")
    private Integer overDays;

    @ApiModelProperty(value = "累计还款")
    private BigDecimal repaymentAllPrice;

    @ApiModelProperty(value = "最近还款时间")
    private String lastReturnTime;

}
