package com.macro.mall.portal.domain;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class OmsOrderCreateParam {

    @ApiModelProperty(value = "商品id")
    private Long productId;

    @ApiModelProperty(value = "用户id")
    private Long memberId;

    @ApiModelProperty(value = "规格列表id")
    private List<Long> skuIds;

    @ApiModelProperty(value = "订单类型：0->正常订单；1->后台生成订单")
    private Integer orderType;
    @ApiModelProperty(value = "订单来源：0->微信小程序；1->支付宝小程序；2->app 3->后台创建")
    private Integer sourceType;

    @ApiModelProperty(value = "收货人姓名")
    private String receiverName;

    @ApiModelProperty(value = "收货人电话")
    private String receiverPhone;

    @ApiModelProperty(value = "收货人邮编")
    private String receiverPostCode;

    @ApiModelProperty(value = "省份/直辖市")
    private String receiverProvince;

    @ApiModelProperty(value = "城市")
    private String receiverCity;

    @ApiModelProperty(value = "区")
    private String receiverRegion;

    @ApiModelProperty(value = "详细地址")
    private String receiverDetailAddress;

    @ApiModelProperty(value = "纬度")
    private Double latitude;


    @ApiModelProperty(value = "经度")
    private Double longitude;

    @ApiModelProperty(value = "订单状态：0->待审核；1->待支付；2->代发货；3->待收货；4->租用中；5->逾期；6->订单完成；7->订单关闭 11->审核拒绝")
    private Integer status;

    @ApiModelProperty(value = "订单编号")
    private String orderSn;

    @ApiModelProperty(value = "页码")
    private Integer pageNum;

    @ApiModelProperty(value = "数量")
    private Integer pageSize;
}
