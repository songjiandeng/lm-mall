package com.macro.mall.strategy.order;

import cn.hutool.json.JSONUtil;
import com.macro.mall.common.enums.ConstantTypesEnum;
import com.macro.mall.dto.OmsOrderQueryParam;
import com.macro.mall.mapper.OmsOrderOperateHistoryMapper;
import com.macro.mall.mapper.UmsAdminMapper;
import com.macro.mall.model.OmsOrderOperateHistory;
import com.macro.mall.model.UmsAdmin;
import com.macro.mall.service.OrderOperateStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class OrderCollectionStrategy implements OrderOperateStrategy<Integer> {

    @Autowired
    private OmsOrderOperateHistoryMapper historyMapper;

    @Autowired
    private UmsAdminMapper umsAdminMapper;

    @Override
    public Integer operateOrder(OmsOrderQueryParam omsOrderQueryParam) {
        int count = 0;
        log.info("【订单操作】【订单催收记录】【入参】：{}", JSONUtil.toJsonStr(omsOrderQueryParam));
        try{
            Long id = omsOrderQueryParam.getId();
            Long operateId = omsOrderQueryParam.getOperateId();
            String processNote = omsOrderQueryParam.getProcessNote();
            Integer status = omsOrderQueryParam.getStatus();
            OmsOrderOperateHistory history = new OmsOrderOperateHistory();
            history.setOrderId(id);
            history.setCreateTime(new Date());
            UmsAdmin umsAdmin = umsAdminMapper.selectByPrimaryKey(operateId);
            history.setOperateId(operateId);
            history.setOperateMan(umsAdmin.getUsername());
            history.setOrderStatus(status);
            history.setNote(processNote);
            count = historyMapper.insertSelective(history);
        }catch (Exception e){
            e.printStackTrace();
        }
        return count;
    }

    @Override
    public ConstantTypesEnum.OrderStatus getOrderStatus() {
        return ConstantTypesEnum.OrderStatus.COLLECTION;
    }
}
