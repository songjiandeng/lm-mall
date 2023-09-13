package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.portal.domain.*;
import com.macro.mall.portal.service.OmsPortalOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 订单管理Controller
 * fisher
 */
@RestController
@Api(tags = "订单管理")
@Tag(name = "OmsPortalOrderController", description = "订单管理")
@RequestMapping("/order")
public class OmsPortalOrderController {
    @Autowired
    private OmsPortalOrderService portalOrderService;

    @ApiOperation("根据购物车信息生成确认单")
    @RequestMapping(value = "/generateConfirmOrder", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<ConfirmOrderResult> generateConfirmOrder(@RequestBody List<Long> cartIds) {
        ConfirmOrderResult confirmOrderResult = portalOrderService.generateConfirmOrder(cartIds);
        return CommonResult.success(confirmOrderResult);
    }

    @ApiOperation("根据购物车信息生成订单")
    @RequestMapping(value = "/generateOrder", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult generateOrder(@RequestBody OrderParam orderParam) {
        Map<String, Object> result = portalOrderService.generateOrder(orderParam);
        return CommonResult.success(result, "下单成功");
    }

    @ApiOperation("用户支付成功的回调")
    @RequestMapping(value = "/paySuccess", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult paySuccess(@RequestParam Long orderId,@RequestParam Integer payType) {
        Integer count = portalOrderService.paySuccess(orderId,payType);
        return CommonResult.success(count, "支付成功");
    }

    @ApiOperation("自动取消超时订单")
    @RequestMapping(value = "/cancelTimeOutOrder", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult cancelTimeOutOrder() {
        portalOrderService.cancelTimeOutOrder();
        return CommonResult.success(null);
    }

    @ApiOperation("取消单个超时订单")
    @RequestMapping(value = "/cancelOrder", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult cancelOrder(Long orderId) {
        portalOrderService.sendDelayMessageCancelOrder(orderId);
        return CommonResult.success(null);
    }

    @ApiOperation("按状态分页获取用户订单列表")
    @ApiImplicitParam(name = "status", value = "订单状态：-1->全部；0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭",
            defaultValue = "-1", allowableValues = "-1,0,1,2,3,4", paramType = "query", dataType = "int")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<OmsOrderDetail>> list(@RequestParam Integer status,
                                                   @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                                   @RequestParam(required = false, defaultValue = "5") Integer pageSize) {
        CommonPage<OmsOrderDetail> orderPage = portalOrderService.list(status,pageNum,pageSize);
        return CommonResult.success(orderPage);
    }

    @ApiOperation("根据ID获取订单详情")
    @RequestMapping(value = "/detail/{orderId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<OmsOrderDetail> detail(@PathVariable Long orderId) {
        OmsOrderDetail orderDetail = portalOrderService.detail(orderId);
        return CommonResult.success(orderDetail);
    }

    @ApiOperation("用户取消订单")
    @RequestMapping(value = "/cancelUserOrder", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult cancelUserOrder(Long orderId) {
        portalOrderService.cancelOrder(orderId);
        return CommonResult.success(null);
    }

    @ApiOperation("用户确认收货")
    @RequestMapping(value = "/confirmReceiveOrder", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult confirmReceiveOrder(Long orderId) {
        portalOrderService.confirmReceiveOrder(orderId);
        return CommonResult.success(null);
    }

    @ApiOperation("用户删除订单")
    @RequestMapping(value = "/deleteOrder", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult deleteOrder(Long orderId) {
        portalOrderService.deleteOrder(orderId);
        return CommonResult.success(null);
    }

    @ApiOperation("订单预览")
    @RequestMapping(value = "/orderPreview", method = RequestMethod.POST)
    public CommonResult<OrderPreviewDetail> orderPreview(@RequestBody OmsOrderCreateParam omsOrderCreateParam){
        return portalOrderService.orderPreview(omsOrderCreateParam);
    }

    @ApiOperation("申请租赁")
    @RequestMapping(value = "/applyforLease", method = RequestMethod.POST)
    public CommonResult<String> applyforLease(@RequestBody OmsOrderCreateParam omsOrderCreateParam){
        return portalOrderService.applyforLease(omsOrderCreateParam);
    }

    @ApiOperation("获取用户订单详情")
    @RequestMapping(value = "/orderDetail", method = RequestMethod.POST)
    public CommonResult<OrderPreviewDetail> orderDetail(@RequestBody OmsOrderCreateParam omsOrderCreateParam){
        return portalOrderService.orderDetail(omsOrderCreateParam);
    }

    @ApiOperation("获取用户订单列表")
    @RequestMapping(value = "/orderList", method = RequestMethod.POST)
    public CommonPage<OrderPreviewDetail> orderList(@RequestBody OmsOrderCreateParam omsOrderCreateParam){
        return portalOrderService.orderList(omsOrderCreateParam);
    }

    @ApiOperation("待处理订单")
    @RequestMapping(value = "/home/orderStatus", method = RequestMethod.POST)
    public CommonResult<List<OmsOrderStatus>> orderStatus(@RequestBody OmsOrderCreateParam omsOrderCreateParam) {
        return portalOrderService.orderStatus(omsOrderCreateParam);
    }

    /**
     * 01 02 09 11 13 14|0804 06 13 15 16 25|0505 15 18 23 24 28|0401 15 21 24 28 32|10
     * 02 08 15 23 27 33|0806 22 24 26 31 33|0314 17 18 22 25 30|1303 06 12 13 22 24|03
     * 04 05 06 19 30 32|1501 14 20 22 28 29|0308 18 21 25 27 33|1312 15 21 24 28 33|08
     * 04 20 22 24 25 27|0606 17 22 24 27 29|1519 20 22 24 26 30|0502 05 11 16 20 25|09
     * 08 12 18 19 20 22|0308 09 23 24 25 26|0604 07 08 19 24 25|0606 08 12 19 26 28|14
     * 01 09 10 16 21 22|0701 02 11 16 24 31|1407 08 12 16 24 32|0306 13 17 18 19 22|06
     * 04 08 15 21 27 32|0303 17 23 24 31 33|1111 15 16 18 19 21|0909 11 15 21 24 26|09
     */
}
