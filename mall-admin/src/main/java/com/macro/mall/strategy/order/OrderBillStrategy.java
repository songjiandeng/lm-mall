package com.macro.mall.strategy.order;

import cn.hutool.json.JSONUtil;
import com.macro.mall.common.enums.ConstantTypesEnum;
import com.macro.mall.dto.OmsOrderQueryParam;
import com.macro.mall.dto.OmsOrderStageDetail;
import com.macro.mall.mapper.OmsOrderStageMapper;
import com.macro.mall.model.OmsOrderStage;
import com.macro.mall.model.OmsOrderStageExample;
import com.macro.mall.service.OrderOperateStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class OrderBillStrategy implements OrderOperateStrategy<OmsOrderStageDetail> {

    @Autowired
    private OmsOrderStageMapper omsOrderStageMapper;

    @Override
    public OmsOrderStageDetail operateOrder(OmsOrderQueryParam omsOrderQueryParam) {
        log.info("【订单操作】【历史账单详情获取】【入参】：{}", JSONUtil.toJsonStr(omsOrderQueryParam));
        OmsOrderStageDetail detail = new OmsOrderStageDetail();
        try {
            String orderSn = omsOrderQueryParam.getOrderSn();
            Long id = omsOrderQueryParam.getId();
            //获取账单列表信息
            OmsOrderStageExample example = new OmsOrderStageExample();
            example.createCriteria().andOrderIdEqualTo(id).andOrderSnEqualTo(orderSn);
            List<OmsOrderStage> omsOrderStages = omsOrderStageMapper.selectByExample(example);
            if(!omsOrderStages.isEmpty()){
                double sum = omsOrderStages.stream().mapToDouble(m -> m.getPeriodPrice().doubleValue()).sum();
                detail.setGrossRent(new BigDecimal(sum));
                detail.setStageNum(omsOrderStages.get(0).getStageNum());
                List<OmsOrderStage> collect = omsOrderStages.stream().filter(f -> f.getBillType() == 1).collect(Collectors.toList());
                if(!collect.isEmpty()){
                    detail.setBuyoutType(collect.get(0).getBuyoutType());
                    detail.setBuyoutPrice(collect.get(0).getPeriodPrice());
                }
                List<OmsOrderStage> collect2 = omsOrderStages.stream().filter(f -> f.getBillType() == 2).collect(Collectors.toList());
               if(!collect2.isEmpty()){
                   String addServer = collect2.get(0).getBillContent() + "￥" + collect2.get(0).getPeriodPrice();
                   detail.setAddServer(addServer);
               }
                detail.setOmsOrderStageList(omsOrderStages);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    @Override
    public ConstantTypesEnum.OrderStatus getOrderStatus() {
        return ConstantTypesEnum.OrderStatus.BILL;
    }
}
