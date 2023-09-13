package com.macro.mall.dto;

import com.macro.mall.model.UmsRole;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: fisher
 * @Date: 2023/8/17 10:08
 */

@Data
public class UmsRolePermissionRelationDto {

    @ApiModelProperty("部门")
    private UmsRole umsRole;

    @ApiModelProperty("权限列表主键id")
    private List<Long> permissionIds;
}
