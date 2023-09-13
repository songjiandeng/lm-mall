package com.macro.mall.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
public class UmsMemberParamDto {

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "是否实名：0 未实名 1 实名")
    private Integer isReal;

    @ApiModelProperty(value = "归属成员id")
    private Long adminId;

    @ApiModelProperty(value = "成单数量")
    private Integer orderNum;

    @ApiModelProperty(value = "用户来源,注册来源")
    private Integer sourceType;

    @ApiModelProperty(value = "渠道id,就是adminId")
    private Long channelId;

    @ApiModelProperty(value = "注册开始时间")
    private String startTime;

    @ApiModelProperty(value = "注册结束时间")
    private String endTime;

    @ApiModelProperty(value = "页码")
    private Integer pageNum;

    @ApiModelProperty(value = "数量")
    private Integer pageSize;

    @ApiModelProperty(value = "用户id")
    private List<Long> ids;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "是否成单：-1 全部 0 未 1成单")
    private Integer isOrder;

    @ApiModelProperty(value = "推广id，渠道推广注册")
    private String promotionId;



}
