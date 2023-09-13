package com.macro.mall.service;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 订单管理Service
 * fisher
 */
public interface OmsOrderService {
    /**
     * 订单查询
     * @return
     */
    OmsOrderDetailInfoVo list(OmsOrderQueryParam queryParam);

    /**
     * 批量发货
     */
    @Transactional
    int delivery(List<OmsOrderDeliveryParam> deliveryParamList);

    /**
     * 批量关闭订单
     */
    @Transactional
    int close(List<Long> ids, String note, Long operateId, Integer status);

    /**
     * 批量删除订单
     */
    int delete(List<Long> ids);

    /**
     * 获取指定订单详情
     */
    OmsOrderDetail detail(Long id);

    /**
     * 修改订单收货人信息
     */
    @Transactional
    int updateReceiverInfo(OmsReceiverInfoParam receiverInfoParam);

    /**
     * 修改订单费用信息
     */
    @Transactional
    int updateMoneyInfo(OmsMoneyInfoParam moneyInfoParam);

    /**
     * 修改订单备注
     */
    @Transactional
    int updateNote(Long id, String note, Integer status, Long adminId);

    CommonPage<OmsDueOrOverOrderDetail> dueList(OmsOrderQueryParam queryParam);

    CommonPage<OmsDueOrOverOrderDetail> overList(OmsOrderQueryParam queryParam);

    CommonResult<OmsOrderHeadDataVo> headData();

    CommonResult<List<OmsOrderStatus>> orderStatus();

    CommonResult<OrderLineData> lineData(Map<String, String> map);

    CommonResult<UmsMemberData> userInfo();
}
