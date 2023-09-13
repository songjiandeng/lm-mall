package com.macro.mall.strategy.order;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.macro.mall.common.enums.ConstantTypesEnum;
import com.macro.mall.dto.OmsOrderQueryParam;
import com.macro.mall.mapper.*;
import com.macro.mall.model.*;
import com.macro.mall.service.OrderOperateStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Slf4j
public class OrderBillReviewStrategy implements OrderOperateStrategy<Integer> {

    @Autowired
    private OmsOrderMapper orderMapper;

    @Autowired
    private OmsOrderOperateHistoryMapper orderOperateHistoryMapper;

    @Autowired
    private UmsAdminMapper umsAdminMapper;

    @Autowired
    private OmsOrderItemMapper itemMapper;

    @Autowired
    private OmsOrderStageMapper stageMapper;

    @Autowired
    private PmsProductRentMapper pmsProductRentMapper;

    @Override
    @Transactional
    public Integer operateOrder(OmsOrderQueryParam omsOrderQueryParam) {
        log.info("【订单操作】【订单审核】【入参】：{}", JSONUtil.toJsonStr(omsOrderQueryParam));
        Integer status = null;
        try {
            //1. 订单审核修改订单商品状态（下一状态） 2. 添加审核记录  3. 生成账单信息  4. 返回下一状态
            Integer state = omsOrderQueryParam.getState();
            //获取订单详情信息
            Long id = omsOrderQueryParam.getId();
            OmsOrder order = orderMapper.selectByPrimaryKey(id);
            if (!Objects.isNull(order)) {
                if (state == ConstantTypesEnum.OrderStatus.REFUSE.getValue()) {
                    status = ConstantTypesEnum.OrderStatus.CLOSE.getValue();
                    order.setStatus(status);
                } else {
                    status = ConstantTypesEnum.OrderStatus.PAYOFF.getValue();
                    order.setStatus(status);
                    // TODO: 2023/8/21  支付首期完成之后一天生成后续账单账单，可能线下支付 可能支付回调生成
                    //首期支付=审核通过后即可支付
                    //开始时间=第一期支付时间+1
                    //租用天数=分期数*订单周期
                    //订单结束时间=开始时间+租用天数
                    //获取订单周期、期数
                    OmsOrderItemExample example = new OmsOrderItemExample();
                    example.createCriteria().andOrderIdEqualTo(id);
                    List<OmsOrderItem> omsOrderItems = itemMapper.selectByExample(example);
                    for (OmsOrderItem item : omsOrderItems) {
                        Integer stageNum = item.getStageNum();
                        //账单周期单位 0->月 1->周 2->日
                        Integer billingCycleUnit = item.getBillingCycleUnit();
                        String desc = ConstantTypesEnum.CycleUnit.getByType(billingCycleUnit).getDesc();
                        Integer billingCycle = item.getBillingCycle();
                        Integer leaseNum = stageNum * Integer.parseInt(desc) * billingCycle;
                        item.setLeaseNum(leaseNum);
                        //计算起租时间 结束时间
                        Date now = new Date();
                        Date startTime = DateUtil.offsetDay(now, 1);
                        item.setStartTime(startTime);
                        Date endTime = DateUtil.offsetDay(startTime, leaseNum - 1);
                        item.setEndTime(endTime);
                        itemMapper.updateByPrimaryKey(item);
                        //生成对应商品订单 账单列表
                        BigDecimal productPrice = item.getProductPrice();
                        List<OmsOrderStage> stageList = new ArrayList<>();
                        String addServer = item.getAddServer();
                        BigDecimal buyoutPrice = item.getBuyoutPrice();
                        for (int i = 1; i <= stageNum; i++) {
                            OmsOrderStage stage = new OmsOrderStage();
                            stage.setMemberId(order.getMemberId());
                            stage.setOrderId(order.getId());
                            stage.setOrderSn(order.getOrderSn());
                            stage.setOrderItemId(item.getId());
                            stage.setBillContent("租金");
                            stage.setBillType(0);
                            stage.setStageNum(stageNum);
                            stage.setStatus(1);
                            stage.setPayNo(i);
                            BigDecimal periodPrice = productPrice.divide(new BigDecimal(stageNum), 2, BigDecimal.ROUND_HALF_DOWN);
                            //验证是否是灵活分期
                            Integer repaymentType = item.getRepaymentType();
                            if (1 == repaymentType) {
                                //获取对应账单值
                                PmsProductRentExample rent = new PmsProductRentExample();
                                rent.createCriteria().andProductIdEqualTo(item.getProductId()).andStagesEqualTo(i);
                                List<PmsProductRent> pmsProductRentList = pmsProductRentMapper.selectByExample(rent);
                                periodPrice = pmsProductRentList.get(0).getStagesPrice();
                            }
                            //验证是否存在增值，买断金额
                            //生成额外账单
                            if(!StrUtil.isEmpty(addServer) && i==1){
                                OmsOrderStage orderStage = new OmsOrderStage();
                                orderStage.setMemberId(order.getMemberId());
                                orderStage.setOrderId(order.getId());
                                orderStage.setOrderSn(order.getOrderSn());
                                orderStage.setOrderItemId(item.getId());
                                orderStage.setBillContent(addServer);
                                orderStage.setBillType(2);
                                orderStage.setStageNum(stageNum);
                                orderStage.setStatus(1);
                                orderStage.setPayNo(1);
                                orderStage.setBillType(2);
                                orderStage.setOverPrice(new BigDecimal(0));
                                orderStage.setPeriodPrice(item.getAddServerPrice());
                                orderStage.setIsOver(0);
                                orderStage.setPayPrice(new BigDecimal(0));
                                Date repaymentTime = startTime;
                                orderStage.setRepaymentTime(repaymentTime);
                                orderStage.setCreateTime(new Date());
                                stageMapper.insertSelective(orderStage);
                                stageList.add(orderStage);
                                periodPrice = periodPrice.add(item.getAddServerPrice());
                            }
                            if (!Objects.isNull(buyoutPrice)){
                                Integer buyoutType = item.getBuyoutType();
                                if(i==(buyoutType == 0 ? 1 : stageNum)){
                                    periodPrice = periodPrice.add(item.getBuyoutPrice());
                                    OmsOrderStage buyoutStage = new OmsOrderStage();
                                    buyoutStage.setMemberId(order.getMemberId());
                                    buyoutStage.setOrderId(order.getId());
                                    buyoutStage.setOrderSn(order.getOrderSn());
                                    buyoutStage.setOrderItemId(item.getId());
                                    buyoutStage.setBillContent("买断");
                                    buyoutStage.setBillType(1);
                                    buyoutStage.setBuyoutType(buyoutType);
                                    buyoutStage.setStageNum(stageNum);
                                    buyoutStage.setStatus(1);
                                    buyoutStage.setBillType(1);
                                    buyoutStage.setPayNo(buyoutType == 0 ? 1 : stageNum);
                                    buyoutStage.setPeriodPrice(item.getBuyoutPrice());
                                    buyoutStage.setIsOver(0);
                                    buyoutStage.setOverPrice(new BigDecimal(0));
                                    buyoutStage.setPayPrice(new BigDecimal(0));
                                    Date repaymentTime = DateUtil.offsetDay(startTime, stage.getPayNo() - 1);
                                    buyoutStage.setRepaymentTime(repaymentTime);
                                    buyoutStage.setCreateTime(new Date());
                                    stageMapper.insertSelective(buyoutStage);
                                    stageList.add(buyoutStage);
                                }
                            }
                            stage.setPeriodPrice(periodPrice);
                            stage.setIsOver(0);
                            stage.setBillType(0);
                            stage.setOverPrice(new BigDecimal(0));
                            stage.setPayPrice(new BigDecimal(0));
                            Date repaymentTime = DateUtil.offsetDay(startTime, i - 1);
                            stage.setRepaymentTime(repaymentTime);
                            stage.setCreateTime(new Date());
                            stageMapper.insertSelective(stage);
                            stageList.add(stage);
                        }
                        String collect = stageList.stream().filter(f->f.getBillType()==0).map(m -> m.getId().toString()).distinct().collect(Collectors.joining(", "));
                        item.setStageIds(collect);
                        itemMapper.updateByPrimaryKey(item);
                    }
                }
                order.setModifyTime(new Date());
                orderMapper.updateByPrimaryKeySelective(order);
                //生成操作记录
                OmsOrderOperateHistory history = new OmsOrderOperateHistory();
                history.setOrderId(omsOrderQueryParam.getId());
                history.setCreateTime(new Date());
                history.setOperateId(omsOrderQueryParam.getOperateId());
                UmsAdmin umsAdmin = umsAdminMapper.selectByPrimaryKey(history.getOperateId());
                history.setOperateMan(umsAdmin.getUsername());
                history.setOrderStatus(state);
                history.setNote(omsOrderQueryParam.getProcessNote());
                orderOperateHistoryMapper.insert(history);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("【订单操作】【订单审核异常】【入参】:{}",JSONUtil.toJsonStr(omsOrderQueryParam));
        }
        return status;
    }

    @Override
    public ConstantTypesEnum.OrderStatus getOrderStatus() {
        return ConstantTypesEnum.OrderStatus.PROCESS;
    }
}
