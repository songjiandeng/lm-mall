package com.macro.mall.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.github.pagehelper.PageHelper;
import com.macro.mall.dto.UmsMemberDetail;
import com.macro.mall.dto.UmsMemberOrder;
import com.macro.mall.dto.UmsMemberParamDto;
import com.macro.mall.mapper.*;
import com.macro.mall.model.*;
import com.macro.mall.service.UmsMemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class UmsMemberServiceImpl implements UmsMemberService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UmsMemberServiceImpl.class);

    @Autowired
    private UmsMemberMapper umsMemberMapper;

    @Autowired
    private UmsAdminMapper umsAdminMapper;

    @Autowired
    private OmsOrderMapper orderMapper;

    @Autowired
    private OmsOrderItemMapper itemMapper;

    @Autowired
    private OmsOrderStageMapper stageMapper;

    @Override
    public List<UmsMember> list(UmsMemberParamDto umsMemberParamDto) {
        LOGGER.info("【用戶列表获取（渠道，普通）】【入参】：{}", JSONUtil.toJsonStr(umsMemberParamDto));
        List<UmsMember> memberList = new ArrayList<>();
        try{
            UmsMemberExample example = new UmsMemberExample();
            UmsMemberExample.Criteria criteria = example.createCriteria();
            if(!StrUtil.isEmpty(umsMemberParamDto.getPhone())){
                criteria.andPhoneEqualTo(umsMemberParamDto.getPhone());
            }
            if(!Objects.isNull(umsMemberParamDto.getIsReal())){
                criteria.andIsRealEqualTo(umsMemberParamDto.getIsReal());
            }
            if(!Objects.isNull(umsMemberParamDto.getAdminId())){
                criteria.andAdminIdEqualTo(umsMemberParamDto.getAdminId());
            }
            if(!Objects.isNull(umsMemberParamDto.getOrderNum())){
                criteria.andOrderNumEqualTo(umsMemberParamDto.getOrderNum());
            }
            if(!Objects.isNull(umsMemberParamDto.getSourceType())){
                criteria.andSourceTypeEqualTo(umsMemberParamDto.getSourceType());
            }
            if(!Objects.isNull(umsMemberParamDto.getChannelId())){
                criteria.andChannelIdEqualTo(umsMemberParamDto.getChannelId());
            }
            if(!StrUtil.isEmpty(umsMemberParamDto.getStartTime()) && !StrUtil.isEmpty(umsMemberParamDto.getEndTime())){
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                criteria.andCreateTimeGreaterThanOrEqualTo(DateUtil.parse(umsMemberParamDto.getStartTime(),sdf));
                criteria.andCreateTimeLessThanOrEqualTo(DateUtil.parse(umsMemberParamDto.getEndTime(),sdf));
            }
            PageHelper.startPage(umsMemberParamDto.getPageNum(), umsMemberParamDto.getPageSize());
            memberList = umsMemberMapper.selectByExample(example);
        }catch (Exception e){
            e.printStackTrace();
        }
        return memberList;
    }

    @Override
    public int batchUpdateMark(UmsMemberParamDto umsMemberParamDto) {
        LOGGER.info("【批量更新用户备注】【入参】：{}",JSONUtil.toJsonStr(umsMemberParamDto));
        int i = 0;
        try{
            List<Long> memberList = umsMemberParamDto.getIds();
            if(!memberList.isEmpty()){
                String remark = umsMemberParamDto.getRemark();
                UmsMember member = new UmsMember();
                member.setRemark(remark);
                UmsMemberExample example = new UmsMemberExample();
                example.createCriteria().andIdIn(memberList);
                i = umsMemberMapper.updateByExampleSelective(member, example);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return i;
    }

    @Override
    public int batchUpdateAdmin(UmsMemberParamDto umsMemberParamDto) {
        LOGGER.info("【批量更新用户归属】【入参】：{}",JSONUtil.toJsonStr(umsMemberParamDto));
        int i = 0;
        try{
            List<Long> memberList = umsMemberParamDto.getIds();
            if(!memberList.isEmpty()){
                Long adminId = umsMemberParamDto.getAdminId();
                UmsMember member = new UmsMember();
                member.setAdminId(adminId);
                UmsAdmin umsAdmin = umsAdminMapper.selectByPrimaryKey(adminId);
                member.setAdminName(umsAdmin.getUsername());
                UmsMemberExample example = new UmsMemberExample();
                example.createCriteria().andIdIn(memberList);
                i = umsMemberMapper.updateByExampleSelective(member, example);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return i;
    }

    @Override
    public int batchUpdateRole(UmsMemberParamDto umsMemberParamDto) {
        LOGGER.info("【批量更新用户渠道】【入参】：{}",JSONUtil.toJsonStr(umsMemberParamDto));
        int i = 0;
        try{
            List<Long> memberList = umsMemberParamDto.getIds();
            if(!memberList.isEmpty()){
                Long channelId = umsMemberParamDto.getChannelId();
                UmsMember member = new UmsMember();
                member.setChannelId(channelId);
                UmsMemberExample example = new UmsMemberExample();
                example.createCriteria().andIdIn(memberList);
                i = umsMemberMapper.updateByExampleSelective(member, example);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return i;
    }

    @Override
    public int update(Long id, UmsMember umsMember) {
        umsMember.setId(id);
        return umsMemberMapper.updateByPrimaryKeySelective(umsMember);
    }

    @Override
    public List<UmsMember> channelList(UmsMemberParamDto umsMemberParamDto) {
        LOGGER.info("【渠道用戶列表获取】【入参】：{}", JSONUtil.toJsonStr(umsMemberParamDto));
        List<UmsMember> memberList = new ArrayList<>();
        try {
            UmsMemberExample example = new UmsMemberExample();
            UmsMemberExample.Criteria criteria = example.createCriteria();
            criteria.andChannelIdIsNotNull();
            if(!Objects.isNull(umsMemberParamDto.getChannelId())){
                criteria.andChannelIdEqualTo(umsMemberParamDto.getChannelId());
            }
            if (0 == umsMemberParamDto.getIsOrder()) {
                criteria.andOrderNumEqualTo(0);
            } else {
                criteria.andOrderNumGreaterThan(0);
            }
            if (!StrUtil.isEmpty(umsMemberParamDto.getStartTime()) && !StrUtil.isEmpty(umsMemberParamDto.getEndTime())) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                criteria.andCreateTimeGreaterThanOrEqualTo(DateUtil.parse(umsMemberParamDto.getStartTime(), sdf));
                criteria.andCreateTimeLessThanOrEqualTo(DateUtil.parse(umsMemberParamDto.getEndTime(), sdf));
            }
            PageHelper.startPage(umsMemberParamDto.getPageNum(), umsMemberParamDto.getPageSize());
            memberList = umsMemberMapper.selectByExample(example);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return memberList;
    }

    @Override
    public int register(UmsMemberParamDto umsMemberParamDto) {
        //新建注册用户，渠道推广注册、自然流量注册、后台注册
        LOGGER.info("【注册客户端账号】【入参】：{}",JSONUtil.toJsonStr(umsMemberParamDto));
        int insert = 0;
        try{
            UmsMember umsMember = new UmsMember();
            umsMember.setPhone(umsMemberParamDto.getPhone());
            umsMember.setSourceType(umsMemberParamDto.getSourceType());
            //验证是否后台渠道注册
            if(Objects.isNull(umsMemberParamDto.getChannelId())){//空则不为渠道注册
                //验证是否有推广id
                String promotionId = umsMemberParamDto.getPromotionId();
                if(!StrUtil.isEmpty(promotionId)){
                    //根据渠道推广id 获取渠道信息
                    UmsAdminExample example = new UmsAdminExample();
                    example.createCriteria().andTypeEqualTo(1).andPromotionIdEqualTo(promotionId);
                    List<UmsAdmin> umsAdmins = umsAdminMapper.selectByExample(example);
                    if(umsAdmins.isEmpty()){
                        LOGGER.error("【渠道推广注册id不存在】【入参】：{}",promotionId);
                    }else{
                        umsMember.setChannelId(umsAdmins.get(0).getId());
                        umsMember.setChannelName(umsAdmins.get(0).getUsername());
                    }
                }
            }else{
                Long channelId = umsMemberParamDto.getChannelId();
                umsMember.setChannelId(channelId);
                //根据id 获取渠道名称
                UmsAdmin umsAdmin = umsAdminMapper.selectByPrimaryKey(channelId);
                umsMember.setChannelName(umsAdmin.getUsername());
            }
            umsMember.setCreateTime(new Date());
            umsMember.setStatus(1);
            insert = umsMemberMapper.insert(umsMember);
        }catch (Exception e){
            e.printStackTrace();
        }
        return insert;
    }

    @Override
    public UmsMemberDetail getItem(Long id) {
        LOGGER.info("【获取用户详情信息】【用户id】:{}",id);
        UmsMemberDetail detail = null;
        try {
            //获取用户信息
            UmsMember member = umsMemberMapper.selectByPrimaryKey(id);
            detail = JSONUtil.toBean(JSONUtil.toJsonStr(member),UmsMemberDetail.class);
            //获取订单列表
            OmsOrderExample example = new OmsOrderExample();
            example.createCriteria().andMemberIdEqualTo(id);
            List<OmsOrder> omsOrders = orderMapper.selectByExample(example);
            List<UmsMemberOrder> umsMemberOrderList = new ArrayList<>();
            for(OmsOrder omsOrder : omsOrders){
                UmsMemberOrder umsMemberOrder = new UmsMemberOrder();
                umsMemberOrder.setOrderSn(omsOrder.getOrderSn());
                umsMemberOrder.setStatus(omsOrder.getStatus());
                //获取租期
                OmsOrderItemExample itemExample = new OmsOrderItemExample();
                itemExample.createCriteria().andOrderSnEqualTo(omsOrder.getOrderSn());
                List<OmsOrderItem> omsOrderItems = itemMapper.selectByExample(itemExample);
                Integer leaseNum = omsOrderItems.get(0).getLeaseNum();
                Integer stageNum = omsOrderItems.get(0).getStageNum();
                umsMemberOrder.setLeaseNum(leaseNum);
                umsMemberOrder.setStageNum(stageNum);
                //返回租期
                OmsOrderStageExample stageExample = new OmsOrderStageExample();
                stageExample.createCriteria().andMemberIdEqualTo(id).andOrderSnEqualTo(omsOrder.getOrderSn()).andStatusEqualTo(2);
                List<OmsOrderStage> omsOrderStages = stageMapper.selectByExample(stageExample);
                umsMemberOrder.setReturnNum(omsOrderStages.size());
                umsMemberOrderList.add(umsMemberOrder);
            }
            detail.setUmsMemberOrderList(umsMemberOrderList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }
}
