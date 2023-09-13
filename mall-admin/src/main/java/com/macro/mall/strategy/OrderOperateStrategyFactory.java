package com.macro.mall.strategy;

import com.macro.mall.common.enums.ConstantTypesEnum;
import com.macro.mall.service.OrderOperateStrategy;

import java.util.HashMap;
import java.util.Map;

public class OrderOperateStrategyFactory {

    private static Map<ConstantTypesEnum.OrderStatus, OrderOperateStrategy> strategyMap = new HashMap<>();

    public static OrderOperateStrategy getStrategy(ConstantTypesEnum.OrderStatus orderStatus) {
        return strategyMap.get(orderStatus);
    }

    public static void register(ConstantTypesEnum.OrderStatus orderStatus, OrderOperateStrategy strategy) {
        if(orderStatus != null && strategy != null) {
            strategyMap.put(orderStatus, strategy);
        }
    }
}
