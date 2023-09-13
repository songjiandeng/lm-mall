package com.macro.mall.strategy.order;

import cn.hutool.json.JSONUtil;
import com.macro.mall.common.enums.ConstantTypesEnum;
import com.macro.mall.dto.OmsOrderQueryParam;
import com.macro.mall.mapper.OmsOrderMapper;
import com.macro.mall.mapper.OmsOrderOperateHistoryMapper;
import com.macro.mall.mapper.OmsOrderStageMapper;
import com.macro.mall.mapper.UmsAdminMapper;
import com.macro.mall.model.*;
import com.macro.mall.service.OrderOperateStrategy;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class OrderOfflineReturnStrategy implements OrderOperateStrategy<Integer> {

    @Autowired
    private OmsOrderStageMapper omsOrderStageMapper;

    @Autowired
    private OmsOrderMapper omsOrderMapper;

    @Autowired
    private OmsOrderOperateHistoryMapper orderOperateHistoryMapper;

    @Autowired
    private UmsAdminMapper umsAdminMapper;

    @Override
    public Integer operateOrder(OmsOrderQueryParam omsOrderQueryParam) {
        log.info("【订单操作】【线下归还】【入参】：{}", JSONUtil.toJsonStr(omsOrderQueryParam));
        Integer orderStatus = omsOrderQueryParam.getStatus();
        try {
            BigDecimal offlinePrice = omsOrderQueryParam.getOfflinePrice();
            Long id = omsOrderQueryParam.getId();
            OmsOrderStageExample example = new OmsOrderStageExample();
            example.createCriteria().andOrderIdEqualTo(id).andStatusNotEqualTo(2).andBillTypeEqualTo(0);
            List<OmsOrderStage> stageList = omsOrderStageMapper.selectByExample(example);
            //线下归还调整状态
            OmsOrder omsOrder = omsOrderMapper.selectByPrimaryKey(id);
            Integer status = omsOrder.getStatus();
            stageList = stageList.stream().sorted(Comparator.comparing(OmsOrderStage::getPayNo)).collect(Collectors.toList());
            if (status == ConstantTypesEnum.OrderStatus.PAYOFF.getValue() && stageList.size() == 1) {//首期
                OmsOrderStage stage = stageList.get(0);
                stage.setUpdateTime(new Date());
                stage.setPayTime(new Date());
                stage.setPayType(2);
                BigDecimal payPrice = stage.getPayPrice();
                if (payPrice == null) {
                    payPrice = new BigDecimal(0);
                }
                BigDecimal add = stage.getPeriodPrice().subtract(payPrice);
                //验证金额是否支付完成
                if (offlinePrice.compareTo(add) == 0 || offlinePrice.compareTo(add) == 1) {
                    omsOrder.setStatus(ConstantTypesEnum.OrderStatus.CONSIGNMENT.getValue());
                    stage.setStatus(2);
                    stage.setPayPrice(stage.getPeriodPrice());
                    BigDecimal subtract = offlinePrice.subtract(add);
                    // TODO: 2023/8/26 推送延迟队列生成后续账单（延迟队列实体需要思考下 subtract）
                } else if (offlinePrice.compareTo(add) == -1) {
                    //依然是待归还状态
                    stage.setStatus(1);
                    stage.setPayPrice(payPrice.add(offlinePrice));
                }
                omsOrderStageMapper.updateByPrimaryKey(stage);
            } else if (status == ConstantTypesEnum.OrderStatus.DELAY.getValue() || status == ConstantTypesEnum.OrderStatus.USED.getValue()) {
                //遍历账单
                for (OmsOrderStage stage : stageList) {
                    stage.setUpdateTime(new Date());
                    stage.setPayTime(new Date());
                    stage.setPayType(2);
                    BigDecimal payPrice = stage.getPayPrice();
                    if (payPrice == null) {
                        payPrice = new BigDecimal(0);
                    }
                    BigDecimal add = stage.getPeriodPrice().add(stage.getOverPrice()).subtract(payPrice);
                    if (offlinePrice.compareTo(add) == 0) {
                        stage.setStatus(2);
                        omsOrderStageMapper.updateByPrimaryKey(stage);
                        break;
                    } else if (offlinePrice.compareTo(add) == -1) {
                        stage.setStatus(1);
                        if (new Date().after(stage.getRepaymentTime())) {
                            stage.setIsOver(1);
                            stage.setStatus(3);
                        }
                        omsOrderStageMapper.updateByPrimaryKey(stage);
                        break;
                    } else if (offlinePrice.compareTo(add) == 1) {
                        offlinePrice = offlinePrice.subtract(add);
                        stage.setStatus(2);
                        stage.setPayPrice(stage.getPeriodPrice());
                        omsOrderStageMapper.updateByPrimaryKey(stage);
                    }
                }
                long count = stageList.stream().filter(f -> f.getStatus() == 3).count();
                if (count == 0) {
                    omsOrder.setStatus(ConstantTypesEnum.OrderStatus.USED.getValue());
                    //验证是否还存在待支付，无则订单完成
                    long payCount = stageList.stream().filter(f -> f.getStatus() == 1).count();
                    if (payCount == 0) {
                        omsOrder.setStatus(ConstantTypesEnum.OrderStatus.COMPLETE.getValue());
                    }
                }
            }
            omsOrder.setModifyTime(new Date());
            omsOrderMapper.updateByPrimaryKey(omsOrder);
            orderStatus = omsOrder.getStatus();
            //添加操作记录
            OmsOrderOperateHistory history = new OmsOrderOperateHistory();
            history.setOrderId(omsOrderQueryParam.getId());
            history.setCreateTime(new Date());
            history.setOperateId(omsOrderQueryParam.getOperateId());
            UmsAdmin umsAdmin = umsAdminMapper.selectByPrimaryKey(history.getId());
            history.setOperateMan(umsAdmin.getUsername());
            history.setOrderStatus(8);
            history.setNote(ConstantTypesEnum.OrderStatus.getByType(8).getDesc() + ":" + offlinePrice);
            orderOperateHistoryMapper.insert(history);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orderStatus;
    }

    @Override
    public ConstantTypesEnum.OrderStatus getOrderStatus() {
        return ConstantTypesEnum.OrderStatus.RETURN;
    }
}
