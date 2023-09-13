package com.macro.mall.portal.domain;

import com.macro.mall.model.OmsOrderStage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class OrderPreviewDetail {

    private String pic;

    @ApiModelProperty(value = "商品名称")
    private String name;

    private Long skuId;

    @ApiModelProperty(value = "商品销售属性，json格式")
    private String spData;

    private Long orderId;

    @ApiModelProperty(value = "订单备注")
    private String note;

    @ApiModelProperty(value = "租赁天数")
    private Integer leaseNum;

    @ApiModelProperty(value = "起租时间")
    private Date startTime;

    @ApiModelProperty(value = "到期时间")
    private Date endTime;

    @ApiModelProperty(value = "首期支付金额/到期支付金额")
    private BigDecimal firstPayPrice;

    @ApiModelProperty(value = "增值金额")
    private BigDecimal addServerPrice;

    @ApiModelProperty(value = "增值服务")
    private String addServer;

    @ApiModelProperty(value = "首期租金/到期租金")
    private BigDecimal firstRentPrice;

    @ApiModelProperty(value = "剩余归还金额")
    private BigDecimal residuePrice;

    @ApiModelProperty(value = "剩余总租金")
    private BigDecimal totalRentPrice;

    @ApiModelProperty(value = "买断金额")
    private BigDecimal buyoutPrice;

    @ApiModelProperty(value = "买断方式：0->首期支付 1->尾期支付")
    private Integer buyoutType;

    @ApiModelProperty(value = "还款方式：0->非灵活还款 1->灵活还款")
    private Integer repaymentType;

    @ApiModelProperty(value = "是否实名：0 未实名 1 实名")
    private Integer isReal;

    @ApiModelProperty(value = "订单状态：0->待审核；1->待支付；2->代发货；3->待收货；4->租用中；5->逾期；6->订单完成；7->订单关闭 11->审核拒绝")
    private Integer status;

    @ApiModelProperty(value = "订单编号")
    private String orderSn;

    @ApiModelProperty(value = "订单创建时间")
    private Date createTime;

    @ApiModelProperty(value = "即将到期天数")
    private Integer dueDay;

    @ApiModelProperty(value = "逾期天数")
    private Integer overDay;

    @ApiModelProperty(value = "逾期金额")
    private BigDecimal overPrice;

    @ApiModelProperty(value = "租赁分期数")
    private Integer stageNum;


    List<OmsOrderStage> omsOrderStages;


}
