package com.macro.mall.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class OmsOrderStage implements Serializable {
    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "订单主键id")
    private Long orderId;

    @ApiModelProperty(value = "订单编号")
    private String orderSn;

    @ApiModelProperty(value = "订单商品id")
    private Long orderItemId;

    @ApiModelProperty(value = "租赁分期数")
    private Integer stageNum;

    @ApiModelProperty(value = "买断方式：0->首期支付 1->尾期支付")
    private Integer buyoutType;

    @ApiModelProperty(value = "账单内容")
    private String billContent;

    @ApiModelProperty(value = "状态: 1 待归还 2 已归还 3 逾期")
    private Integer status;

    @ApiModelProperty(value = "归还期数")
    private Integer payNo;

    @ApiModelProperty(value = "本期金额,买断金额,增值服务金额")
    private BigDecimal periodPrice;

    @ApiModelProperty(value = "还款时间")
    private Date repaymentTime;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "用户id")
    private Long memberId;

    @ApiModelProperty(value = "是否逾期：0 未 1 逾期")
    private Integer isOver;

    @ApiModelProperty(value = "支付方式：0 微信 1 支付宝 2 线下支付（后台支付）")
    private Integer payType;

    @ApiModelProperty(value = "支付流水号，线下履约为空")
    private String flowingSn;

    @ApiModelProperty(value = "账单类型：0 租金 1 买断 2 增值服务")
    private Integer billType;

    @ApiModelProperty(value = "支付时间")
    private Date payTime;

    @ApiModelProperty(value = "支付金额")
    private BigDecimal payPrice;

    @ApiModelProperty(value = "逾期金额")
    private BigDecimal overPrice;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public Long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    public Integer getStageNum() {
        return stageNum;
    }

    public void setStageNum(Integer stageNum) {
        this.stageNum = stageNum;
    }

    public Integer getBuyoutType() {
        return buyoutType;
    }

    public void setBuyoutType(Integer buyoutType) {
        this.buyoutType = buyoutType;
    }

    public String getBillContent() {
        return billContent;
    }

    public void setBillContent(String billContent) {
        this.billContent = billContent;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getPayNo() {
        return payNo;
    }

    public void setPayNo(Integer payNo) {
        this.payNo = payNo;
    }

    public BigDecimal getPeriodPrice() {
        return periodPrice;
    }

    public void setPeriodPrice(BigDecimal periodPrice) {
        this.periodPrice = periodPrice;
    }

    public Date getRepaymentTime() {
        return repaymentTime;
    }

    public void setRepaymentTime(Date repaymentTime) {
        this.repaymentTime = repaymentTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Integer getIsOver() {
        return isOver;
    }

    public void setIsOver(Integer isOver) {
        this.isOver = isOver;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public String getFlowingSn() {
        return flowingSn;
    }

    public void setFlowingSn(String flowingSn) {
        this.flowingSn = flowingSn;
    }

    public Integer getBillType() {
        return billType;
    }

    public void setBillType(Integer billType) {
        this.billType = billType;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public BigDecimal getPayPrice() {
        return payPrice;
    }

    public void setPayPrice(BigDecimal payPrice) {
        this.payPrice = payPrice;
    }

    public BigDecimal getOverPrice() {
        return overPrice;
    }

    public void setOverPrice(BigDecimal overPrice) {
        this.overPrice = overPrice;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", orderId=").append(orderId);
        sb.append(", orderSn=").append(orderSn);
        sb.append(", orderItemId=").append(orderItemId);
        sb.append(", stageNum=").append(stageNum);
        sb.append(", buyoutType=").append(buyoutType);
        sb.append(", billContent=").append(billContent);
        sb.append(", status=").append(status);
        sb.append(", payNo=").append(payNo);
        sb.append(", periodPrice=").append(periodPrice);
        sb.append(", repaymentTime=").append(repaymentTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", memberId=").append(memberId);
        sb.append(", isOver=").append(isOver);
        sb.append(", payType=").append(payType);
        sb.append(", flowingSn=").append(flowingSn);
        sb.append(", billType=").append(billType);
        sb.append(", payTime=").append(payTime);
        sb.append(", payPrice=").append(payPrice);
        sb.append(", overPrice=").append(overPrice);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}