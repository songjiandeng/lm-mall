package com.macro.mall.dto;


import io.swagger.models.auth.In;
import lombok.Data;

@Data
public class UmsMemberOrder {

    private String orderSn;

    private Integer leaseNum;

    private Integer stageNum;

    private Integer status;

    private Integer returnNum;

}
