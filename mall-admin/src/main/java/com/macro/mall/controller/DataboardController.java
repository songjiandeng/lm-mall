package com.macro.mall.controller;


import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.OmsOrderHeadDataVo;
import com.macro.mall.dto.OmsOrderStatus;
import com.macro.mall.dto.OrderLineData;
import com.macro.mall.dto.UmsMemberData;
import com.macro.mall.service.OmsOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@Api(tags = "数据看板")
@Tag(name = "DataboardController", description = "数据看板")
@RequestMapping("/data")
@RequiredArgsConstructor
public class DataboardController {

    @Autowired
    private OmsOrderService omsOrderService;

    @ApiOperation("数据看板头部卡片数据")
    @RequestMapping(value = "/head/data", method = RequestMethod.POST)
    public CommonResult<OmsOrderHeadDataVo> headData() {
        return omsOrderService.headData();
    }

    @ApiOperation("待处理订单")
    @RequestMapping(value = "/home/orderStatus", method = RequestMethod.POST)
    public CommonResult<List<OmsOrderStatus>> orderStatus() {
        return omsOrderService.orderStatus();
    }

    @ApiOperation("首页折线图数据")
    @RequestMapping(value = "/home/line", method = RequestMethod.POST)
    public CommonResult<OrderLineData> lineData(@RequestBody Map<String,String> map) {
        return omsOrderService.lineData(map);
    }

    @ApiOperation("用户信息数据（平台用户，过审占比）")
    @RequestMapping(value = "/home/userInfo", method = RequestMethod.POST)
    public CommonResult<UmsMemberData> userInfo() {
        return omsOrderService.userInfo();
    }



}
