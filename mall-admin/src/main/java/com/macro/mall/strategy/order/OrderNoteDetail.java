package com.macro.mall.strategy.order;


import cn.hutool.json.JSONUtil;
import com.macro.mall.common.enums.ConstantTypesEnum;
import com.macro.mall.dto.OmsOrderQueryParam;
import com.macro.mall.mapper.OmsOrderOperateHistoryMapper;
import com.macro.mall.model.OmsOrderOperateHistory;
import com.macro.mall.model.OmsOrderOperateHistoryExample;
import com.macro.mall.service.OrderOperateStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class OrderNoteDetail implements OrderOperateStrategy<List<OmsOrderOperateHistory>> {

    @Autowired
    private OmsOrderOperateHistoryMapper orderOperateHistoryMapper;

    @Override
    public List<OmsOrderOperateHistory> operateOrder(OmsOrderQueryParam omsOrderQueryParam) {
        log.info("【订单操作】【获取备注详情】【入参】：{}", JSONUtil.toJsonStr(omsOrderQueryParam));
        List<OmsOrderOperateHistory> omsOrderOperateHistories = null;
        try{
            Long id = omsOrderQueryParam.getId();
            OmsOrderOperateHistoryExample example = new OmsOrderOperateHistoryExample();
            example.createCriteria().andOrderIdEqualTo(id).andOrderStatusEqualTo(ConstantTypesEnum.OrderStatus.NOTE.getValue());
            omsOrderOperateHistories = orderOperateHistoryMapper.selectByExample(example);
        }catch (Exception e){
            e.printStackTrace();
        }
        return omsOrderOperateHistories;
    }

    @Override
    public ConstantTypesEnum.OrderStatus getOrderStatus() {
        return ConstantTypesEnum.OrderStatus.NOTE_DETAIL;
    }
}
