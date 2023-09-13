package com.macro.mall.strategy.order;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.macro.mall.common.enums.ConstantTypesEnum;
import com.macro.mall.dao.OmsOrderDao;
import com.macro.mall.dto.ItemVo;
import com.macro.mall.dto.OmsOrderDetail;
import com.macro.mall.dto.OmsOrderQueryParam;
import com.macro.mall.dto.OrderHistoryDetail;
import com.macro.mall.mapper.OmsOrderItemMapper;
import com.macro.mall.mapper.OmsOrderStageMapper;
import com.macro.mall.model.*;
import com.macro.mall.service.OrderOperateStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: fisher
 * @Date: 2023/8/20 10:29
 */
@Component
@Slf4j
public class OrderHistoryStrategy implements OrderOperateStrategy<OmsOrderDetail> {

    @Autowired
    private OmsOrderDao omsOrderDao;

    @Autowired
    private OmsOrderStageMapper omsOrderStageMapper;

    @Autowired
    private OmsOrderItemMapper omsOrderItemMapper;

    @Override
    public OmsOrderDetail operateOrder(OmsOrderQueryParam omsOrderQueryParam) {
        log.info("【订单操作】【历史订单详情获取】【入参】：{}", JSONUtil.toJsonStr(omsOrderQueryParam));
        OmsOrderDetail orderDetailResult = new OmsOrderDetail();
        try{
            Long memberId = omsOrderQueryParam.getMemberId();
            //根据用户id 获取所有订单数据
            List<Long> ids = new ArrayList<>();
            ids.add(memberId);
            omsOrderQueryParam.setMemberIds(ids);
            omsOrderQueryParam.setStatus(null);
            List<OmsOrder> list = omsOrderDao.getList(omsOrderQueryParam);
            //过滤在租 完结订单
            List<OmsOrder> usedOrders = list.stream().filter(f -> f.getStatus() == ConstantTypesEnum.OrderStatus.DELAY.getValue() || f.getStatus() == ConstantTypesEnum.OrderStatus.USED.getValue()).collect(Collectors.toList());
            List<OmsOrder> complateOrders = list.stream().filter(f -> f.getStatus() == ConstantTypesEnum.OrderStatus.COMPLETE.getValue()).collect(Collectors.toList());
            if(!usedOrders.isEmpty()){
                OrderHistoryDetail convert = convert(usedOrders, 1, memberId);
                orderDetailResult.setOrderUsedDetail(convert);
            }
            if(!complateOrders.isEmpty()){
                OrderHistoryDetail convert = convert(complateOrders, 2, memberId);
                orderDetailResult.setOrderUsedDetail(convert);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return orderDetailResult;
    }

    private OrderHistoryDetail convert(List<OmsOrder> orders,Integer status, Long memberId){
        OrderHistoryDetail orderUsedDetail = new OrderHistoryDetail();
        orderUsedDetail.setOrderNum(orders.size());
        double sum = orders.stream().mapToDouble(m -> m.getPayAmount().doubleValue()).sum();
        orderUsedDetail.setRentAmount(new BigDecimal(sum));
        //获取总的支付订单列表
        OmsOrderStageExample orderStageExample = new OmsOrderStageExample();
        orderStageExample.createCriteria().andMemberIdEqualTo(memberId);
        List<OmsOrderStage> omsOrderStages = omsOrderStageMapper.selectByExample(orderStageExample);
        double sum1 = omsOrderStages.stream().filter(f -> f.getStatus() == 2).mapToDouble(m -> m.getPeriodPrice().doubleValue()).sum();
        orderUsedDetail.setReturnAmount(new BigDecimal(sum1));
        double sum2 = omsOrderStages.stream().filter(f -> f.getStatus() == 1 || f.getStatus() == 3).mapToDouble(m -> m.getPeriodPrice().doubleValue()).sum();
        orderUsedDetail.setToAmount(new BigDecimal(sum2));
        if(status==2){
            long count = omsOrderStages.stream().filter(f -> f.getIsOver() == 1).count();
            orderUsedDetail.setOrderNum(Integer.parseInt(count+""));
        }
        List<Long> collect = orders.stream().map(m -> m.getId()).distinct().collect(Collectors.toList());
        //获取对应商品列表
        OmsOrderItemExample itemExample = new OmsOrderItemExample();
        itemExample.createCriteria().andOrderIdIn(collect);
        List<OmsOrderItem> omsOrderItems = omsOrderItemMapper.selectByExample(itemExample);
        List<ItemVo> itemVoList = new ArrayList<>();
        for(OmsOrderItem item : omsOrderItems){
            ItemVo vo = JSONUtil.toBean(JSONUtil.toJsonStr(item),ItemVo.class);
            Long orderId = item.getOrderId();
            List<OmsOrder> collect1 = orders.stream().filter(f -> f.getId() == orderId).collect(Collectors.toList());
            vo.setOrderTime(DateUtil.format(collect1.get(0).getCreateTime(),"yyyy-MM-dd HH:mm:ss"));
            vo.setStatus(collect1.get(0).getStatus());
            //订单对应商品分期列表
            List<OmsOrderStage> collect2 = omsOrderStages.stream().filter(f -> f.getOrderId() == orderId).collect(Collectors.toList());
            long returnNum = collect2.stream().filter(f -> f.getStatus() == 2).count();
            vo.setReturnNum(Integer.parseInt(returnNum + ""));
            long toNum = collect2.stream().filter(f -> f.getStatus() == 1).count();
            vo.setToNum(Integer.parseInt(toNum + ""));
            long overNum = collect2.stream().filter(f -> f.getIsOver() == 1).count();
            vo.setOverNum(Integer.parseInt(overNum + ""));
            itemVoList.add(vo);
        }
        return orderUsedDetail;
    }

    @Override
    public ConstantTypesEnum.OrderStatus getOrderStatus() {
        return ConstantTypesEnum.OrderStatus.HISTORY;
    }
}
