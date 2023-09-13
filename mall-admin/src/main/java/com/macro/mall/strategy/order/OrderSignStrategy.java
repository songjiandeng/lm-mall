package com.macro.mall.strategy.order;


import cn.hutool.json.JSONUtil;
import com.macro.mall.common.enums.ConstantTypesEnum;
import com.macro.mall.dao.OmsOrderOperateHistoryDao;
import com.macro.mall.dto.OmsOrderQueryParam;
import com.macro.mall.mapper.OmsOrderMapper;
import com.macro.mall.mapper.OmsOrderOperateHistoryMapper;
import com.macro.mall.mapper.UmsAdminMapper;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.model.OmsOrderOperateHistory;
import com.macro.mall.model.UmsAdmin;
import com.macro.mall.service.OrderOperateStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class OrderSignStrategy implements OrderOperateStrategy<Integer> {

    @Autowired
    private OmsOrderMapper orderMapper;

    @Autowired
    private UmsAdminMapper umsAdminMapper;

    @Autowired
    private OmsOrderOperateHistoryMapper orderOperateHistoryMapper;

    @Override
    public Integer operateOrder(OmsOrderQueryParam omsOrderQueryParam) {
        log.info("【订单操作】【订单签收】【入参】：{}", JSONUtil.toJsonStr(omsOrderQueryParam));
        Integer status = null;
        try{
            Long id = omsOrderQueryParam.getId();
            OmsOrder order = orderMapper.selectByPrimaryKey(id);
            //修改订单状态，签收进入下一阶段，租用中
            order.setStatus(ConstantTypesEnum.OrderStatus.USED.getValue());
            order.setModifyTime(new Date());
            status = orderMapper.updateByPrimaryKey(order);
            if(status>0){
                OmsOrderOperateHistory history = new OmsOrderOperateHistory();
                history.setOrderId(id);
                history.setCreateTime(new Date());
                Long operateId = omsOrderQueryParam.getAdminId()==null?omsOrderQueryParam.getChannelId():omsOrderQueryParam.getAdminId();
                UmsAdmin umsAdmin = umsAdminMapper.selectByPrimaryKey(operateId);
                history.setOperateId(operateId);
                history.setOperateMan(umsAdmin.getUsername());
                history.setOrderStatus(status);
                history.setNote("订单签收");
                orderOperateHistoryMapper.insert(history);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return status;
    }

    @Override
    public ConstantTypesEnum.OrderStatus getOrderStatus() {
        return ConstantTypesEnum.OrderStatus.SIGN;
    }
}
