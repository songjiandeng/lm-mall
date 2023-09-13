package com.macro.mall.dto;

import com.macro.mall.model.UmsMember;
import lombok.Data;

import java.util.List;

@Data
public class UmsMemberDetail extends UmsMember {
    List<UmsMemberOrder> umsMemberOrderList;
}
