package com.macro.mall.strategy.order;

import cn.hutool.json.JSONUtil;
import com.kuaidi100.sdk.response.QueryTrackData;
import com.kuaidi100.sdk.response.QueryTrackResp;
import com.macro.mall.common.enums.ConstantTypesEnum;
import com.macro.mall.common.util.Kuaidi100Util;
import com.macro.mall.dto.OmsOrderDetail;
import com.macro.mall.dto.OmsOrderQueryParam;
import com.macro.mall.service.OrderOperateStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class OrderLogisticsStrategy implements OrderOperateStrategy<List<QueryTrackData>> {

    @Autowired
    private Kuaidi100Util kuaidi100Util;

    @Override
    public List<QueryTrackData> operateOrder(OmsOrderQueryParam omsOrderQueryParam) {
        log.info("【订单操作】【物流详情】【入参】：{}", JSONUtil.toJsonStr(omsOrderQueryParam));
        try {
            String receiverPhone = omsOrderQueryParam.getReceiverPhone();
            String deliveryCompany = omsOrderQueryParam.getDeliveryCompany();
            String deliverySn = omsOrderQueryParam.getDeliverySn();
            return kuaidi100Util.queryTrace(deliveryCompany, deliverySn, receiverPhone);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ConstantTypesEnum.OrderStatus getOrderStatus() {
        return ConstantTypesEnum.OrderStatus.LOGISTICS;
    }
}
