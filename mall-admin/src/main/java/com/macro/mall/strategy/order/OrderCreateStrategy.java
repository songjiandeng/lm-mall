package com.macro.mall.strategy.order;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.macro.mall.common.enums.ConstantTypesEnum;
import com.macro.mall.dto.OmsOrderCreateParam;
import com.macro.mall.dto.OmsOrderQueryParam;
import com.macro.mall.mapper.*;
import com.macro.mall.model.*;
import com.macro.mall.service.OrderOperateStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class OrderCreateStrategy implements OrderOperateStrategy<String> {

    @Autowired
    private PmsProductMapper pmsProductMapper;

    @Autowired
    private UmsMemberMapper umsMemberMapper;

    @Autowired
    private PmsSkuStockMapper skuStockMapper;

    @Autowired
    private PmsProductRentMapper pmsProductRentMapper;

    @Autowired
    private OmsOrderMapper omsOrderMapper;

    @Autowired
    private OmsOrderItemMapper itemMapper;

    @Override
    public String operateOrder(OmsOrderQueryParam omsOrderQueryParam) {
        String orderSn = "";
        log.info("【订单操作】【订单创建】【入参】：{}", JSONUtil.toJsonStr(omsOrderQueryParam));
        try {
//            OmsOrderCreateParam omsOrderCreateParam = omsOrderQueryParam.getOmsOrderCreateParam();
            //获取商品id
            Long productId = omsOrderQueryParam.getProductId();
            PmsProduct pmsProduct = pmsProductMapper.selectByPrimaryKey(productId);
            //获取用户信息
            Long memberId = omsOrderQueryParam.getMemberId();
            UmsMember member = umsMemberMapper.selectByPrimaryKey(memberId);
            //获取规格
            List<Long> skuIds = omsOrderQueryParam.getSkuIds();
            for(Long skuId : skuIds){
                //获取规格数据，以及分期数据
                PmsSkuStock pmsSkuStock = skuStockMapper.selectByPrimaryKey(skuId);
                PmsProductRentExample rent = new PmsProductRentExample();
                rent.createCriteria().andProductIdEqualTo(productId).andSkuIdEqualTo(skuId);
                List<PmsProductRent> pmsProductRentList = pmsProductRentMapper.selectByExample(rent);
                //生产订单，订单商品，虚拟分期账单
                //订单
                OmsOrder order = new OmsOrder();
                order.setMemberId(memberId);
                orderSn = getRanom();
                order.setOrderSn(orderSn);
                order.setCreateTime(new Date());
                order.setMemberUsername(member.getUsername());
                order.setUserPhone(member.getPhone());
                order.setIdCard(StrUtil.isEmpty(member.getIdCard())?"":member.getIdCard());
                BigDecimal price = pmsSkuStock.getPrice();
                order.setTotalAmount(price);
                order.setPayAmount(price);
                order.setPayType(0);
                order.setSourceType(omsOrderQueryParam.getSourceType());
                order.setStatus(0);
                order.setOrderType(omsOrderQueryParam.getOrderType());
                order.setConfirmStatus(0);
                order.setDeleteStatus(0);
                order.setProductName(pmsProduct.getName());
                order.setAdminName(StrUtil.isEmpty(member.getAdminName())?"":member.getAdminName());
                order.setChannelName(StrUtil.isEmpty(member.getChannelName())?"":member.getChannelName());
                omsOrderMapper.insertSelective(order);
                //订单商品
                OmsOrderItem omsOrderItem = new OmsOrderItem();
                omsOrderItem.setOrderId(order.getId());
                omsOrderItem.setOrderSn(orderSn);
                omsOrderItem.setProductId(productId);
                omsOrderItem.setProductPic(pmsProduct.getPic());
                omsOrderItem.setProductName(pmsProduct.getName());
                omsOrderItem.setProductSn(pmsProduct.getProductSn());
                omsOrderItem.setProductPrice(pmsSkuStock.getPrice());
                omsOrderItem.setProductSkuId(skuId);
                omsOrderItem.setProductSkuCode(pmsSkuStock.getSkuCode());
                omsOrderItem.setProductCategoryId(pmsProduct.getProductCategoryId());
                omsOrderItem.setProductAttr(pmsSkuStock.getSpData());
                //预设开始结束时间
                Date startTime = DateUtil.offsetDay(new Date(),1);
                Integer billingCycle = pmsProduct.getBillingCycle();
                Integer billingCycleUnit = pmsProduct.getBillingCycleUnit();
                Integer repaymentNumber = pmsProduct.getRepaymentNumber();
                String desc = ConstantTypesEnum.CycleUnit.getByType(billingCycleUnit).getDesc();
                Integer leaseNum = repaymentNumber * Integer.parseInt(desc) * billingCycle;
                Date endTime = DateUtil.offsetDay(new Date(),leaseNum);
                omsOrderItem.setStartTime(startTime);
                omsOrderItem.setEndTime(endTime);
                omsOrderItem.setLeaseNum(leaseNum);
                omsOrderItem.setStageNum(repaymentNumber);
                omsOrderItem.setBillingCycle(billingCycle);
                omsOrderItem.setBillingCycleUnit(billingCycleUnit);
                omsOrderItem.setRepaymentType(pmsProduct.getRepaymentType());
                omsOrderItem.setBuyoutType(pmsProduct.getBuyoutType());
                omsOrderItem.setBuyoutPrice(pmsProduct.getBuyoutPrice());
                omsOrderItem.setAddServer(pmsProduct.getAddServer());
                omsOrderItem.setAddServerPrice(pmsProduct.getAddServerPrice());
                itemMapper.insertSelective(omsOrderItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orderSn;
    }

    @Override
    public ConstantTypesEnum.OrderStatus getOrderStatus() {
        return ConstantTypesEnum.OrderStatus.CREATE;
    }

    public  String getRanom() {
        Snowflake snowflake = IdUtil.getSnowflake(1, 1);
        String uuid = String.valueOf(snowflake.nextId());
        return uuid;
    }
}
