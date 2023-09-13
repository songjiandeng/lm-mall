package com.macro.mall.task;


import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.macro.mall.mapper.OmsOrderItemMapper;
import com.macro.mall.mapper.OmsOrderMapper;
import com.macro.mall.mapper.OmsOrderStageMapper;
import com.macro.mall.mapper.PmsProductMapper;
import com.macro.mall.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TimeTask {

    @Autowired
    private OmsOrderMapper omsOrderMapper;

    @Autowired
    private OmsOrderStageMapper stageMapper;

    @Autowired
    private OmsOrderItemMapper orderItemMapper;
    
    @Autowired
    private PmsProductMapper pmsProductMapper;

//    @Scheduled(cron= "0 0/10 * ? * ?")
    @Scheduled(cron= "0 10 0 * * ?")
    private void overOrderPatrol(){
        //获取所有未完成支付账单进行巡检
        //获取状态不为关闭 审核中
        log.info("【定时核算逾期订单】【开始】");
        OmsOrderExample orderExample = new OmsOrderExample();
        List<Integer> status = new ArrayList<>();
        status.add(0);
        status.add(7);
        status.add(8);
        status.add(6);
        orderExample.createCriteria().andStatusNotIn(status);
        List<OmsOrder> omsOrders = omsOrderMapper.selectByExample(orderExample);
        //遍历订单获取id
        List<Long> collect = omsOrders.stream().map(m -> m.getId()).distinct().collect(Collectors.toList());
        log.info("【定时核算逾期订单】【订单列表】:{}", JSONUtil.toJsonStr(collect));
        OmsOrderStageExample stageExample = new OmsOrderStageExample();
        stageExample.createCriteria().andOrderIdIn(collect).andBillTypeEqualTo(0).andStatusNotEqualTo(2);
        List<OmsOrderStage> stageList = stageMapper.selectByExample(stageExample);
        //根据订单id 分组
        Map<Long, List<OmsOrderStage>> collect1 = stageList.stream().collect(Collectors.groupingBy(OmsOrderStage::getOrderId));
        Date now = new Date();
        for(Map.Entry<Long, List<OmsOrderStage>> entry : collect1.entrySet()){
            List<OmsOrderStage> value = entry.getValue();
            Long key = entry.getKey();
            log.info("【账单核实】【订单id】:{} ，【账单列表】: {}", key,JSONUtil.toJsonStr(value));
            //验证是否逾期，逾期账单标记比计算逾期金额，同时已经逾期账单进行逾期金额更新
            OmsOrderItemExample itemExample = new OmsOrderItemExample();
            itemExample.createCriteria().andOrderIdEqualTo(key);
            List<OmsOrderItem> omsOrderItems = orderItemMapper.selectByExample(itemExample);
            Long productId = omsOrderItems.get(0).getProductId();
            PmsProduct pmsProduct = pmsProductMapper.selectByPrimaryKey(productId);
            Integer latePenalty = pmsProduct.getLatePenalty();
            boolean flag = false;
            for(OmsOrderStage stage : value){
                Date repaymentTime = stage.getRepaymentTime();
                if(now.after(repaymentTime)){
                    flag = true;
                    //逾期
                    stage.setIsOver(1);
                    BigDecimal periodPrice = stage.getPeriodPrice();
                    BigDecimal payPrice = stage.getPayPrice();
                    BigDecimal overPrice = stage.getOverPrice()==null?new BigDecimal(0):stage.getOverPrice();
                    //计算逾期金额
                    BigDecimal subtract = periodPrice.add(overPrice).subtract(payPrice).multiply(new BigDecimal(latePenalty).divide(new BigDecimal(100),2,BigDecimal.ROUND_HALF_UP));
                    stage.setStatus(3);
                    overPrice = overPrice.add(subtract);
                    stage.setOverPrice(overPrice);
                    stage.setUpdateTime(new Date());
                    stageMapper.updateByPrimaryKey(stage);
                }
            }
            if(flag){
                List<OmsOrder> orderUpdate = omsOrders.stream().filter(f -> f.getId() == key).collect(Collectors.toList());
                orderUpdate.forEach(f->{
                    f.setStatus(5);
                    f.setModifyTime(new Date());
                    omsOrderMapper.updateByPrimaryKey(f);
                });
            }
        }

    }
}
