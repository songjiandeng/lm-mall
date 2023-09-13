package com.macro.mall.service;

import com.macro.mall.common.enums.ConstantTypesEnum;
import com.macro.mall.dto.OmsOrderQueryParam;
import com.macro.mall.strategy.OrderOperateStrategyFactory;
import org.springframework.beans.factory.InitializingBean;

public interface OrderOperateStrategy<D> extends InitializingBean {


    D operateOrder(OmsOrderQueryParam omsOrderQueryParam);


    /**
     * 获取当前策略所属枚举
     *
     * @return
     */
    ConstantTypesEnum.OrderStatus getOrderStatus();

    @Override
    default void afterPropertiesSet() throws Exception {
        OrderOperateStrategyFactory.register(getOrderStatus(), this);
    }
}
