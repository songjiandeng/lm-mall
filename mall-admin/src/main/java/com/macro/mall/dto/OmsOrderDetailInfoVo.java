package com.macro.mall.dto;


import com.macro.mall.common.api.CommonPage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OmsOrderDetailInfoVo {

    @ApiModelProperty("订单列表数据")
    CommonPage<OmsOrderDetail> omsOrderDetailCommonPage;

    @ApiModelProperty("总订单数")
    private Integer allNum;

    @ApiModelProperty("审核订单数")
    private Integer processNum;

    @ApiModelProperty("待支付订单数")
    private Integer payOffNum;

    @ApiModelProperty("代发货订单数")
    private Integer consignmentNum;

    @ApiModelProperty("待收货订单数")
    private Integer receivedNum;

    @ApiModelProperty("租用中订单数")
    private Integer usedNum;

    @ApiModelProperty("订单完成数")
    private Integer completeNum;

    @ApiModelProperty("订单关闭")
    private Integer closeNum;

}
