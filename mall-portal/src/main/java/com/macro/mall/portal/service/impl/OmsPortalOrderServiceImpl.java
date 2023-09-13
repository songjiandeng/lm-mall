package com.macro.mall.portal.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.github.pagehelper.PageHelper;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.enums.ConstantTypesEnum;
import com.macro.mall.common.exception.Asserts;
import com.macro.mall.common.service.RedisService;
import com.macro.mall.mapper.*;
import com.macro.mall.model.*;
import com.macro.mall.portal.component.CancelOrderSender;
import com.macro.mall.portal.dao.PortalOrderDao;
import com.macro.mall.portal.dao.PortalOrderItemDao;
import com.macro.mall.portal.dao.SmsCouponHistoryDao;
import com.macro.mall.portal.domain.*;
import com.macro.mall.portal.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 前台订单管理Service
 * fisher
 */
@Service
@Slf4j
public class OmsPortalOrderServiceImpl implements OmsPortalOrderService {
    @Autowired
    private UmsMemberService memberService;
    @Autowired
    private OmsCartItemService cartItemService;
    @Autowired
    private UmsMemberReceiveAddressService memberReceiveAddressService;
    @Autowired
    private UmsMemberCouponService memberCouponService;
    @Autowired
    private UmsIntegrationConsumeSettingMapper integrationConsumeSettingMapper;
    @Autowired
    private PmsSkuStockMapper skuStockMapper;
    @Autowired
    private SmsCouponHistoryDao couponHistoryDao;
    @Autowired
    private OmsOrderMapper orderMapper;
    @Autowired
    private PortalOrderItemDao orderItemDao;
    @Autowired
    private SmsCouponHistoryMapper couponHistoryMapper;
    @Autowired
    private RedisService redisService;
    @Value("${redis.key.orderId}")
    private String REDIS_KEY_ORDER_ID;
    @Value("${redis.database}")
    private String REDIS_DATABASE;
    @Autowired
    private PortalOrderDao portalOrderDao;
    @Autowired
    private OmsOrderSettingMapper orderSettingMapper;
    @Autowired
    private OmsOrderItemMapper orderItemMapper;
    @Autowired
    private CancelOrderSender cancelOrderSender;

    @Autowired
    private UmsMemberMapper umsMemberMapper;

    @Autowired
    private PmsProductMapper pmsProductMapper;

    @Autowired
    private PmsProductRentMapper pmsProductRentMapper;

    @Autowired
    private OmsOrderStageMapper stageMapper;

    @Autowired
    private OmsOrderItemMapper itemMapper;

    @Override
    public ConfirmOrderResult generateConfirmOrder(List<Long> cartIds) {
        ConfirmOrderResult result = new ConfirmOrderResult();
        //获取购物车信息
        UmsMember currentMember = memberService.getCurrentMember();
        List<CartPromotionItem> cartPromotionItemList = cartItemService.listPromotion(currentMember.getId(),cartIds);
        result.setCartPromotionItemList(cartPromotionItemList);
        //获取用户收货地址列表
        List<UmsMemberReceiveAddress> memberReceiveAddressList = memberReceiveAddressService.list();
        result.setMemberReceiveAddressList(memberReceiveAddressList);
        //获取用户可用优惠券列表
        List<SmsCouponHistoryDetail> couponHistoryDetailList = memberCouponService.listCart(cartPromotionItemList, 1);
        result.setCouponHistoryDetailList(couponHistoryDetailList);
        //获取用户积分
        result.setMemberIntegration(currentMember.getIntegration());
        //获取积分使用规则
        UmsIntegrationConsumeSetting integrationConsumeSetting = integrationConsumeSettingMapper.selectByPrimaryKey(1L);
        result.setIntegrationConsumeSetting(integrationConsumeSetting);
        //计算总金额、活动优惠、应付金额
        ConfirmOrderResult.CalcAmount calcAmount = calcCartAmount(cartPromotionItemList);
        result.setCalcAmount(calcAmount);
        return result;
    }

    @Override
    public Map<String, Object> generateOrder(OrderParam orderParam) {
        List<OmsOrderItem> orderItemList = new ArrayList<>();
        //校验收货地址
        if(orderParam.getMemberReceiveAddressId()==null){
            Asserts.fail("请选择收货地址！");
        }
        //获取购物车及优惠信息
        UmsMember currentMember = memberService.getCurrentMember();
        List<CartPromotionItem> cartPromotionItemList = cartItemService.listPromotion(currentMember.getId(), orderParam.getCartIds());
        for (CartPromotionItem cartPromotionItem : cartPromotionItemList) {
            //生成下单商品信息
            OmsOrderItem orderItem = new OmsOrderItem();
            orderItem.setProductId(cartPromotionItem.getProductId());
            orderItem.setProductName(cartPromotionItem.getProductName());
            orderItem.setProductPic(cartPromotionItem.getProductPic());
            orderItem.setProductAttr(cartPromotionItem.getProductAttr());
            orderItem.setProductBrand(cartPromotionItem.getProductBrand());
            orderItem.setProductSn(cartPromotionItem.getProductSn());
            orderItem.setProductPrice(cartPromotionItem.getPrice());
            orderItem.setProductQuantity(cartPromotionItem.getQuantity());
            orderItem.setProductSkuId(cartPromotionItem.getProductSkuId());
            orderItem.setProductSkuCode(cartPromotionItem.getProductSkuCode());
            orderItem.setProductCategoryId(cartPromotionItem.getProductCategoryId());
            orderItem.setPromotionAmount(cartPromotionItem.getReduceAmount());
            orderItem.setPromotionName(cartPromotionItem.getPromotionMessage());
            orderItem.setGiftIntegration(cartPromotionItem.getIntegration());
            orderItem.setGiftGrowth(cartPromotionItem.getGrowth());
            orderItemList.add(orderItem);
        }
        //判断购物车中商品是否都有库存
        if (!hasStock(cartPromotionItemList)) {
            Asserts.fail("库存不足，无法下单");
        }
        //判断使用使用了优惠券
        if (orderParam.getCouponId() == null) {
            //不用优惠券
            for (OmsOrderItem orderItem : orderItemList) {
                orderItem.setCouponAmount(new BigDecimal(0));
            }
        } else {
            //使用优惠券
            SmsCouponHistoryDetail couponHistoryDetail = getUseCoupon(cartPromotionItemList, orderParam.getCouponId());
            if (couponHistoryDetail == null) {
                Asserts.fail("该优惠券不可用");
            }
            //对下单商品的优惠券进行处理
            handleCouponAmount(orderItemList, couponHistoryDetail);
        }
        //判断是否使用积分
        if (orderParam.getUseIntegration() == null||orderParam.getUseIntegration().equals(0)) {
            //不使用积分
            for (OmsOrderItem orderItem : orderItemList) {
                orderItem.setIntegrationAmount(new BigDecimal(0));
            }
        } else {
            //使用积分
            BigDecimal totalAmount = calcTotalAmount(orderItemList);
            BigDecimal integrationAmount = getUseIntegrationAmount(orderParam.getUseIntegration(), totalAmount, currentMember, orderParam.getCouponId() != null);
            if (integrationAmount.compareTo(new BigDecimal(0)) == 0) {
                Asserts.fail("积分不可用");
            } else {
                //可用情况下分摊到可用商品中
                for (OmsOrderItem orderItem : orderItemList) {
                    BigDecimal perAmount = orderItem.getProductPrice().divide(totalAmount, 3, RoundingMode.HALF_EVEN).multiply(integrationAmount);
                    orderItem.setIntegrationAmount(perAmount);
                }
            }
        }
        //计算order_item的实付金额
        handleRealAmount(orderItemList);
        //进行库存锁定
        lockStock(cartPromotionItemList);
        //根据商品合计、运费、活动优惠、优惠券、积分计算应付金额
        OmsOrder order = new OmsOrder();
        order.setDiscountAmount(new BigDecimal(0));
        order.setTotalAmount(calcTotalAmount(orderItemList));
        order.setFreightAmount(new BigDecimal(0));
        order.setPromotionAmount(calcPromotionAmount(orderItemList));
        order.setPromotionInfo(getOrderPromotionInfo(orderItemList));
        if (orderParam.getCouponId() == null) {
            order.setCouponAmount(new BigDecimal(0));
        } else {
            order.setCouponId(orderParam.getCouponId());
            order.setCouponAmount(calcCouponAmount(orderItemList));
        }
        if (orderParam.getUseIntegration() == null) {
            order.setIntegration(0);
            order.setIntegrationAmount(new BigDecimal(0));
        } else {
            order.setIntegration(orderParam.getUseIntegration());
            order.setIntegrationAmount(calcIntegrationAmount(orderItemList));
        }
        order.setPayAmount(calcPayAmount(order));
        //转化为订单信息并插入数据库
        order.setMemberId(currentMember.getId());
        order.setCreateTime(new Date());
        order.setMemberUsername(currentMember.getUsername());
        //支付方式：0->未支付；1->支付宝；2->微信
        order.setPayType(orderParam.getPayType());
        //订单来源：0->PC订单；1->app订单
        order.setSourceType(1);
        //订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单
        order.setStatus(0);
        //订单类型：0->正常订单；1->秒杀订单
        order.setOrderType(0);
        //收货人信息：姓名、电话、邮编、地址
        UmsMemberReceiveAddress address = memberReceiveAddressService.getItem(orderParam.getMemberReceiveAddressId());
        order.setReceiverName(address.getName());
        order.setReceiverPhone(address.getPhoneNumber());
        order.setReceiverPostCode(address.getPostCode());
        order.setReceiverProvince(address.getProvince());
        order.setReceiverCity(address.getCity());
        order.setReceiverRegion(address.getRegion());
        order.setReceiverDetailAddress(address.getDetailAddress());
        //0->未确认；1->已确认
        order.setConfirmStatus(0);
        order.setDeleteStatus(0);
        //计算赠送积分
        order.setIntegration(calcGifIntegration(orderItemList));
        //计算赠送成长值
        order.setGrowth(calcGiftGrowth(orderItemList));
        //生成订单号
        order.setOrderSn(generateOrderSn(order));
        //设置自动收货天数
        List<OmsOrderSetting> orderSettings = orderSettingMapper.selectByExample(new OmsOrderSettingExample());
        if(CollUtil.isNotEmpty(orderSettings)){
            order.setAutoConfirmDay(orderSettings.get(0).getConfirmOvertime());
        }
        // TODO: 2018/9/3 bill_*,delivery_*
        //插入order表和order_item表
        orderMapper.insert(order);
        for (OmsOrderItem orderItem : orderItemList) {
            orderItem.setOrderId(order.getId());
            orderItem.setOrderSn(order.getOrderSn());
        }
        orderItemDao.insertList(orderItemList);
        //如使用优惠券更新优惠券使用状态
        if (orderParam.getCouponId() != null) {
            updateCouponStatus(orderParam.getCouponId(), currentMember.getId(), 1);
        }
        //如使用积分需要扣除积分
        if (orderParam.getUseIntegration() != null) {
            order.setUseIntegration(orderParam.getUseIntegration());
            if(currentMember.getIntegration()==null){
                currentMember.setIntegration(0);
            }
            memberService.updateIntegration(currentMember.getId(), currentMember.getIntegration() - orderParam.getUseIntegration());
        }
        //删除购物车中的下单商品
        deleteCartItemList(cartPromotionItemList, currentMember);
        //发送延迟消息取消订单
        sendDelayMessageCancelOrder(order.getId());
        Map<String, Object> result = new HashMap<>();
        result.put("order", order);
        result.put("orderItemList", orderItemList);
        return result;
    }

    @Override
    public Integer paySuccess(Long orderId, Integer payType) {
        //修改订单支付状态
        OmsOrder order = new OmsOrder();
        order.setId(orderId);
        order.setStatus(1);
        order.setPaymentTime(new Date());
        order.setPayType(payType);
        orderMapper.updateByPrimaryKeySelective(order);
        //恢复所有下单商品的锁定库存，扣减真实库存
        OmsOrderDetail orderDetail = portalOrderDao.getDetail(orderId);
        int count = portalOrderDao.updateSkuStock(orderDetail.getOrderItemList());
        return count;
    }

    @Override
    public Integer cancelTimeOutOrder() {
        Integer count=0;
        OmsOrderSetting orderSetting = orderSettingMapper.selectByPrimaryKey(1L);
        //查询超时、未支付的订单及订单详情
        List<OmsOrderDetail> timeOutOrders = portalOrderDao.getTimeOutOrders(orderSetting.getNormalOrderOvertime());
        if (CollectionUtils.isEmpty(timeOutOrders)) {
            return count;
        }
        //修改订单状态为交易取消
        List<Long> ids = new ArrayList<>();
        for (OmsOrderDetail timeOutOrder : timeOutOrders) {
            ids.add(timeOutOrder.getId());
        }
        portalOrderDao.updateOrderStatus(ids, 4);
        for (OmsOrderDetail timeOutOrder : timeOutOrders) {
            //解除订单商品库存锁定
            portalOrderDao.releaseSkuStockLock(timeOutOrder.getOrderItemList());
            //修改优惠券使用状态
            updateCouponStatus(timeOutOrder.getCouponId(), timeOutOrder.getMemberId(), 0);
            //返还使用积分
            if (timeOutOrder.getUseIntegration() != null) {
                UmsMember member = memberService.getById(timeOutOrder.getMemberId());
                memberService.updateIntegration(timeOutOrder.getMemberId(), member.getIntegration() + timeOutOrder.getUseIntegration());
            }
        }
        return timeOutOrders.size();
    }

    @Override
    public void cancelOrder(Long orderId) {
        //查询未付款的取消订单
        OmsOrderExample example = new OmsOrderExample();
        example.createCriteria().andIdEqualTo(orderId).andStatusEqualTo(0).andDeleteStatusEqualTo(0);
        List<OmsOrder> cancelOrderList = orderMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(cancelOrderList)) {
            return;
        }
        OmsOrder cancelOrder = cancelOrderList.get(0);
        if (cancelOrder != null) {
            //修改订单状态为取消
            cancelOrder.setStatus(4);
            orderMapper.updateByPrimaryKeySelective(cancelOrder);
            OmsOrderItemExample orderItemExample = new OmsOrderItemExample();
            orderItemExample.createCriteria().andOrderIdEqualTo(orderId);
            List<OmsOrderItem> orderItemList = orderItemMapper.selectByExample(orderItemExample);
            //解除订单商品库存锁定
            if (!CollectionUtils.isEmpty(orderItemList)) {
                portalOrderDao.releaseSkuStockLock(orderItemList);
            }
            //修改优惠券使用状态
            updateCouponStatus(cancelOrder.getCouponId(), cancelOrder.getMemberId(), 0);
            //返还使用积分
            if (cancelOrder.getUseIntegration() != null) {
                UmsMember member = memberService.getById(cancelOrder.getMemberId());
                memberService.updateIntegration(cancelOrder.getMemberId(), member.getIntegration() + cancelOrder.getUseIntegration());
            }
        }
    }

    @Override
    public void sendDelayMessageCancelOrder(Long orderId) {
        //获取订单超时时间
        OmsOrderSetting orderSetting = orderSettingMapper.selectByPrimaryKey(1L);
        long delayTimes = orderSetting.getNormalOrderOvertime() * 60 * 1000;
        //发送延迟消息
        cancelOrderSender.sendMessage(orderId, delayTimes);
    }

    @Override
    public void confirmReceiveOrder(Long orderId) {
        UmsMember member = memberService.getCurrentMember();
        OmsOrder order = orderMapper.selectByPrimaryKey(orderId);
        if(!member.getId().equals(order.getMemberId())){
            Asserts.fail("不能确认他人订单！");
        }
        if(order.getStatus()!=2){
            Asserts.fail("该订单还未发货！");
        }
        order.setStatus(3);
        order.setConfirmStatus(1);
        order.setReceiveTime(new Date());
        orderMapper.updateByPrimaryKey(order);
    }

    @Override
    public CommonPage<OmsOrderDetail> list(Integer status, Integer pageNum, Integer pageSize) {
        if(status==-1){
            status = null;
        }
        UmsMember member = memberService.getCurrentMember();
        PageHelper.startPage(pageNum,pageSize);
        OmsOrderExample orderExample = new OmsOrderExample();
        OmsOrderExample.Criteria criteria = orderExample.createCriteria();
        criteria.andDeleteStatusEqualTo(0)
                .andMemberIdEqualTo(member.getId());
        if(status!=null){
            criteria.andStatusEqualTo(status);
        }
        orderExample.setOrderByClause("create_time desc");
        List<OmsOrder> orderList = orderMapper.selectByExample(orderExample);
        CommonPage<OmsOrder> orderPage = CommonPage.restPage(orderList);
        //设置分页信息
        CommonPage<OmsOrderDetail> resultPage = new CommonPage<>();
        resultPage.setPageNum(orderPage.getPageNum());
        resultPage.setPageSize(orderPage.getPageSize());
        resultPage.setTotal(orderPage.getTotal());
        resultPage.setTotalPage(orderPage.getTotalPage());
        if(CollUtil.isEmpty(orderList)){
            return resultPage;
        }
        //设置数据信息
        List<Long> orderIds = orderList.stream().map(OmsOrder::getId).collect(Collectors.toList());
        OmsOrderItemExample orderItemExample = new OmsOrderItemExample();
        orderItemExample.createCriteria().andOrderIdIn(orderIds);
        List<OmsOrderItem> orderItemList = orderItemMapper.selectByExample(orderItemExample);
        List<OmsOrderDetail> orderDetailList = new ArrayList<>();
        for (OmsOrder omsOrder : orderList) {
            OmsOrderDetail orderDetail = new OmsOrderDetail();
            BeanUtil.copyProperties(omsOrder,orderDetail);
            List<OmsOrderItem> relatedItemList = orderItemList.stream().filter(item -> item.getOrderId().equals(orderDetail.getId())).collect(Collectors.toList());
            orderDetail.setOrderItemList(relatedItemList);
            orderDetailList.add(orderDetail);
        }
        resultPage.setList(orderDetailList);
        return resultPage;
    }

    @Override
    public OmsOrderDetail detail(Long orderId) {
        OmsOrder omsOrder = orderMapper.selectByPrimaryKey(orderId);
        OmsOrderItemExample example = new OmsOrderItemExample();
        example.createCriteria().andOrderIdEqualTo(orderId);
        List<OmsOrderItem> orderItemList = orderItemMapper.selectByExample(example);
        OmsOrderDetail orderDetail = new OmsOrderDetail();
        BeanUtil.copyProperties(omsOrder,orderDetail);
        orderDetail.setOrderItemList(orderItemList);
        return orderDetail;
    }

    @Override
    public void deleteOrder(Long orderId) {
        UmsMember member = memberService.getCurrentMember();
        OmsOrder order = orderMapper.selectByPrimaryKey(orderId);
        if(!member.getId().equals(order.getMemberId())){
            Asserts.fail("不能删除他人订单！");
        }
        if(order.getStatus()==3||order.getStatus()==4){
            order.setDeleteStatus(1);
            orderMapper.updateByPrimaryKey(order);
        }else{
            Asserts.fail("只能删除已完成或已关闭的订单！");
        }
    }

    @Override
    public CommonResult<OrderPreviewDetail> orderPreview(OmsOrderCreateParam omsOrderCreateParam) {
        log.info("【订单预览】【入参】:{}", JSONUtil.toJsonStr(omsOrderCreateParam));
        OrderPreviewDetail detail = null;
        try{
            Long memberId = omsOrderCreateParam.getMemberId();
            Long productId = omsOrderCreateParam.getProductId();
            PmsProduct pmsProduct = pmsProductMapper.selectByPrimaryKey(productId);
            //获取用户信息
            UmsMember member = umsMemberMapper.selectByPrimaryKey(memberId);
            //获取规格
            List<Long> skuIds = omsOrderCreateParam.getSkuIds();
            for(Long skuId : skuIds){
                PmsSkuStock pmsSkuStock = skuStockMapper.selectByPrimaryKey(skuId);
                detail = new OrderPreviewDetail();
                detail.setPic(pmsProduct.getPic());
                detail.setName(pmsProduct.getName());
                detail.setSkuId(skuId);
                detail.setSpData(pmsSkuStock.getSpData());
                //预设开始结束时间
                Date startTime = DateUtil.offsetDay(new Date(),1);
                Integer billingCycle = pmsProduct.getBillingCycle();
                Integer billingCycleUnit = pmsProduct.getBillingCycleUnit();
                Integer repaymentNumber = pmsProduct.getRepaymentNumber();
                String desc = ConstantTypesEnum.CycleUnit.getByType(billingCycleUnit).getDesc();
                Integer leaseNum = repaymentNumber * Integer.parseInt(desc) * billingCycle;
                Date endTime = DateUtil.offsetDay(new Date(),leaseNum);
                detail.setStartTime(startTime);
                detail.setEndTime(endTime);
                detail.setLeaseNum(leaseNum);
                PmsProductRentExample rent = new PmsProductRentExample();
                rent.createCriteria().andProductIdEqualTo(productId).andSkuIdEqualTo(skuId);
                List<PmsProductRent> pmsProductRentList = pmsProductRentMapper.selectByExample(rent);
                double sum = pmsProductRentList.stream().filter(f -> f.getStages() == 1).mapToDouble(m -> m.getStagesPrice().doubleValue()).sum();
                detail.setFirstRentPrice(new BigDecimal(sum));
                BigDecimal firstPayPrice = detail.getFirstRentPrice();
                String addServer = pmsProduct.getAddServer();
                if(!StrUtil.isEmpty(addServer)){
                    detail.setAddServer(addServer);
                    detail.setAddServerPrice(pmsProduct.getAddServerPrice());
                    firstPayPrice = firstPayPrice.add(pmsProduct.getAddServerPrice());
                }
                Integer buyoutType = pmsProduct.getBuyoutType();
                detail.setBuyoutType(buyoutType);
                detail.setBuyoutPrice(pmsProduct.getBuyoutPrice());
                //剩余总租金
                double totalRentPrice = pmsProductRentList.stream().filter(f -> f.getStages() != 1).mapToDouble(m -> m.getStagesPrice().doubleValue()).sum();
                detail.setTotalRentPrice(new BigDecimal(totalRentPrice));
                BigDecimal residuePrice = detail.getTotalRentPrice();
                if(buyoutType==0){
                    firstPayPrice = firstPayPrice.add(pmsProduct.getBuyoutPrice());
                }else{
                    residuePrice = residuePrice.add(detail.getBuyoutPrice());
                }
                detail.setResiduePrice(residuePrice);
                detail.setFirstPayPrice(firstPayPrice);
                detail.setRepaymentType(pmsProduct.getRepaymentType());
                detail.setIsReal(member.getIsReal());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return CommonResult.success(detail);
    }

    @Override
    public CommonResult<String> applyforLease(OmsOrderCreateParam omsOrderCreateParam) {
        log.info("【客户端申请租赁】【入参】:{}",JSONUtil.toJsonStr(omsOrderCreateParam));
        String orderSn = "";
        try {
            //获取商品id
            Long productId = omsOrderCreateParam.getProductId();
            PmsProduct pmsProduct = pmsProductMapper.selectByPrimaryKey(productId);
            //获取用户信息
            Long memberId = omsOrderCreateParam.getMemberId();
            UmsMember member = umsMemberMapper.selectByPrimaryKey(memberId);
            //获取规格
            List<Long> skuIds = omsOrderCreateParam.getSkuIds();
            for(Long skuId : skuIds){
                //获取规格数据，以及分期数据
                PmsSkuStock pmsSkuStock = skuStockMapper.selectByPrimaryKey(skuId);
                //生产订单，订单商品，虚拟分期账单
                //订单
                OmsOrder order = new OmsOrder();
                BeanUtil.copyProperties(omsOrderCreateParam,order,true);
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
                order.setStatus(0);
                order.setConfirmStatus(0);
                order.setDeleteStatus(0);
                order.setProductName(pmsProduct.getName());
                order.setAdminName(StrUtil.isEmpty(member.getAdminName())?"":member.getAdminName());
                order.setChannelName(StrUtil.isEmpty(member.getChannelName())?"":member.getChannelName());
                orderMapper.insertSelective(order);
                //订单商品
                OmsOrderItem omsOrderItem = new OmsOrderItem();
                BeanUtil.copyProperties(pmsProduct,omsOrderItem,true);
                omsOrderItem.setOrderId(order.getId());
                omsOrderItem.setOrderSn(orderSn);
                omsOrderItem.setProductId(productId);
                omsOrderItem.setProductPic(pmsProduct.getPic());
                omsOrderItem.setProductName(pmsProduct.getName());
                omsOrderItem.setProductPrice(pmsSkuStock.getPrice());
                omsOrderItem.setProductSkuId(skuId);
                omsOrderItem.setProductSkuCode(pmsSkuStock.getSkuCode());
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
                itemMapper.insertSelective(omsOrderItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("【客户端申请租赁异常】【入参】:{}",JSONUtil.toJsonStr(omsOrderCreateParam));
            return CommonResult.failed("申请租赁异常");
        }
        return CommonResult.success(orderSn);
    }

    @Override
    public CommonResult<OrderPreviewDetail> orderDetail(OmsOrderCreateParam omsOrderCreateParam) {
        log.info("【用户获取实时订单详情】【入参】：{}",JSONUtil.toJsonStr(omsOrderCreateParam));
        try{
            //根据用户id,orderSn 获取当前订单
            Long memberId = omsOrderCreateParam.getMemberId();
            String orderSn = omsOrderCreateParam.getOrderSn();
            OmsOrderExample example = new OmsOrderExample();
            example.createCriteria().andMemberIdEqualTo(memberId).andOrderSnEqualTo(orderSn);
            List<OmsOrder> omsOrders = orderMapper.selectByExample(example);
            if(!omsOrders.isEmpty()){
                OmsOrder order = omsOrders.get(0);
                Long id = order.getId();
                OmsOrderItemExample itemExample = new OmsOrderItemExample();
                itemExample.createCriteria().andOrderIdEqualTo(id);
                List<OmsOrderItem> omsOrderItems = itemMapper.selectByExample(itemExample);
                OmsOrderItem omsOrderItem = omsOrderItems.get(0);
                OrderPreviewDetail detail = new OrderPreviewDetail();
                detail.setPic(omsOrderItem.getProductPic());
                detail.setName(omsOrderItem.getProductName());
                detail.setSpData(omsOrderItem.getProductAttr());
                detail.setOrderId(id);
                detail.setOrderSn(orderSn);
                detail.setNote(order.getNote());
                detail.setLeaseNum(omsOrderItem.getLeaseNum());
                detail.setStartTime(omsOrderItem.getStartTime());
                detail.setEndTime(omsOrderItem.getEndTime());
                detail.setStageNum(omsOrderItem.getStageNum());
                Integer status = order.getStatus();
                detail.setStatus(status);
                //验证订单状态，待支付，待审核，待发货，已发货状态返回首期支付金额
                //租用中状态根据账单显现最近账单详情
                //逾期状态显示逾期详情
                PmsProductRentExample rent = new PmsProductRentExample();
                rent.createCriteria().andProductIdEqualTo(omsOrderItem.getProductId()).andSkuIdEqualTo(omsOrderItem.getProductSkuId());
                List<PmsProductRent> pmsProductRentList = pmsProductRentMapper.selectByExample(rent);
                List<PmsProductRent> collect = pmsProductRentList.stream().filter(f -> f.getStages() == 1).collect(Collectors.toList());
                BigDecimal firstRentPrice = collect.get(0).getStagesPrice();
                detail.setFirstRentPrice(firstRentPrice);
                BigDecimal firstPayPrice = firstRentPrice;
                if(!Objects.isNull(omsOrderItem.getBuyoutPrice())){
                    BigDecimal periodPrice = omsOrderItem.getBuyoutPrice();
                    detail.setBuyoutPrice(periodPrice);
                    detail.setBuyoutType(omsOrderItem.getBuyoutType());
                    if(omsOrderItem.getBuyoutType()==0){
                        firstPayPrice = firstPayPrice.add(periodPrice);
                    }
                }
                if(!StrUtil.isEmpty(omsOrderItem.getAddServer())){
                    detail.setAddServer(omsOrderItem.getAddServer());
                    BigDecimal addServerPrice = omsOrderItem.getAddServerPrice();
                    firstPayPrice = firstPayPrice.add(addServerPrice);
                }
                detail.setFirstPayPrice(firstPayPrice);
                detail.setCreateTime(order.getCreateTime());
                BigDecimal residuePrice = new BigDecimal(0);
                BigDecimal totalRentPrice = new BigDecimal(0);
                if (status == (ConstantTypesEnum.OrderStatus.PROCESS.getValue())) {
                    double total = pmsProductRentList.stream().filter(f -> f.getStages() != 1).mapToDouble(m -> m.getStagesPrice().doubleValue()).sum();
                    totalRentPrice = new BigDecimal(total);
                    if(omsOrderItem.getBuyoutType()==1){
                        residuePrice = totalRentPrice.add(omsOrderItem.getBuyoutPrice());
                    }
                } else {
                    OmsOrderStageExample stageExample = new OmsOrderStageExample();
                    stageExample.createCriteria().andOrderIdEqualTo(id);
                    List<OmsOrderStage> omsOrderStages = stageMapper.selectByExample(stageExample);//账单
                    double sum = omsOrderStages.stream().filter(f -> f.getStatus() != 2).mapToDouble(m -> m.getPeriodPrice().doubleValue()).sum();
                    totalRentPrice = new BigDecimal(sum);
                    //剩余归还总金额
                    double overPrice = omsOrderStages.stream().filter(f -> f.getStatus() != 2).mapToDouble(m -> m.getOverPrice().doubleValue()).sum();
                    double payPrice = omsOrderStages.stream().filter(f -> f.getStatus() != 2).mapToDouble(m -> m.getPayPrice().doubleValue()).sum();
                    residuePrice = totalRentPrice.add(new BigDecimal(overPrice)).subtract(new BigDecimal(payPrice));
                    detail.setOmsOrderStages(omsOrderStages);
                }
                detail.setResiduePrice(residuePrice);
                detail.setTotalRentPrice(totalRentPrice);
                if(status==ConstantTypesEnum.OrderStatus.USED.getValue()){
                    //获取最近归还时间
                    List<OmsOrderStage> omsOrderStages = detail.getOmsOrderStages();
                    List<OmsOrderStage> collect1 = omsOrderStages.stream().filter(f -> f.getStatus() != 2).sorted(Comparator.comparing(OmsOrderStage::getPayNo)).collect(Collectors.toList());
                    Date repaymentTime = collect1.get(0).getRepaymentTime();
                    //距离当前天数
                    int diffDays = (int) DateUtil.between(repaymentTime, new Date(), DateUnit.DAY);
                    detail.setDueDay(diffDays);
                    detail.setFirstRentPrice(collect1.get(0).getPeriodPrice());
                    BigDecimal periodPrice = collect1.get(0).getPeriodPrice();
                    detail.setFirstPayPrice(periodPrice.subtract(collect1.get(0).getPayPrice()));
                }
                if(status==ConstantTypesEnum.OrderStatus.DELAY.getValue()){
                    //获取逾期天数，违约金
                    List<OmsOrderStage> omsOrderStages = detail.getOmsOrderStages();
                    List<OmsOrderStage> collect1 = omsOrderStages.stream().filter(f -> f.getStatus() == 3).sorted(Comparator.comparing(OmsOrderStage::getPayNo).reversed()).collect(Collectors.toList());
                    Date repaymentTime = collect1.get(0).getRepaymentTime();
                    //距离当前天数
                    int diffDays = (int) DateUtil.between(repaymentTime, new Date(), DateUnit.DAY);
                    detail.setOverDay(diffDays);
                    //计算违约金
                    double sum = collect1.stream().mapToDouble(m -> m.getOverPrice().doubleValue()).sum();
                    detail.setOverPrice(new BigDecimal(sum));
                    double pay = collect1.stream().mapToDouble(m -> m.getPeriodPrice().add(m.getOverPrice()).subtract(m.getPayPrice()).doubleValue()).sum();
                    detail.setFirstPayPrice(new BigDecimal(pay));
                    double sum1 = collect1.stream().mapToDouble(m -> m.getPeriodPrice().doubleValue()).sum();
                    detail.setFirstRentPrice(new BigDecimal(sum1));
                }
                return CommonResult.success(detail);
            }
        }catch (Exception e){
            e.printStackTrace();
            log.info("【用户获取实时订单详情异常】【入参】：{}",JSONUtil.toJsonStr(omsOrderCreateParam));
        }
        return CommonResult.failed("获取实时订单详情失败");
    }

    @Override
    public CommonPage<OrderPreviewDetail> orderList(OmsOrderCreateParam omsOrderCreateParam) {
        log.info("【用户获取实时订单列表】【入参】：{}",JSONUtil.toJsonStr(omsOrderCreateParam));
        List<OrderPreviewDetail> detailList = new ArrayList<>();
        PageHelper.startPage(omsOrderCreateParam.getPageNum(), omsOrderCreateParam.getPageSize());
        try {
            //根据状态获取对应订单列表
            Long memberId = omsOrderCreateParam.getMemberId();
            OmsOrderExample example = new OmsOrderExample();
            OmsOrderExample.Criteria criteria = example.createCriteria();
            criteria.andMemberIdEqualTo(memberId);
            Integer status = omsOrderCreateParam.getStatus();
            if(!Objects.isNull(status)){
                criteria.andStatusEqualTo(status);
            }
            example.setOrderByClause("create_time desc");
            List<OmsOrder> omsOrders = orderMapper.selectByExample(example);
            for(OmsOrder order : omsOrders){
                Long id = order.getId();
                //获取订单商品
                OmsOrderItemExample itemExample = new OmsOrderItemExample();
                itemExample.createCriteria().andOrderIdEqualTo(id);
                OmsOrderItem omsOrderItem = itemMapper.selectByExample(itemExample).get(0);
                OrderPreviewDetail detail = BeanUtil.copyProperties(omsOrderItem,OrderPreviewDetail.class);
                detail.setPic(omsOrderItem.getProductPic());
                detail.setName(omsOrderItem.getProductName());
                detail.setSkuId(omsOrderItem.getProductSkuId());
                detail.setSpData(omsOrderItem.getProductAttr());
                detail.setNote(order.getNote());
                detail.setCreateTime(order.getCreateTime());
                //根据状态获取总的租金，几天后到期，逾期天数
                PmsProductRentExample rent = new PmsProductRentExample();
                rent.createCriteria().andProductIdEqualTo(omsOrderItem.getProductId()).andSkuIdEqualTo(omsOrderItem.getProductSkuId());
                List<PmsProductRent> pmsProductRentList = pmsProductRentMapper.selectByExample(rent);
                BigDecimal totalRentPrice = new BigDecimal(0);
                if (status == (ConstantTypesEnum.OrderStatus.PROCESS.getValue())) {
                    double total = pmsProductRentList.stream().mapToDouble(m -> m.getStagesPrice().doubleValue()).sum();
                    totalRentPrice = new BigDecimal(total);
                    if(!StrUtil.isEmpty(omsOrderItem.getAddServer())){
                        totalRentPrice = totalRentPrice.add(omsOrderItem.getAddServerPrice());
                    }
                    if(!Objects.isNull(omsOrderItem.getBuyoutType())){
                        totalRentPrice = totalRentPrice.add(omsOrderItem.getBuyoutPrice());
                    }
                } else {
                    OmsOrderStageExample stageExample = new OmsOrderStageExample();
                    stageExample.createCriteria().andOrderIdEqualTo(id);
                    List<OmsOrderStage> omsOrderStages = stageMapper.selectByExample(stageExample);//账单
                    detail.setOmsOrderStages(omsOrderStages);
                    // TODO: 2023/9/3 总租金
                }
                detail.setTotalRentPrice(totalRentPrice);
                if(status==ConstantTypesEnum.OrderStatus.USED.getValue()){
                    //获取最近归还时间
                    List<OmsOrderStage> omsOrderStages = detail.getOmsOrderStages();
                    List<OmsOrderStage> collect1 = omsOrderStages.stream().filter(f -> f.getStatus() != 2).sorted(Comparator.comparing(OmsOrderStage::getPayNo)).collect(Collectors.toList());
                    Date repaymentTime = collect1.get(0).getRepaymentTime();
                    //距离当前天数
                    int diffDays = (int) DateUtil.between(repaymentTime, new Date(), DateUnit.DAY);
                    detail.setDueDay(diffDays);
                }
                if(status==ConstantTypesEnum.OrderStatus.DELAY.getValue()){
                    //获取逾期天数，违约金
                    List<OmsOrderStage> omsOrderStages = detail.getOmsOrderStages();
                    List<OmsOrderStage> collect1 = omsOrderStages.stream().filter(f -> f.getStatus() == 3).sorted(Comparator.comparing(OmsOrderStage::getPayNo).reversed()).collect(Collectors.toList());
                    Date repaymentTime = collect1.get(0).getRepaymentTime();
                    //距离当前天数
                    int diffDays = (int) DateUtil.between(repaymentTime, new Date(), DateUnit.DAY);
                    detail.setOverDay(diffDays);
                }
                detailList.add(detail);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CommonPage.restPage(detailList);
    }

    @Override
    public CommonResult<List<OmsOrderStatus>> orderStatus(OmsOrderCreateParam omsOrderCreateParam) {
        Long memberId = omsOrderCreateParam.getMemberId();
        List<OmsOrderStatus> omsOrderStatusList = portalOrderDao.selectGroupByStatus(memberId);
        return CommonResult.success(omsOrderStatusList);
    }

    public  String getRanom() {
        Snowflake snowflake = IdUtil.getSnowflake(1, 1);
        String uuid = String.valueOf(snowflake.nextId());
        return uuid;
    }

    /**
     * 生成18位订单编号:8位日期+2位平台号码+2位支付方式+6位以上自增id
     */
    private String generateOrderSn(OmsOrder order) {
        StringBuilder sb = new StringBuilder();
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String key = REDIS_DATABASE+":"+ REDIS_KEY_ORDER_ID + date;
        Long increment = redisService.incr(key, 1);
        sb.append(date);
        sb.append(String.format("%02d", order.getSourceType()));
        sb.append(String.format("%02d", order.getPayType()));
        String incrementStr = increment.toString();
        if (incrementStr.length() <= 6) {
            sb.append(String.format("%06d", increment));
        } else {
            sb.append(incrementStr);
        }
        return sb.toString();
    }

    /**
     * 删除下单商品的购物车信息
     */
    private void deleteCartItemList(List<CartPromotionItem> cartPromotionItemList, UmsMember currentMember) {
        List<Long> ids = new ArrayList<>();
        for (CartPromotionItem cartPromotionItem : cartPromotionItemList) {
            ids.add(cartPromotionItem.getId());
        }
        cartItemService.delete(currentMember.getId(), ids);
    }

    /**
     * 计算该订单赠送的成长值
     */
    private Integer calcGiftGrowth(List<OmsOrderItem> orderItemList) {
        Integer sum = 0;
        for (OmsOrderItem orderItem : orderItemList) {
            sum = sum + orderItem.getGiftGrowth() * orderItem.getProductQuantity();
        }
        return sum;
    }

    /**
     * 计算该订单赠送的积分
     */
    private Integer calcGifIntegration(List<OmsOrderItem> orderItemList) {
        int sum = 0;
        for (OmsOrderItem orderItem : orderItemList) {
            sum += orderItem.getGiftIntegration() * orderItem.getProductQuantity();
        }
        return sum;
    }

    /**
     * 将优惠券信息更改为指定状态
     *
     * @param couponId  优惠券id
     * @param memberId  会员id
     * @param useStatus 0->未使用；1->已使用
     */
    private void updateCouponStatus(Long couponId, Long memberId, Integer useStatus) {
        if (couponId == null) return;
        //查询第一张优惠券
        SmsCouponHistoryExample example = new SmsCouponHistoryExample();
        example.createCriteria().andMemberIdEqualTo(memberId)
                .andCouponIdEqualTo(couponId).andUseStatusEqualTo(useStatus == 0 ? 1 : 0);
        List<SmsCouponHistory> couponHistoryList = couponHistoryMapper.selectByExample(example);
        if (!CollectionUtils.isEmpty(couponHistoryList)) {
            SmsCouponHistory couponHistory = couponHistoryList.get(0);
            couponHistory.setUseTime(new Date());
            couponHistory.setUseStatus(useStatus);
            couponHistoryMapper.updateByPrimaryKeySelective(couponHistory);
        }
    }

    private void handleRealAmount(List<OmsOrderItem> orderItemList) {
        for (OmsOrderItem orderItem : orderItemList) {
            //原价-促销优惠-优惠券抵扣-积分抵扣
            BigDecimal realAmount = orderItem.getProductPrice()
                    .subtract(orderItem.getPromotionAmount())
                    .subtract(orderItem.getCouponAmount())
                    .subtract(orderItem.getIntegrationAmount());
            orderItem.setRealAmount(realAmount);
        }
    }

    /**
     * 获取订单促销信息
     */
    private String getOrderPromotionInfo(List<OmsOrderItem> orderItemList) {
        StringBuilder sb = new StringBuilder();
        for (OmsOrderItem orderItem : orderItemList) {
            sb.append(orderItem.getPromotionName());
            sb.append(";");
        }
        String result = sb.toString();
        if (result.endsWith(";")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    /**
     * 计算订单应付金额
     */
    private BigDecimal calcPayAmount(OmsOrder order) {
        //总金额+运费-促销优惠-优惠券优惠-积分抵扣
        BigDecimal payAmount = order.getTotalAmount()
                .add(order.getFreightAmount())
                .subtract(order.getPromotionAmount())
                .subtract(order.getCouponAmount())
                .subtract(order.getIntegrationAmount());
        return payAmount;
    }

    /**
     * 计算订单优惠券金额
     */
    private BigDecimal calcIntegrationAmount(List<OmsOrderItem> orderItemList) {
        BigDecimal integrationAmount = new BigDecimal(0);
        for (OmsOrderItem orderItem : orderItemList) {
            if (orderItem.getIntegrationAmount() != null) {
                integrationAmount = integrationAmount.add(orderItem.getIntegrationAmount().multiply(new BigDecimal(orderItem.getProductQuantity())));
            }
        }
        return integrationAmount;
    }

    /**
     * 计算订单优惠券金额
     */
    private BigDecimal calcCouponAmount(List<OmsOrderItem> orderItemList) {
        BigDecimal couponAmount = new BigDecimal(0);
        for (OmsOrderItem orderItem : orderItemList) {
            if (orderItem.getCouponAmount() != null) {
                couponAmount = couponAmount.add(orderItem.getCouponAmount().multiply(new BigDecimal(orderItem.getProductQuantity())));
            }
        }
        return couponAmount;
    }

    /**
     * 计算订单活动优惠
     */
    private BigDecimal calcPromotionAmount(List<OmsOrderItem> orderItemList) {
        BigDecimal promotionAmount = new BigDecimal(0);
        for (OmsOrderItem orderItem : orderItemList) {
            if (orderItem.getPromotionAmount() != null) {
                promotionAmount = promotionAmount.add(orderItem.getPromotionAmount().multiply(new BigDecimal(orderItem.getProductQuantity())));
            }
        }
        return promotionAmount;
    }

    /**
     * 获取可用积分抵扣金额
     *
     * @param useIntegration 使用的积分数量
     * @param totalAmount    订单总金额
     * @param currentMember  使用的用户
     * @param hasCoupon      是否已经使用优惠券
     */
    private BigDecimal getUseIntegrationAmount(Integer useIntegration, BigDecimal totalAmount, UmsMember currentMember, boolean hasCoupon) {
        BigDecimal zeroAmount = new BigDecimal(0);
        //判断用户是否有这么多积分
        if (useIntegration.compareTo(currentMember.getIntegration()) > 0) {
            return zeroAmount;
        }
        //根据积分使用规则判断是否可用
        //是否可与优惠券共用
        UmsIntegrationConsumeSetting integrationConsumeSetting = integrationConsumeSettingMapper.selectByPrimaryKey(1L);
        if (hasCoupon && integrationConsumeSetting.getCouponStatus().equals(0)) {
            //不可与优惠券共用
            return zeroAmount;
        }
        //是否达到最低使用积分门槛
        if (useIntegration.compareTo(integrationConsumeSetting.getUseUnit()) < 0) {
            return zeroAmount;
        }
        //是否超过订单抵用最高百分比
        BigDecimal integrationAmount = new BigDecimal(useIntegration).divide(new BigDecimal(integrationConsumeSetting.getUseUnit()), 2, RoundingMode.HALF_EVEN);
        BigDecimal maxPercent = new BigDecimal(integrationConsumeSetting.getMaxPercentPerOrder()).divide(new BigDecimal(100), 2, RoundingMode.HALF_EVEN);
        if (integrationAmount.compareTo(totalAmount.multiply(maxPercent)) > 0) {
            return zeroAmount;
        }
        return integrationAmount;
    }

    /**
     * 对优惠券优惠进行处理
     *
     * @param orderItemList       order_item列表
     * @param couponHistoryDetail 可用优惠券详情
     */
    private void handleCouponAmount(List<OmsOrderItem> orderItemList, SmsCouponHistoryDetail couponHistoryDetail) {
        SmsCoupon coupon = couponHistoryDetail.getCoupon();
        if (coupon.getUseType().equals(0)) {
            //全场通用
            calcPerCouponAmount(orderItemList, coupon);
        } else if (coupon.getUseType().equals(1)) {
            //指定分类
            List<OmsOrderItem> couponOrderItemList = getCouponOrderItemByRelation(couponHistoryDetail, orderItemList, 0);
            calcPerCouponAmount(couponOrderItemList, coupon);
        } else if (coupon.getUseType().equals(2)) {
            //指定商品
            List<OmsOrderItem> couponOrderItemList = getCouponOrderItemByRelation(couponHistoryDetail, orderItemList, 1);
            calcPerCouponAmount(couponOrderItemList, coupon);
        }
    }

    /**
     * 对每个下单商品进行优惠券金额分摊的计算
     *
     * @param orderItemList 可用优惠券的下单商品商品
     */
    private void calcPerCouponAmount(List<OmsOrderItem> orderItemList, SmsCoupon coupon) {
        BigDecimal totalAmount = calcTotalAmount(orderItemList);
        for (OmsOrderItem orderItem : orderItemList) {
            //(商品价格/可用商品总价)*优惠券面额
            BigDecimal couponAmount = orderItem.getProductPrice().divide(totalAmount, 3, RoundingMode.HALF_EVEN).multiply(coupon.getAmount());
            orderItem.setCouponAmount(couponAmount);
        }
    }

    /**
     * 获取与优惠券有关系的下单商品
     *
     * @param couponHistoryDetail 优惠券详情
     * @param orderItemList       下单商品
     * @param type                使用关系类型：0->相关分类；1->指定商品
     */
    private List<OmsOrderItem> getCouponOrderItemByRelation(SmsCouponHistoryDetail couponHistoryDetail, List<OmsOrderItem> orderItemList, int type) {
        List<OmsOrderItem> result = new ArrayList<>();
        if (type == 0) {
            List<Long> categoryIdList = new ArrayList<>();
            for (SmsCouponProductCategoryRelation productCategoryRelation : couponHistoryDetail.getCategoryRelationList()) {
                categoryIdList.add(productCategoryRelation.getProductCategoryId());
            }
            for (OmsOrderItem orderItem : orderItemList) {
                if (categoryIdList.contains(orderItem.getProductCategoryId())) {
                    result.add(orderItem);
                } else {
                    orderItem.setCouponAmount(new BigDecimal(0));
                }
            }
        } else if (type == 1) {
            List<Long> productIdList = new ArrayList<>();
            for (SmsCouponProductRelation productRelation : couponHistoryDetail.getProductRelationList()) {
                productIdList.add(productRelation.getProductId());
            }
            for (OmsOrderItem orderItem : orderItemList) {
                if (productIdList.contains(orderItem.getProductId())) {
                    result.add(orderItem);
                } else {
                    orderItem.setCouponAmount(new BigDecimal(0));
                }
            }
        }
        return result;
    }

    /**
     * 获取该用户可以使用的优惠券
     *
     * @param cartPromotionItemList 购物车优惠列表
     * @param couponId              使用优惠券id
     */
    private SmsCouponHistoryDetail getUseCoupon(List<CartPromotionItem> cartPromotionItemList, Long couponId) {
        List<SmsCouponHistoryDetail> couponHistoryDetailList = memberCouponService.listCart(cartPromotionItemList, 1);
        for (SmsCouponHistoryDetail couponHistoryDetail : couponHistoryDetailList) {
            if (couponHistoryDetail.getCoupon().getId().equals(couponId)) {
                return couponHistoryDetail;
            }
        }
        return null;
    }

    /**
     * 计算总金额
     */
    private BigDecimal calcTotalAmount(List<OmsOrderItem> orderItemList) {
        BigDecimal totalAmount = new BigDecimal("0");
        for (OmsOrderItem item : orderItemList) {
            totalAmount = totalAmount.add(item.getProductPrice().multiply(new BigDecimal(item.getProductQuantity())));
        }
        return totalAmount;
    }

    /**
     * 锁定下单商品的所有库存
     */
    private void lockStock(List<CartPromotionItem> cartPromotionItemList) {
        for (CartPromotionItem cartPromotionItem : cartPromotionItemList) {
            PmsSkuStock skuStock = skuStockMapper.selectByPrimaryKey(cartPromotionItem.getProductSkuId());
            skuStock.setLockStock(skuStock.getLockStock() + cartPromotionItem.getQuantity());
            skuStockMapper.updateByPrimaryKeySelective(skuStock);
        }
    }

    /**
     * 判断下单商品是否都有库存
     */
    private boolean hasStock(List<CartPromotionItem> cartPromotionItemList) {
        for (CartPromotionItem cartPromotionItem : cartPromotionItemList) {
            if (cartPromotionItem.getRealStock()==null //判断真实库存是否为空
                    ||cartPromotionItem.getRealStock() <= 0 //判断真实库存是否小于0
                    || cartPromotionItem.getRealStock() < cartPromotionItem.getQuantity()) //判断真实库存是否小于下单的数量
            {
                return false;
            }
        }
        return true;
    }

    /**
     * 计算购物车中商品的价格
     */
    private ConfirmOrderResult.CalcAmount calcCartAmount(List<CartPromotionItem> cartPromotionItemList) {
        ConfirmOrderResult.CalcAmount calcAmount = new ConfirmOrderResult.CalcAmount();
        calcAmount.setFreightAmount(new BigDecimal(0));
        BigDecimal totalAmount = new BigDecimal("0");
        BigDecimal promotionAmount = new BigDecimal("0");
        for (CartPromotionItem cartPromotionItem : cartPromotionItemList) {
            totalAmount = totalAmount.add(cartPromotionItem.getPrice().multiply(new BigDecimal(cartPromotionItem.getQuantity())));
            promotionAmount = promotionAmount.add(cartPromotionItem.getReduceAmount().multiply(new BigDecimal(cartPromotionItem.getQuantity())));
        }
        calcAmount.setTotalAmount(totalAmount);
        calcAmount.setPromotionAmount(promotionAmount);
        calcAmount.setPayAmount(totalAmount.subtract(promotionAmount));
        return calcAmount;
    }

}
