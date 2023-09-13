package com.macro.mall.strategy.order;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.macro.mall.common.enums.ConstantTypesEnum;
import com.macro.mall.dao.OmsOrderDao;
import com.macro.mall.dto.OmsOrderDetail;
import com.macro.mall.dto.OmsOrderQueryParam;
import com.macro.mall.dto.OmsOrderStageDetail;
import com.macro.mall.mapper.OmsOrderOperateHistoryMapper;
import com.macro.mall.mapper.OmsOrderStageMapper;
import com.macro.mall.mapper.UmsMemberMapper;
import com.macro.mall.model.*;
import com.macro.mall.service.OmsOrderService;
import com.macro.mall.service.OrderOperateStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author: fisher
 * @Date: 2023/8/20 9:21
 */
@Component
@Slf4j
public class OrderDetailStrategy implements OrderOperateStrategy<OmsOrderDetail> {

    @Autowired
    private OmsOrderService omsOrderService;

    @Autowired
    private UmsMemberMapper umsMemberMapper;

    @Autowired
    private OmsOrderDao orderDao;

    @Autowired
    private OmsOrderStageMapper stageMapper;

    @Autowired
    private OmsOrderOperateHistoryMapper historyMapper;


    @Override
    public OmsOrderDetail operateOrder(OmsOrderQueryParam omsOrderQueryParam) {
        log.info("【订单操作】【订单详情获取】【入参】：{}", JSONUtil.toJsonStr(omsOrderQueryParam));
        OmsOrderDetail orderDetailResult = null;
        try {
            //获取订单详情信息
            Long id = omsOrderQueryParam.getId();
            orderDetailResult = omsOrderService.detail(id);
            if(!Objects.isNull(orderDetailResult)){
                //根据memberId 获取用户详情信息
                Long memberId = orderDetailResult.getMemberId();
                UmsMember umsMember = umsMemberMapper.selectByPrimaryKey(memberId);
                orderDetailResult.setUmsMember(umsMember);
                //获取总在租订单，完成订单
                List<OmsOrder> list = orderDao.getList(omsOrderQueryParam);
                long usedNum = list.stream().filter(f -> f.getStatus() == ConstantTypesEnum.OrderStatus.USED.getValue()).count();
                long completeNum = list.stream().filter(f -> f.getStatus() == ConstantTypesEnum.OrderStatus.COMPLETE.getValue()).count();
                orderDetailResult.setUsedNum(Integer.parseInt(usedNum+""));
                orderDetailResult.setCompleteNum(Integer.parseInt(completeNum+""));
                //OmsOrderStageDetail
                OmsOrderStageDetail omsOrderStageDetail = new OmsOrderStageDetail();
                OmsOrderStageExample stageExample = new OmsOrderStageExample();
                stageExample.createCriteria().andOrderIdEqualTo(id);
                List<OmsOrderStage> omsOrderStages = stageMapper.selectByExample(stageExample);
                if(!omsOrderStages.isEmpty()){
                    double sum = omsOrderStages.stream().mapToDouble(m -> m.getPeriodPrice().doubleValue()).sum();
                    omsOrderStageDetail.setGrossRent(new BigDecimal(sum));
                    omsOrderStageDetail.setStageNum(omsOrderStages.get(0).getStageNum());
                    List<OmsOrderStage> collect = omsOrderStages.stream().filter(f -> f.getBillType() == 1).collect(Collectors.toList());
                    if(!collect.isEmpty()){
                        omsOrderStageDetail.setBuyoutType(collect.get(0).getBuyoutType());
                        omsOrderStageDetail.setBuyoutPrice(collect.get(0).getPeriodPrice());
                    }
                    List<OmsOrderStage> collect2 = omsOrderStages.stream().filter(f -> f.getBillType() == 2).collect(Collectors.toList());
                    if(!collect2.isEmpty()){
                        String addServer = collect2.get(0).getBillContent() + "￥" + collect2.get(0).getPeriodPrice();
                        omsOrderStageDetail.setAddServer(addServer);
                    }
                }else{
                    BeanUtil.copyProperties(orderDetailResult.getOrderItemList().get(0),omsOrderStageDetail,false);
                }
                omsOrderStageDetail.setOmsOrderStageList(omsOrderStages);
                orderDetailResult.setOmsOrderStageDetail(omsOrderStageDetail);
                //获取操作记录
                OmsOrderOperateHistoryExample example = new OmsOrderOperateHistoryExample();
                example.createCriteria().andOrderIdEqualTo(id);
                example.setOrderByClause("create_time desc");
                List<OmsOrderOperateHistory> historyList = historyMapper.selectByExample(example);
                orderDetailResult.setHistoryList(historyList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orderDetailResult;
    }

    @Override
    public ConstantTypesEnum.OrderStatus getOrderStatus() {
        return ConstantTypesEnum.OrderStatus.DETAIL;
    }
}
