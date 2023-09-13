package com.macro.mall.dto;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.model.UmsMember;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;



/**
 * @Author: fisher
 * @Date: 2023/8/18 10:10
 */

@Data
public class UmsChannelMemberVo {

    @ApiModelProperty(value = "成单用户数")
    private Integer orderUserNum;

    @ApiModelProperty(value = "渠道用户总数")
    private Integer channelUserNum;

    @ApiModelProperty(value = "列表分页数据")
    private CommonPage<UmsMember> memberVoList;
}
