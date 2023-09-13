package com.macro.mall.dto;


import com.macro.mall.model.OmsOrderStage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OmsOrderStageDetail {

    @ApiModelProperty(value = "总租金")
    private BigDecimal grossRent;

    @ApiModelProperty(value = "增值服务")
    private String addServer;

    @ApiModelProperty(value = "买断金额")
    private BigDecimal buyoutPrice;

    @ApiModelProperty(value = "租赁分期数")
    private Integer stageNum;

    @ApiModelProperty(value = "买断方式：0->首期支付 1->尾期支付")
    private Integer buyoutType;

    @ApiModelProperty(value = "支付列表")
    private List<OmsOrderStage> omsOrderStageList;

}
