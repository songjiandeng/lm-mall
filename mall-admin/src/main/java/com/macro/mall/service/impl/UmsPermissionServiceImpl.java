package com.macro.mall.service.impl;

import com.macro.mall.dto.UmsPermissionNode;
import com.macro.mall.mapper.UmsPermissionMapper;
import com.macro.mall.model.UmsPermission;
import com.macro.mall.model.UmsPermissionExample;
import com.macro.mall.service.UmsPermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: fisher
 * @Date: 2023/8/17 10:45
 */
@Service
@Slf4j
public class UmsPermissionServiceImpl implements UmsPermissionService {

    @Autowired
    private UmsPermissionMapper umsPermissionMapper;

    @Override
    public List<UmsPermissionNode> listPermission() {
        List<UmsPermissionNode> result = new ArrayList<>();
        try {
            //获取权限列表，树形结构
            List<UmsPermission> umsPermissions = umsPermissionMapper.selectByExample(new UmsPermissionExample());
            result = umsPermissions.stream()
                    .filter(menu -> menu.getPid().equals(0L))
                    .map(menu -> covertPermissionNode(menu, umsPermissions))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private UmsPermissionNode covertPermissionNode(UmsPermission permission, List<UmsPermission> umsPermissions) {
        UmsPermissionNode node = new UmsPermissionNode();
        BeanUtils.copyProperties(permission, node);
        List<UmsPermissionNode> children = umsPermissions.stream()
                .filter(subMenu -> subMenu.getPid().equals(permission.getId()))
                .map(subMenu -> covertPermissionNode(subMenu, umsPermissions)).collect(Collectors.toList());
        node.setChildren(children);
        return node;
    }
}
