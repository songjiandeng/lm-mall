package com.macro.mall.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 用户注册参数
 * fisher
 */
@Getter
@Setter
public class UmsAdminParam {
    @NotEmpty
    @ApiModelProperty(value = "用户名", required = true)
    private String username;
    @NotEmpty
    @ApiModelProperty(value = "密码", required = true)
    private String password;
    @ApiModelProperty(value = "用户头像")
    private String icon;
    @Email
    @ApiModelProperty(value = "邮箱")
    private String email;
    @ApiModelProperty(value = "用户昵称")
    private String nickName;
    @ApiModelProperty(value = "备注")
    private String note;

    @NotEmpty
    @ApiModelProperty(value = "手机号")
    private String phone;

    @NotNull
    @ApiModelProperty(value = "用户类型：0->普通用户；1->渠道用户")
    private Integer type;

    private Integer status;

    @ApiModelProperty(value = "推广id，渠道用户才有")
    private String promotionId;

    @ApiModelProperty(value = "部门ID")
    private List<Long> roleId;
}
