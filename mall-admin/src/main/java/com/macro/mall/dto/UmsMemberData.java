package com.macro.mall.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UmsMemberData {

    @ApiModelProperty(value = "累计总用户数")
    private Integer totalMemberNum;

    @ApiModelProperty(value = "申请用户数")
    private Integer applyMemberNum;

    @ApiModelProperty(value = "下单转化率")
    private BigDecimal orderRate;

    @ApiModelProperty(value = "拒审占比")
    private BigDecimal refuseRate;

    @ApiModelProperty(value = "过审占比")
    private BigDecimal passRate;

    @ApiModelProperty(value = "拒审数")
    private Integer refuseNum;

    @ApiModelProperty(value = "过审数")
    private Integer passNum;
}
