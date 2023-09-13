package com.macro.mall.controller;

import cn.hutool.json.JSONUtil;
import com.github.pagehelper.PageHelper;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.enums.ConstantTypesEnum;
import com.macro.mall.dto.*;
import com.macro.mall.service.OmsOrderService;
import com.macro.mall.service.OrderOperateStrategy;
import com.macro.mall.strategy.OrderOperateStrategyFactory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 订单管理Controller
 */
@RestController
@Api(tags = "订单管理")
@Tag(name = "OmsOrderController", description = "订单管理")
@RequestMapping("/order")
@RequiredArgsConstructor
public class OmsOrderController {

    @Autowired
    private OmsOrderService orderService;

    @ApiOperation("查询订单列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public CommonResult<OmsOrderDetailInfoVo> list(@RequestBody OmsOrderQueryParam queryParam) {
        OmsOrderDetailInfoVo vo = orderService.list(queryParam);
        return CommonResult.success(vo);
    }

    @ApiOperation("批量发货")
    @RequestMapping(value = "/update/delivery", method = RequestMethod.POST)
    public CommonResult delivery(@RequestBody List<OmsOrderDeliveryParam> deliveryParamList) {
        int count = orderService.delivery(deliveryParamList);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("批量关闭订单")
    @RequestMapping(value = "/update/close", method = RequestMethod.POST)
    public CommonResult close(@RequestParam("ids") String ids, @RequestParam String note, @RequestParam Long operateId, @RequestParam Integer status) {
        List<Long> longs = JSONUtil.toList(ids, Long.class);
        int count = orderService.close(longs, note, operateId, status);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

//    @ApiOperation("批量删除订单")
//    @RequestMapping(value = "/delete", method = RequestMethod.POST)
//    public CommonResult delete(@RequestParam("ids") List<Long> ids) {
//        int count = orderService.delete(ids);
//        if (count > 0) {
//            return CommonResult.success(count);
//        }
//        return CommonResult.failed();
//    }

    @ApiOperation("获取订单详情：订单信息、商品信息、操作记录")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public CommonResult<OmsOrderDetail> detail(@PathVariable Long id) {
        OmsOrderDetail orderDetailResult = orderService.detail(id);
        return CommonResult.success(orderDetailResult);
    }

    @ApiOperation("修改收货人信息")
    @RequestMapping(value = "/update/receiverInfo", method = RequestMethod.POST)
    public CommonResult updateReceiverInfo(@RequestBody OmsReceiverInfoParam receiverInfoParam) {
        int count = orderService.updateReceiverInfo(receiverInfoParam);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

//    @ApiOperation("修改订单费用信息")
//    @RequestMapping(value = "/update/moneyInfo", method = RequestMethod.POST)
//    public CommonResult updateReceiverInfo(@RequestBody OmsMoneyInfoParam moneyInfoParam) {
//        int count = orderService.updateMoneyInfo(moneyInfoParam);
//        if (count > 0) {
//            return CommonResult.success(count);
//        }
//        return CommonResult.failed();
//    }

    @ApiOperation("备注订单")
    @RequestMapping(value = "/update/note", method = RequestMethod.POST)
    public CommonResult updateNote(@RequestParam("id") Long id,
                                   @RequestParam("adminId") Long adminId,
                                   @RequestParam("note") String note,
                                   @RequestParam("status") Integer status) {
        int count = orderService.updateNote(id, note, status, adminId);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("订单各种状态操作")
    @RequestMapping(value = "/operate/order", method = RequestMethod.POST)
    public CommonResult operateOrder(@RequestBody OmsOrderQueryParam orderQueryParam) {
        Integer status = orderQueryParam.getStatus();
        ConstantTypesEnum.OrderStatus orderStatus = ConstantTypesEnum.OrderStatus.getByType(status);
        if (orderStatus != null) {
            OrderOperateStrategy strategy =
                    OrderOperateStrategyFactory.getStrategy(orderStatus);
            if (strategy != null) {
                //对应操作执行对应策略
                Object param = strategy.operateOrder(orderQueryParam);
                if (!Objects.isNull(param)) {
                    return CommonResult.success(param);
                }
            }
        }
        return CommonResult.failed();
    }

    @ApiOperation("查询到期订单")
    @RequestMapping(value = "/due/list", method = RequestMethod.POST)
    public CommonResult<CommonPage<OmsDueOrOverOrderDetail>> dueList(@RequestBody OmsOrderQueryParam queryParam) {
        return CommonResult.success(orderService.dueList(queryParam));
    }

    @ApiOperation("查询逾期订单")
    @RequestMapping(value = "/over/list", method = RequestMethod.POST)
    public CommonResult<CommonPage<OmsDueOrOverOrderDetail>> overList(@RequestBody OmsOrderQueryParam queryParam) {
        return CommonResult.success(orderService.overList(queryParam));
    }
}
