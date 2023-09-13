package com.macro.mall.dto;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单查询参数
 * fisher
 */
@Getter
@Setter
public class OmsOrderQueryParam extends OmsOrderCreateParam{
    @ApiModelProperty(value = "订单id")
    private Long id;

    @ApiModelProperty(value = "订单编号")
    private String orderSn;
    @ApiModelProperty(value = "收货人姓名/号码")
    private String receiverKeyword;
    @ApiModelProperty(value = "商品名称")
    private String itemName;
    @ApiModelProperty(value = "用户帐号")
    private String memberUsername;
    @ApiModelProperty(value = "用户手机号")
    private String userPhone;
    @ApiModelProperty(value = "订单操作：-1 查看订单详情, 0->待审核；1->待支付；2->代发货；3->待收货；4->租用中；5->逾期；6->订单完成；7->订单关闭 10-> 查看历史订单 8->线下归还 9->订单备注  11->物流信息 12-> 账单信息 13 ->审核拒绝 14 ->审核通过 15 ->订单签收 16->订单创建 17->催收记录")
    private Integer status;

    @ApiModelProperty(value = "状态: 1 待归还 2 已归还 3 逾期")
    private Integer stageStatus;

    @ApiModelProperty(value = "13 审核拒绝 14 审核通过")
    private Integer state;
    @ApiModelProperty(value = "订单提交时间")
    private String createTime;
    @ApiModelProperty(value = "归属成员id")
    private Long adminId;
    @ApiModelProperty(value = "渠道id")
    private Long channelId;
    @ApiModelProperty(value = "审核id")
    private Long operateId;
    @ApiModelProperty(value = "到期时间结束")
    private String expEndTime;
    @ApiModelProperty(value = "到期时间开始")
    private String expStartTime;
    @ApiModelProperty(value = "还款开始时间")
    private String returnSTime;
    @ApiModelProperty(value = "还款结束时间")
    private String returnETime;
    @ApiModelProperty(value = "线下支付金额")
    private BigDecimal offlinePrice;
    @ApiModelProperty(value = "账单id,空默认为第一期")
    private List<Long> stageId;
    @ApiModelProperty(value = "页码")
    private Integer pageNum;

    @ApiModelProperty(value = "数量")
    private Integer pageSize;

    @ApiModelProperty(value = "逾期次数")
    private Integer overNum;

    @ApiModelProperty(value = "逾期天数")
    private Integer overDays;

    @ApiModelProperty(value = "是否渠道订单 0 否 1 是")
    private Integer isChannel;

    @ApiModelProperty(value = "审核记录")
    private String processNote;

//    @ApiModelProperty(value = "订单创建入参")
//    private OmsOrderCreateParam omsOrderCreateParam;

    private List<Long> memberIds;

    private List<Long> orderIds;
}
