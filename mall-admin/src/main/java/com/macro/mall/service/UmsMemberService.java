package com.macro.mall.service;

import com.macro.mall.dto.UmsMemberDetail;
import com.macro.mall.dto.UmsMemberParamDto;
import com.macro.mall.model.UmsMember;

import java.util.List;

public interface UmsMemberService {
    List<UmsMember> list(UmsMemberParamDto umsMemberParamDto);

    int batchUpdateMark(UmsMemberParamDto umsMemberParamDto);

    int batchUpdateAdmin(UmsMemberParamDto umsMemberParamDto);

    int batchUpdateRole(UmsMemberParamDto umsMemberParamDto);

    int update(Long id, UmsMember umsMember);

    List<UmsMember> channelList(UmsMemberParamDto umsMemberParamDto);

    int register(UmsMemberParamDto umsMember);

    UmsMemberDetail getItem(Long id);
}
