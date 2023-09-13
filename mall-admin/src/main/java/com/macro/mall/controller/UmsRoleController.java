package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.UmsPermissionNode;
import com.macro.mall.dto.UmsRolePermissionRelationDto;
import com.macro.mall.model.*;
import com.macro.mall.service.UmsRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 后台用户部门管理
 * fisher
 */
@RestController
@Api(tags = "部门管理")
@Tag(name = "UmsRoleController", description = "后台部门管理")
@RequestMapping("/role")
@RequiredArgsConstructor
public class UmsRoleController {
    private final UmsRoleService roleService;


    @ApiOperation("添加部门")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public CommonResult create(@RequestBody UmsRolePermissionRelationDto umsRolePermissionRelationDto) {
        int count = roleService.create(umsRolePermissionRelationDto);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("修改部门")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    public CommonResult update(@PathVariable Long id, @RequestBody UmsRole role) {
        int count = roleService.update(id, role);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("批量删除")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public CommonResult delete(@RequestParam("ids") List<Long> ids) {
        int count = roleService.delete(ids);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("获取所有部门")
    @RequestMapping(value = "/listAll", method = RequestMethod.GET)
    public CommonResult<List<UmsRole>> listAll() {
        List<UmsRole> roleList = roleService.list();
        return CommonResult.success(roleList);
    }

    @ApiOperation("获取部门列表(分页)")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<UmsRole>> list(@RequestParam(value = "keyword", required = false) String keyword,
                                                  @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                                                  @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        List<UmsRole> roleList = roleService.list(keyword, pageSize, pageNum);
        for (UmsRole umsRole : roleList) {
            List<UmsPermission> umsPermissions = roleService.listPrivilege(umsRole.getId());
            if(CollectionUtils.isNotEmpty(umsPermissions)){
                umsRole.setPrivilegeIds(umsPermissions.stream().map(UmsPermission::getId).collect(Collectors.toList()));
            }
        }
        return CommonResult.success(CommonPage.restPage(roleList));
    }

    @ApiOperation("修改部门状态")
    @RequestMapping(value = "/updateStatus/{id}", method = RequestMethod.POST)
    public CommonResult updateStatus(@PathVariable Long id, @RequestParam(value = "status") Integer status) {
        UmsRole umsRole = new UmsRole();
        umsRole.setStatus(status);
        int count = roleService.update(id, umsRole);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
/*
    @ApiOperation("获取相关权限菜单")
    @RequestMapping(value = "/listMenu/{roleId}", method = RequestMethod.GET)
    public CommonResult<List<UmsMenu>> listMenu(@PathVariable Long roleId) {
        List<UmsMenu> roleList = roleService.listMenu(roleId);
        return CommonResult.success(roleList);
    }*/

   /* @ApiOperation("获取相关资源")
    @RequestMapping(value = "/listResource/{roleId}", method = RequestMethod.GET)
    public CommonResult<List<UmsResource>> listResource(@PathVariable Long roleId) {
        List<UmsResource> roleList = roleService.listResource(roleId);
        return CommonResult.success(roleList);
    }*/

    @ApiOperation("获取部门相关权限")
    @RequestMapping(value = "/listPrivilege/{roleId}", method = RequestMethod.GET)
    public CommonResult<List<UmsPermission>> listPrivilege(@PathVariable Long roleId) {
        List<UmsPermission> roleList = roleService.listPrivilege(roleId);
        return CommonResult.success(roleList);
    }

   /* @ApiOperation("给部门分配菜单")
    @RequestMapping(value = "/allocMenu", method = RequestMethod.POST)
    public CommonResult allocMenu(@RequestParam Long roleId, @RequestParam List<Long> menuIds) {
        int count = roleService.allocMenu(roleId, menuIds);
        return CommonResult.success(count);
    }*/

    /*@ApiOperation("给部门分配资源")
    @RequestMapping(value = "/allocResource", method = RequestMethod.POST)
    public CommonResult allocResource(@RequestParam Long roleId, @RequestParam List<Long> resourceIds) {
        int count = roleService.allocResource(roleId, resourceIds);
        return CommonResult.success(count);
    }*/


    @ApiOperation("给部门绑定权限")
    @RequestMapping(value = "/allocPermission", method = RequestMethod.POST)
    public CommonResult allocPermission(@RequestParam Long roleId, @RequestParam List<Long> permissionIds) {
        int count = roleService.allocPermission(roleId, permissionIds);
        return CommonResult.success(count);
    }

}
