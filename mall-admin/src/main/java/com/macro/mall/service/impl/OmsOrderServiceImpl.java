package com.macro.mall.service.impl;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.github.pagehelper.PageHelper;
import com.kuaidi100.sdk.response.QueryTrackData;
import com.kuaidi100.sdk.response.QueryTrackResp;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.enums.ConstantTypesEnum;
import com.macro.mall.common.util.Kuaidi100Util;
import com.macro.mall.dao.OmsOrderDao;
import com.macro.mall.dao.OmsOrderOperateHistoryDao;
import com.macro.mall.dto.*;
import com.macro.mall.mapper.*;
import com.macro.mall.model.*;
import com.macro.mall.service.OmsOrderService;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 订单管理Service实现类
 * fisher
 */
@Service
@Slf4j
public class OmsOrderServiceImpl implements OmsOrderService {
    @Autowired
    private OmsOrderMapper orderMapper;
    @Autowired
    private OmsOrderDao orderDao;
    @Autowired
    private OmsOrderOperateHistoryDao orderOperateHistoryDao;
    @Autowired
    private OmsOrderOperateHistoryMapper orderOperateHistoryMapper;
    @Autowired
    private UmsMemberMapper umsMemberMapper;
    @Autowired
    private OmsOrderItemMapper omsOrderItemMapper;

    @Autowired
    private UmsAdminMapper umsAdminMapper;

    @Autowired
    private OmsOrderStageMapper omsOrderStageMapper;

    @Autowired
    private Kuaidi100Util kuaidi100Util;

    @Autowired
    private PmsProductRentMapper pmsProductRentMapper;

    @Override
    public OmsOrderDetailInfoVo list(OmsOrderQueryParam queryParam) {
        log.info("【订单列表检索】【入参】：{}", JSONUtil.toJsonStr(queryParam));
        List<OmsOrderDetail> detailList = new ArrayList<>();
        UmsMemberExample example = new UmsMemberExample();
        try {
            //根据渠道id 以及成员id 获取用户列表
            UmsMemberExample.Criteria criteria = example.createCriteria();
            if (!Objects.isNull(queryParam.getAdminId())) {
                criteria.andAdminIdEqualTo(queryParam.getAdminId());
            }
            if (!Objects.isNull(queryParam.getChannelId())) {
                criteria.andChannelIdEqualTo(queryParam.getChannelId());
            }
            if (!Objects.isNull(queryParam.getAdminId()) || !Objects.isNull(queryParam.getChannelId())) {
                List<UmsMember> memberList = umsMemberMapper.selectByExample(example);
                List<Long> collect = memberList.stream().map(m -> m.getId()).distinct().collect(Collectors.toList());
                queryParam.setMemberIds(collect);
            }
            List<OmsOrder> list = orderDao.getList(queryParam);
            for (OmsOrder order : list) {
                OmsOrderDetail detail = JSONUtil.toBean(JSONUtil.toJsonStr(order), OmsOrderDetail.class);
                String orderSn = order.getOrderSn();
                Long id = order.getId();
                //获取商品列表信息
                OmsOrderItemExample orderItemExample = new OmsOrderItemExample();
                orderItemExample.createCriteria().andOrderIdEqualTo(id).andOrderSnEqualTo(orderSn);
                List<OmsOrderItem> omsOrderItems = omsOrderItemMapper.selectByExample(orderItemExample);
                detail.setOrderItemList(omsOrderItems);
                OmsOrderStageExample stageExample = new OmsOrderStageExample();
                stageExample.createCriteria().andOrderIdEqualTo(order.getId());
                List<OmsOrderStage> omsOrderStages = omsOrderStageMapper.selectByExample(stageExample);
                if(omsOrderStages.isEmpty()){
                    OmsOrderItem item = omsOrderItems.get(0);
                    Integer stageNum = item.getStageNum();
                    BigDecimal productPrice = item.getProductPrice();
                    BigDecimal periodPrice = productPrice.divide(new BigDecimal(stageNum), 2, BigDecimal.ROUND_HALF_DOWN);
                    //验证是否是灵活分期
                    Integer repaymentType = item.getRepaymentType();
                    if (1 == repaymentType) {
                        //获取对应账单值
                        PmsProductRentExample rent = new PmsProductRentExample();
                        rent.createCriteria().andProductIdEqualTo(item.getProductId()).andStagesEqualTo(1);
                        List<PmsProductRent> pmsProductRentList = pmsProductRentMapper.selectByExample(rent);
                        periodPrice = pmsProductRentList.get(0).getStagesPrice();
                    }
                    detail.setFirstPrice(periodPrice);
                    detail.setPayNum(0);
                    detail.setPayPrice(new BigDecimal(0));
                }else{
                    List<BigDecimal> collect = omsOrderStages.stream().filter(f -> f.getPayNo() == 1 && f.getBillType() == 0).map(m -> m.getPeriodPrice()).collect(Collectors.toList());
                    detail.setFirstPrice(collect.get(0));
                    long count = omsOrderStages.stream().filter(f -> f.getStatus() == 2 && f.getBillType() == 0).count();
                    detail.setPayNum(Integer.parseInt(count+""));
                    double sum = omsOrderStages.stream().filter(f -> f.getBillType() == 0).mapToDouble(m -> m.getPayPrice().doubleValue()).sum();
                    detail.setPayPrice(new BigDecimal(sum));
                }
                //获取操作记录
                OmsOrderOperateHistoryExample historyExample = new OmsOrderOperateHistoryExample();
                historyExample.createCriteria().andOrderIdEqualTo(id).andOrderStatusEqualTo(9);
                historyExample.setOrderByClause("create_time desc");
                List<OmsOrderOperateHistory> historyList = orderOperateHistoryMapper.selectByExample(historyExample);
                detail.setHistoryList(historyList);
                detailList.add(detail);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        queryParam.setStatus(null);
        List<OmsOrder> orderList = orderDao.getList(queryParam);
        //封装订单状态数量
        PageHelper.startPage(queryParam.getPageNum(), queryParam.getPageSize());
        CommonPage<OmsOrderDetail> omsOrderDetailCommonPage = CommonPage.restPage(detailList);
        OmsOrderDetailInfoVo vo = new OmsOrderDetailInfoVo();
        vo.setOmsOrderDetailCommonPage(omsOrderDetailCommonPage);
        //订单状态：0->待审核；1->待支付；2->代发货；3->待收货；4->租用中；5->逾期；6->订单完成；7->订单关闭
        //全部订单数量
        vo.setAllNum(orderList.size());
        //待审核订单
        int processNum = orderList.stream().filter(f -> f.getStatus() == 0).collect(Collectors.toList()).size();
        vo.setProcessNum(processNum);
        //待支付
        int payOffNum = orderList.stream().filter(f -> f.getStatus() == 1).collect(Collectors.toList()).size();
        vo.setPayOffNum(payOffNum);
        int consignmentNum = orderList.stream().filter(f -> f.getStatus() == 2).collect(Collectors.toList()).size();
        vo.setConsignmentNum(consignmentNum);
        int receivedNum = orderList.stream().filter(f -> f.getStatus() == 3).collect(Collectors.toList()).size();
        vo.setReceivedNum(receivedNum);
        int usedNum = orderList.stream().filter(f -> f.getStatus() == 4).collect(Collectors.toList()).size();
        vo.setUsedNum(usedNum);
        int completeNum = orderList.stream().filter(f -> f.getStatus() == 6).collect(Collectors.toList()).size();
        vo.setCompleteNum(completeNum);
        int closeNum = orderList.stream().filter(f -> f.getStatus() == 7).collect(Collectors.toList()).size();
        vo.setCloseNum(closeNum);
        return vo;
    }

    @Override
    public int delivery(List<OmsOrderDeliveryParam> deliveryParamList) {
        List<OmsOrderDeliveryParam> successList = new ArrayList<>();
        for (OmsOrderDeliveryParam deliveryParam : deliveryParamList) {
            Long orderId = deliveryParam.getOrderId();
            OmsOrder order = orderMapper.selectByPrimaryKey(orderId);
            String receiverPhone = order.getReceiverPhone();
            try {
                List<QueryTrackData> queryTrackData = kuaidi100Util.queryTrace(deliveryParam.getDeliveryCompany(), deliveryParam.getDeliverySn(), receiverPhone);
                if (queryTrackData != null) {
                    successList.add(deliveryParam);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //批量发货
        int count = orderDao.delivery(successList);
        //添加操作记录
        List<OmsOrderOperateHistory> operateHistoryList = successList.stream()
                .map(omsOrderDeliveryParam -> {
                    OmsOrderOperateHistory history = new OmsOrderOperateHistory();
                    history.setOrderId(omsOrderDeliveryParam.getOrderId());
                    history.setCreateTime(new Date());
                    Long operateId = omsOrderDeliveryParam.getOperateId();
                    UmsAdmin umsAdmin = umsAdminMapper.selectByPrimaryKey(operateId);
                    history.setOperateMan(umsAdmin.getUsername());
                    history.setOperateId(omsOrderDeliveryParam.getOperateId());
                    history.setOrderStatus(2);
                    history.setNote("完成发货");
                    return history;
                }).collect(Collectors.toList());
        orderOperateHistoryDao.insertList(operateHistoryList);
        return count;
    }

    @Override
    public int close(List<Long> ids, String note, Long operateId, Integer status) {
        OmsOrder record = new OmsOrder();
        record.setStatus(status);
        OmsOrderExample example = new OmsOrderExample();
        example.createCriteria().andDeleteStatusEqualTo(0).andIdIn(ids);
        int count = orderMapper.updateByExampleSelective(record, example);
        List<OmsOrderOperateHistory> historyList = ids.stream().map(orderId -> {
            OmsOrderOperateHistory history = new OmsOrderOperateHistory();
            history.setOrderId(orderId);
            history.setCreateTime(new Date());
            UmsAdmin umsAdmin = umsAdminMapper.selectByPrimaryKey(operateId);
            history.setOperateId(operateId);
            history.setOperateMan(umsAdmin.getUsername());
            history.setOrderStatus(status);
            history.setNote("订单关闭:" + note);
            return history;
        }).collect(Collectors.toList());
        orderOperateHistoryDao.insertList(historyList);
        return count;
    }

    @Override
    public int delete(List<Long> ids) {
        OmsOrder record = new OmsOrder();
        record.setDeleteStatus(1);
        OmsOrderExample example = new OmsOrderExample();
        example.createCriteria().andDeleteStatusEqualTo(0).andIdIn(ids);
        return orderMapper.updateByExampleSelective(record, example);
    }

    @Override
    public OmsOrderDetail detail(Long id) {
        return orderDao.getDetail(id);
    }

    @Override
    public int updateReceiverInfo(OmsReceiverInfoParam receiverInfoParam) {
        OmsOrder order = new OmsOrder();
        order.setId(receiverInfoParam.getOrderId());
        order.setReceiverName(receiverInfoParam.getReceiverName());
        order.setReceiverPhone(receiverInfoParam.getReceiverPhone());
        order.setReceiverPostCode(receiverInfoParam.getReceiverPostCode());
        order.setReceiverDetailAddress(receiverInfoParam.getReceiverDetailAddress());
        order.setReceiverProvince(receiverInfoParam.getReceiverProvince());
        order.setReceiverCity(receiverInfoParam.getReceiverCity());
        order.setReceiverRegion(receiverInfoParam.getReceiverRegion());
        order.setModifyTime(new Date());
        int count = orderMapper.updateByPrimaryKeySelective(order);
        //插入操作记录
        OmsOrderOperateHistory history = new OmsOrderOperateHistory();
        history.setOrderId(receiverInfoParam.getOrderId());
        history.setCreateTime(new Date());
        Long operateId = receiverInfoParam.getOperateId();
        UmsAdmin umsAdmin = umsAdminMapper.selectByPrimaryKey(operateId);
        history.setOperateMan(umsAdmin.getUsername());
        history.setOperateId(operateId);
        history.setOrderStatus(receiverInfoParam.getStatus());
        history.setNote("修改收货人信息");
        orderOperateHistoryMapper.insert(history);
        return count;
    }

    @Override
    public int updateMoneyInfo(OmsMoneyInfoParam moneyInfoParam) {
        OmsOrder order = new OmsOrder();
        order.setId(moneyInfoParam.getOrderId());
        order.setFreightAmount(moneyInfoParam.getFreightAmount());
        order.setDiscountAmount(moneyInfoParam.getDiscountAmount());
        order.setModifyTime(new Date());
        int count = orderMapper.updateByPrimaryKeySelective(order);
        //插入操作记录
        OmsOrderOperateHistory history = new OmsOrderOperateHistory();
        history.setOrderId(moneyInfoParam.getOrderId());
        history.setCreateTime(new Date());
        history.setOperateMan("后台管理员");
        history.setOrderStatus(moneyInfoParam.getStatus());
        history.setNote("修改费用信息");
        orderOperateHistoryMapper.insert(history);
        return count;
    }

    @Override
    public int updateNote(Long id, String note, Integer status, Long adminId) {
        OmsOrder order = new OmsOrder();
        order.setId(id);
        order.setNote(note);
        order.setModifyTime(new Date());
        int count = orderMapper.updateByPrimaryKeySelective(order);
        OmsOrderOperateHistory history = new OmsOrderOperateHistory();
        history.setOrderId(id);
        history.setCreateTime(new Date());
        UmsAdmin umsAdmin = umsAdminMapper.selectByPrimaryKey(adminId);
        history.setOperateMan(umsAdmin.getUsername());
        history.setOperateId(adminId);
        history.setOrderStatus(status);
        history.setNote(note);
        orderOperateHistoryMapper.insert(history);
        return count;
    }

    @Override
    public CommonPage<OmsDueOrOverOrderDetail> dueList(OmsOrderQueryParam queryParam) {
        log.info("【到期订单列表】【入参】：{}", JSONUtil.toJsonStr(queryParam));
        List<OmsDueOrOverOrderDetail> result = new ArrayList<>();
        try {
            OmsOrderStageExample omsOrderStageExample = new OmsOrderStageExample();
            OmsOrderStageExample.Criteria criteria1 = omsOrderStageExample.createCriteria();
            if (!StrUtil.isEmpty(queryParam.getExpStartTime()) && !StrUtil.isEmpty(queryParam.getExpEndTime())) {
                Date expStartTime = DateUtil.parse(queryParam.getExpStartTime());
                Date expendTime = DateUtil.parse(queryParam.getExpEndTime());
                criteria1.andRepaymentTimeGreaterThanOrEqualTo(expStartTime).andRepaymentTimeLessThanOrEqualTo(expendTime);
            }
            if (!StrUtil.isEmpty(queryParam.getReturnSTime()) && !StrUtil.isEmpty(queryParam.getReturnETime())) {
                Date returnSTime = DateUtil.parse(queryParam.getReturnSTime());
                Date returnETime = DateUtil.parse(queryParam.getReturnETime());
                criteria1.andPayTimeGreaterThanOrEqualTo(returnSTime).andPayTimeLessThanOrEqualTo(returnETime);
            }
            if (!Objects.isNull(queryParam.getStageStatus())) {
                criteria1.andStatusEqualTo(queryParam.getStageStatus());
            }
            List<OmsOrderStage> omsOrderStages = omsOrderStageMapper.selectByExample(omsOrderStageExample);
            List<Long> collect2 = omsOrderStages.stream().map(m -> m.getOrderId()).distinct().collect(Collectors.toList());
            if (!collect2.isEmpty()) {
                //根据渠道id 以及成员id 获取用户列表
                queryParam.setOrderIds(collect2);
                UmsMemberExample umsMemberExample = new UmsMemberExample();
                UmsMemberExample.Criteria or = umsMemberExample.or();
                if (!Objects.isNull(queryParam.getAdminId())) {
                    or.andAdminIdEqualTo(queryParam.getAdminId());
                }
                if (!Objects.isNull(queryParam.getChannelId())) {
                    or.andChannelIdEqualTo(queryParam.getChannelId());
                }
                if (!Objects.isNull(queryParam.getAdminId()) || !Objects.isNull(queryParam.getChannelId())) {
                    List<UmsMember> memberList = umsMemberMapper.selectByExample(umsMemberExample);
                    List<Long> collect = memberList.stream().map(m -> m.getId()).distinct().collect(Collectors.toList());
                    queryParam.setMemberIds(collect);
                }
                List<OmsOrder> list = orderDao.getList(queryParam);
                if (!list.isEmpty()) {
                    //验证是否过滤操作员
                    Long operateId = queryParam.getOperateId();
                    String operateMan = "";
                    if (operateId != null) {
                        UmsMember member = umsMemberMapper.selectByPrimaryKey(operateId);
                        operateMan = member.getUsername();
                    }
                    OmsOrderItemExample omsOrderItemExample = new OmsOrderItemExample();
                    omsOrderItemExample.createCriteria().andOrderIdIn(collect2);
                    List<OmsOrderItem> omsOrderItems = omsOrderItemMapper.selectByExample(omsOrderItemExample);
                    for (OmsOrder order : list) {
                        Long id = order.getId();
                        OmsDueOrOverOrderDetail detail = JSONUtil.toBean(JSONUtil.toJsonStr(order), OmsDueOrOverOrderDetail.class);
                        //获取商品过滤
                        List<OmsOrderItem> collect3 = omsOrderItems.stream().filter(f -> f.getOrderId() == id).collect(Collectors.toList());
                        detail.setProductSn(collect3.get(0).getProductSn());
                        detail.setProductName(collect3.get(0).getProductName());
                        detail.setProductPic(collect3.get(0).getProductPic());
                        detail.setStageNum(collect3.get(0).getStageNum());
                        //审核人
                        if (operateId != null) {
                            detail.setOperateMan(operateMan);
                        } else {
                            UmsMember member = umsMemberMapper.selectByPrimaryKey(order.getOperateId());
                            operateMan = member.getUsername();
                            detail.setOperateMan(operateMan);
                        }
                        //支付情况
                        OmsOrderStageExample orderStageExample = new OmsOrderStageExample();
                        OmsOrderStageExample.Criteria stageExampleCriteria = orderStageExample.createCriteria();
                        stageExampleCriteria.andOrderIdEqualTo(id).andStatusEqualTo(2);
                        List<OmsOrderStage> omsOrderStages1 = omsOrderStageMapper.selectByExample(orderStageExample);
                        detail.setPayNum(omsOrderStages1.size());
                        List<OmsOrderStage> collect = omsOrderStages.stream().filter(f -> f.getOrderId() == id).sorted(Comparator.comparing(OmsOrderStage::getPayNo)).collect(Collectors.toList());
                        detail.setRepaymentTime(DateUtil.format(collect.get(0).getRepaymentTime(), "yyyy-MM-dd"));
                        detail.setCurrentPrice(collect.get(0).getPeriodPrice().add(collect.get(0).getOverPrice()));
                        if (collect.get(0).getPayPrice() != null) {
                            detail.setPayPrice(collect.get(0).getPayPrice());
                        }
                        detail.setPayStatus(collect.get(0).getStatus());
                        if (collect.get(0).getPayTime() != null) {
                            detail.setPayTime(DateUtil.format(collect.get(0).getPayTime(), "yyyy-MM-dd"));
                        }
                        // TODO: 2023/8/27  获取最新的催收才做记录
                        result.add(detail);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error("【到期订单列表失败】【入参】：{}", JSONUtil.toJsonStr(queryParam));
        }
        PageHelper.startPage(queryParam.getPageNum(), queryParam.getPageSize());
        return CommonPage.restPage(result);
    }

    @Override
    public CommonPage<OmsDueOrOverOrderDetail> overList(OmsOrderQueryParam queryParam) {
        log.info("【逾期订单列表】【入参】：{}", JSONUtil.toJsonStr(queryParam));
        List<OmsDueOrOverOrderDetail> result = new ArrayList<>();
        try {
            //获取
            OmsOrderStageExample omsOrderStageExample = new OmsOrderStageExample();
            OmsOrderStageExample.Criteria criteria1 = omsOrderStageExample.createCriteria();
            criteria1.andIsOverEqualTo(1);
            if (!StrUtil.isEmpty(queryParam.getReturnSTime()) && !StrUtil.isEmpty(queryParam.getReturnETime())) {
                Date returnSTime = DateUtil.parse(queryParam.getReturnSTime());
                Date returnETime = DateUtil.parse(queryParam.getReturnETime());
                criteria1.andPayTimeGreaterThanOrEqualTo(returnSTime).andPayTimeLessThanOrEqualTo(returnETime);
            }
            List<OmsOrderStage> omsOrderStages = omsOrderStageMapper.selectByExample(omsOrderStageExample);
            List<Long> orderIds = omsOrderStages.stream().map(m -> m.getOrderId()).distinct().collect(Collectors.toList());
            if (!Objects.isNull(queryParam.getOverNum())) {
                Integer overNum = queryParam.getOverNum();
                Map<Long, List<OmsOrderStage>> collect = omsOrderStages.stream().collect(Collectors.groupingBy(OmsOrderStage::getOrderId));
                for (Map.Entry<Long, List<OmsOrderStage>> entry : collect.entrySet()) {
                    List<OmsOrderStage> value = entry.getValue();
                    Long key = entry.getKey();
                    if (overNum != value.size()) {
                        orderIds.remove(key);
                    }
                }
            }
            if (!Objects.isNull(queryParam.getOverDays())) {
                Integer overDays = queryParam.getOverDays();
                Map<Long, List<OmsOrderStage>> collect = omsOrderStages.stream().collect(Collectors.groupingBy(OmsOrderStage::getOrderId));
                for (Map.Entry<Long, List<OmsOrderStage>> entry : collect.entrySet()) {
                    List<OmsOrderStage> value = entry.getValue();
                    Long key = entry.getKey();
                    //延期天数
                    value = value.stream().sorted(Comparator.comparing(OmsOrderStage::getPayNo)).collect(Collectors.toList());
                    Integer maxDay = 0;
                    for (OmsOrderStage stage : value) {
                        Integer status = stage.getStatus();
                        //验证是否还在逾期中
                        Date repaymentTime = stage.getRepaymentTime();
                        if (status == 3) {
                            int diffDays = (int) DateUtil.between(repaymentTime, new Date(), DateUnit.DAY);
                            maxDay = maxDay > diffDays ? maxDay : diffDays;
                        } else {
                            Date payTime = stage.getPayTime();
                            int diffDays = (int) DateUtil.between(repaymentTime, payTime, DateUnit.DAY);
                            maxDay = maxDay > diffDays ? maxDay : diffDays;
                        }
                    }
                    if (maxDay != overDays) {
                        orderIds.remove(key);
                    }
                }
            }
            if (!orderIds.isEmpty()) {
                //根据渠道id 以及成员id 获取用户列表
                queryParam.setOrderIds(orderIds);
                UmsMemberExample umsMemberExample = new UmsMemberExample();
                UmsMemberExample.Criteria or = umsMemberExample.or();
                if (!Objects.isNull(queryParam.getAdminId())) {
                    or.andAdminIdEqualTo(queryParam.getAdminId());
                }
                if (!Objects.isNull(queryParam.getChannelId())) {
                    or.andChannelIdEqualTo(queryParam.getChannelId());
                }
                if (!Objects.isNull(queryParam.getAdminId()) || !Objects.isNull(queryParam.getChannelId())) {
                    List<UmsMember> memberList = umsMemberMapper.selectByExample(umsMemberExample);
                    List<Long> collect = memberList.stream().map(m -> m.getId()).distinct().collect(Collectors.toList());
                    queryParam.setMemberIds(collect);
                }
                List<OmsOrder> list = orderDao.getList(queryParam);
                if (!list.isEmpty()) {
                    List<Long> collect = list.stream().map(m -> m.getId()).distinct().collect(Collectors.toList());
                    //验证是否过滤操作员
                    Long operateId = queryParam.getOperateId();
                    String operateMan = "";
                    if (operateId != null) {
                        UmsMember member = umsMemberMapper.selectByPrimaryKey(operateId);
                        operateMan = member.getUsername();
                    }
                    OmsOrderItemExample omsOrderItemExample = new OmsOrderItemExample();
                    omsOrderItemExample.createCriteria().andOrderIdIn(collect);
                    List<OmsOrderItem> omsOrderItems = omsOrderItemMapper.selectByExample(omsOrderItemExample);
                    for (OmsOrder order : list) {
                        Long id = order.getId();
                        OmsDueOrOverOrderDetail detail = JSONUtil.toBean(JSONUtil.toJsonStr(order), OmsDueOrOverOrderDetail.class);
                        //获取商品过滤
                        List<OmsOrderItem> collect3 = omsOrderItems.stream().filter(f -> f.getOrderId() == id).collect(Collectors.toList());
                        detail.setProductSn(collect3.get(0).getProductSn());
                        detail.setProductName(collect3.get(0).getProductName());
                        detail.setProductPic(collect3.get(0).getProductPic());
                        detail.setStageNum(collect3.get(0).getStageNum());
                        //审核人
                        if (operateId != null) {
                            detail.setOperateMan(operateMan);
                        } else {
                            UmsMember member = umsMemberMapper.selectByPrimaryKey(order.getOperateId());
                            operateMan = member.getUsername();
                            detail.setOperateMan(operateMan);
                        }
                        //支付情况
                        OmsOrderStageExample orderStageExample = new OmsOrderStageExample();
                        OmsOrderStageExample.Criteria stageExampleCriteria = orderStageExample.createCriteria();
                        stageExampleCriteria.andOrderIdEqualTo(id);
                        List<OmsOrderStage> omsOrderStages1 = omsOrderStageMapper.selectByExample(orderStageExample);
                        List<OmsOrderStage> payList = omsOrderStages1.stream().filter(f -> f.getStatus() == 2).collect(Collectors.toList());
                        detail.setPayNum(payList.size());
                        //剩余未还总金额，包括租金，违约金
                        double rentPrice = omsOrderStages1.stream().filter(f -> f.getStatus() != 2).mapToDouble(m -> m.getPeriodPrice().doubleValue()).sum();
                        double overPrice = omsOrderStages1.stream().filter(f -> f.getStatus() != 2).mapToDouble(m -> m.getOverPrice().doubleValue()).sum();
                        BigDecimal reducePrice = new BigDecimal(rentPrice).add(new BigDecimal(overPrice));
                        detail.setReducePrice(reducePrice);
                        detail.setRentPrice(new BigDecimal(rentPrice));
                        detail.setOverPrice(new BigDecimal(overPrice));
                        //逾期次数
                        List<OmsOrderStage> overList = omsOrderStages1.stream().filter(f -> f.getIsOver() == 1).collect(Collectors.toList());
                        detail.setOverNum(overList.size());
                        //最长逾期天数
                        if (!Objects.isNull(queryParam.getOverDays())) {
                            detail.setOverDays(queryParam.getOverDays());
                        } else {
                            Integer maxDay = 0;
                            for (OmsOrderStage stage : overList) {
                                Integer status = stage.getStatus();
                                //验证是否还在逾期中
                                Date repaymentTime = stage.getRepaymentTime();
                                if (status == 3) {
                                    int diffDays = (int) DateUtil.between(repaymentTime, new Date(), DateUnit.DAY);
                                    maxDay = maxDay > diffDays ? maxDay : diffDays;
                                } else {
                                    Date payTime = stage.getPayTime();
                                    int diffDays = (int) DateUtil.between(repaymentTime, payTime, DateUnit.DAY);
                                    maxDay = maxDay > diffDays ? maxDay : diffDays;
                                }
                            }
                            detail.setOverDays(maxDay);
                        }
                        //累计还款
                        double repaymentAllPrice = omsOrderStages1.stream().mapToDouble(m -> m.getPayPrice().doubleValue()).sum();
                        detail.setRepaymentAllPrice(new BigDecimal(repaymentAllPrice));
                        //最近还款时间
                        List<OmsOrderStage> collect1 = omsOrderStages1.stream().filter(f -> f.getPayTime() != null).sorted(Comparator.comparing(OmsOrderStage::getPayTime).reversed()).collect(Collectors.toList());
                        detail.setLastReturnTime(DateUtil.format(collect1.get(0).getPayTime(), "yyyy-MM-dd"));
                        // TODO: 2023/8/27  获取最新的催收才做记录
                        result.add(detail);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("【逾期订单列表失败】【入参】：{}", JSONUtil.toJsonStr(queryParam));
        }
        PageHelper.startPage(queryParam.getPageNum(), queryParam.getPageSize());
        return CommonPage.restPage(result);
    }

    @Override
    public CommonResult<OmsOrderHeadDataVo> headData() {
        OmsOrderHeadDataVo vo = new OmsOrderHeadDataVo();
        try{
            //获取历史总订单，排除审核拒绝订单，待审核，待支付
            OmsOrderExample example = new OmsOrderExample();
            example.or().andStatusNotEqualTo(ConstantTypesEnum.OrderStatus.REFUSE.getValue()).andStatusNotEqualTo(ConstantTypesEnum.OrderStatus.PROCESS.getValue()).andStatusNotEqualTo(ConstantTypesEnum.OrderStatus.PAYOFF.getValue());
            List<OmsOrder> omsOrders = orderMapper.selectByExample(example);
            //订单总金额，已关闭订单需要排除未归还金额
            List<Long> collect = omsOrders.stream().filter(f -> f.getStatus() == ConstantTypesEnum.OrderStatus.CLOSE.getValue()).map(m -> m.getId()).distinct().collect(Collectors.toList());
            List<Long> orderIds = omsOrders.stream().map(m -> m.getId()).distinct().collect(Collectors.toList());
            //获取总的账单，订单数可能较多，需要切割
            OmsOrderStageExample stageExample = new OmsOrderStageExample();
            stageExample.createCriteria().andOrderIdIn(orderIds);
            List<OmsOrderStage> omsOrderStages = omsOrderStageMapper.selectByExample(stageExample);
            //计算已回收款
            double received = omsOrderStages.stream().mapToDouble(m -> m.getPayPrice().doubleValue()).sum();
            vo.setReceived(new BigDecimal(received));
            //计算增值服务款(确定是否是已归还的)
            double addedPrice = omsOrderStages.stream().filter(f -> f.getBillType() == 2).mapToDouble(m -> m.getPeriodPrice().doubleValue()).sum();
            vo.setAddedPrice(new BigDecimal(addedPrice));
            List<OmsOrderStage> collect1 = omsOrderStages.stream().filter(f -> collect.contains(f.getOrderId())).collect(Collectors.toList());//关闭订单
            //总金额
            double sum = collect1.stream().mapToDouble(m -> m.getPayPrice().doubleValue()).sum();
            List<OmsOrderStage> collect2 = omsOrderStages.stream().filter(f -> !collect.contains(f.getOrderId())).collect(Collectors.toList());//排除关闭订单
            double sum1 = collect2.stream().mapToDouble(m -> m.getPeriodPrice().add(m.getOverPrice()).doubleValue()).sum();
            vo.setTotalAmount(new BigDecimal(sum).add(new BigDecimal(sum1)));
            //待回款 总金额-已回款
            BigDecimal pending = vo.getTotalAmount().subtract(vo.getReceived());
            vo.setPending(pending);
            //历史总订单
            vo.setHistoryNum(omsOrders.size());
            //今日新增订单
            example = new OmsOrderExample();
            //当日凌晨时间
            String s = DateUtil.beginOfDay(DateUtil.date()).toString();
            example.createCriteria().andCreateTimeGreaterThanOrEqualTo(DateUtil.parse(s, "yyyy-MM-dd"));
            long todayNum = orderMapper.countByExample(example);
            vo.setTodayNum(Integer.parseInt(todayNum+""));
            //本月新增
            Date beginOfMonth = DateUtil.beginOfMonth(new Date());
            example = new OmsOrderExample();
            example.createCriteria().andCreateTimeGreaterThanOrEqualTo(beginOfMonth);
            long monthNum = orderMapper.countByExample(example);
            vo.setMonthNum(Integer.parseInt(monthNum+""));
            //今日到期账单
            String today = DateUtil.format(new Date(),"yyyy-MM-dd");
            List<OmsOrderStage> collect3 = omsOrderStages.stream().filter(f -> today.equals(DateUtil.format(f.getRepaymentTime(), "yyyy-MM-dd"))).collect(Collectors.toList());
            vo.setTodayDueNum(collect3.size());
            double todayDuePrice = collect3.stream().mapToDouble(m -> m.getPeriodPrice().doubleValue()).sum();
            vo.setTodayDuePrice(new BigDecimal(todayDuePrice));
            //已回收
            List<OmsOrderStage> collect4 = collect3.stream().filter(f -> f.getStatus() == 2).collect(Collectors.toList());
            vo.setRecoveryNum(collect4.size());
            double recoveryPrice = collect4.stream().mapToDouble(m -> m.getPayPrice().doubleValue()).sum();
            vo.setRecoveryPrice(new BigDecimal(recoveryPrice));
            //未回收
            List<OmsOrderStage> collect5 = collect3.stream().filter(f -> f.getStatus() == 1).collect(Collectors.toList());
            double unrecycledPrice = collect5.stream().mapToDouble(m -> m.getPeriodPrice().doubleValue()).sum();
            vo.setUnrecycledPrice(new BigDecimal(unrecycledPrice));
            vo.setUnrecycledNum(collect5.size());
            //逾期未归还
            List<OmsOrderStage> collect6 = omsOrderStages.stream().filter(f -> f.getStatus() == 3 && f.getBillType()==0).collect(Collectors.toList());
            vo.setOverUnrecycledNum(collect6.size());
            long count = omsOrderStages.stream().filter(f -> f.getBillType() == 0).count();
            BigDecimal badRate = new BigDecimal(collect6.size()).divide(new BigDecimal(count)).multiply(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_HALF_UP);
            vo.setBadRate(badRate);
            //逾期金额
            double overPrice = collect6.stream().mapToDouble(m -> m.getPeriodPrice().add(m.getOverPrice()).subtract(m.getPayPrice()).doubleValue()).sum();
            vo.setOverPrice(new BigDecimal(overPrice));
        }catch (Exception e){
            e.printStackTrace();
        }
        return CommonResult.success(vo);
    }

    @Override
    public CommonResult<List<OmsOrderStatus>> orderStatus() {
        List<OmsOrderStatus> omsOrderStatuses = new ArrayList<>();
        try {
            omsOrderStatuses = orderDao.selectGroupByStatus();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CommonResult.success(omsOrderStatuses);
    }

    @Override
    public CommonResult<OrderLineData> lineData(Map<String, String> map) {
        OrderLineData data = new OrderLineData();
        String startTime = map.get("startTime");
        String endTime = map.get("endTime");
        List<Map> line = orderDao.getLine(startTime, endTime);
        if(!line.isEmpty()){
            List<String> dayListOfLongDate = getDayListOfLongDate(startTime, endTime);
            List<String> time = new ArrayList<>();
            List<Integer> yList = new ArrayList<>();
            for(String date : dayListOfLongDate){
                time.add(date);
                List<Integer> collect = line.stream().filter(f -> f.get("time").equals(date)).map(m -> Integer.parseInt(m.get("num")+"")).collect(Collectors.toList());
                if(collect.isEmpty()){
                    yList.add(0);
                }else{
                    Integer o = collect.get(0);
                    yList.add(o);
                }
            }
            int num = line.stream().mapToInt(m -> Integer.parseInt(m.get("num")+"")).sum();
            data.setTotal(num);
//            List<String> time = line.stream().map(m -> m.get("time").toString()).distinct().collect(Collectors.toList());
            data.setXLine(time);
//            List<Integer> num1 = line.stream().map(m -> Integer.parseInt(m.get("num")+"")).distinct().collect(Collectors.toList());
            data.setYLine(yList);
        }
        return CommonResult.success(data);
    }

    public static List<String> getDayListOfLongDate(String beginDateStr, String endDateStr) {
        // 指定要解析的时间格式
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");

        // 定义一些变量
        Date beginDate = null;
        Date endDate = null;

        Calendar beginGC = null;
        Calendar endGC = null;
        List<String> list = new ArrayList<String>();

        try {
            // 将字符串parse成日期
            beginDate = f.parse(beginDateStr);
            endDate = f.parse(endDateStr);

            // 设置日历
            beginGC = Calendar.getInstance();
            beginGC.setTime(beginDate);

            endGC = Calendar.getInstance();
            endGC.setTime(endDate);
            // 直到两个时间相同
            while (beginGC.getTime().compareTo(endGC.getTime()) <= 0) {
                String format = f.format(beginGC.getTime());
                list.add(format);
                // 以日为单位，增加时间
                beginGC.add(Calendar.DAY_OF_MONTH, 1);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public CommonResult<UmsMemberData> userInfo() {
        UmsMemberData data = new UmsMemberData();
        try {
            //获取全部用户数
            UmsMemberExample example = new UmsMemberExample();
            List<UmsMember> memberList = umsMemberMapper.selectByExample(example);
            data.setTotalMemberNum(memberList.size());
            //订单申请用户数
            List<Long> collect = memberList.stream().map(m -> m.getId()).distinct().collect(Collectors.toList());
            //获取订单
            OmsOrderExample omsOrderExample = new OmsOrderExample();
            omsOrderExample.createCriteria().andMemberIdIn(collect);
            List<OmsOrder> omsOrders = orderMapper.selectByExample(omsOrderExample);
            List<Long> collect1 = omsOrders.stream().map(m -> m.getMemberId()).distinct().collect(Collectors.toList());
            data.setApplyMemberNum(collect1.size());
            //获取下过单的用户，就是审核通过用户
            OmsOrderOperateHistoryExample historyExample = new OmsOrderOperateHistoryExample();
            historyExample.createCriteria().andOrderStatusEqualTo(ConstantTypesEnum.OrderStatus.PASS.getValue());
            List<OmsOrderOperateHistory> omsOrderOperateHistories = orderOperateHistoryMapper.selectByExample(historyExample);
            List<Long> collect2 = omsOrderOperateHistories.stream().map(m -> m.getOrderId()).distinct().collect(Collectors.toList());
            List<Long> collect3 = omsOrders.stream().filter(f -> collect2.contains(f.getId())).map(m -> m.getMemberId()).distinct().collect(Collectors.toList());
            data.setOrderRate(new BigDecimal(collect3.size()*100).divide(new BigDecimal(memberList.size()),2,BigDecimal.ROUND_HALF_UP));
            //放审用户数
            data.setPassNum(omsOrderOperateHistories.size());
            historyExample = new OmsOrderOperateHistoryExample();
            historyExample.createCriteria().andOrderStatusEqualTo(ConstantTypesEnum.OrderStatus.REFUSE.getValue());
            List<OmsOrderOperateHistory> historyList = orderOperateHistoryMapper.selectByExample(historyExample);
            //拒绝
            data.setRefuseNum(historyList.size());
            data.setPassRate(new BigDecimal(data.getPassNum()*100).divide(new BigDecimal(collect.size()),2,BigDecimal.ROUND_HALF_UP));
            data.setRefuseRate(new BigDecimal(data.getRefuseNum()*100).divide(new BigDecimal(collect.size()),2,BigDecimal.ROUND_HALF_UP));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CommonResult.success(data);
    }
}
