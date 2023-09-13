package com.macro.mall.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.UmsPermissionNode;
import com.macro.mall.service.UmsPermissionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: fisher
 * @Date: 2023/8/17 10:01
 */

@RestController
@Api(tags = "权限管理")
@Tag(name = "UmsPermissionController", description = "后台权限管理")
@RequestMapping("/permission")
@RequiredArgsConstructor
public class UmsPermissionController {

    @Autowired
    private UmsPermissionService umsPermissionService;

    @ApiOperation("权限列表获取")
    @RequestMapping(value = "/listPermission", method = RequestMethod.POST)
    public CommonResult listPermission() {
        List<UmsPermissionNode> list = umsPermissionService.listPermission();
        return CommonResult.success(list);
    }
}
