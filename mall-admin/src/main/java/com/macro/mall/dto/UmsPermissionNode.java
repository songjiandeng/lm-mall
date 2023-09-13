package com.macro.mall.dto;

import com.macro.mall.model.UmsPermission;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 后台权限节点封装
 * fisher
 */
@Getter
@Setter
public class UmsPermissionNode extends UmsPermission {
    @ApiModelProperty(value = "子级菜单")
    private List<UmsPermissionNode> children;
}
