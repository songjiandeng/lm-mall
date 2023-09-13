package com.macro.mall.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class PmsProduct implements Serializable {
    private Long id;

    @ApiModelProperty(value = "上架平台：1->APP；2->微信小程序；3->支付宝小程序")
    private String platformId;

    @ApiModelProperty(value = "所属品牌id")
    private Long brandId;

    @ApiModelProperty(value = "商品分类id")
    private Long productCategoryId;

    @ApiModelProperty(value = "运费模板id")
    private Long feightTemplateId;

    @ApiModelProperty(value = "商品品属性分类id")
    private Long productAttributeCategoryId;

    @ApiModelProperty(value = "商品名称")
    private String name;

    private String pic;

    @ApiModelProperty(value = "货号")
    private String productSn;

    @ApiModelProperty(value = "删除状态：0->未删除；1->已删除")
    private Integer deleteStatus;

    @ApiModelProperty(value = "上架状态：0->下架；1->上架")
    private Integer publishStatus;

    @ApiModelProperty(value = "显示状态:  0->隐藏；1->显示")
    private Integer showStatus;

    @ApiModelProperty(value = "新品状态:0->不是新品；1->新品")
    private Integer newStatus;

    @ApiModelProperty(value = "推荐状态；0->不推荐；1->推荐")
    private Integer recommandStatus;

    @ApiModelProperty(value = "审核状态：0->未审核；1->审核通过")
    private Integer verifyStatus;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "销量")
    private Integer sale;

    private BigDecimal price;

    @ApiModelProperty(value = "促销价格")
    private BigDecimal promotionPrice;

    @ApiModelProperty(value = "赠送的成长值")
    private Integer giftGrowth;

    @ApiModelProperty(value = "赠送的积分")
    private Integer giftPoint;

    @ApiModelProperty(value = "限制使用的积分数")
    private Integer usePointLimit;

    @ApiModelProperty(value = "副标题")
    private String subTitle;

    @ApiModelProperty(value = "市场价")
    private BigDecimal originalPrice;

    @ApiModelProperty(value = "库存")
    private Integer stock;

    @ApiModelProperty(value = "库存预警值")
    private Integer lowStock;

    @ApiModelProperty(value = "单位")
    private String unit;

    @ApiModelProperty(value = "商品重量，默认为克")
    private BigDecimal weight;

    @ApiModelProperty(value = "是否为预告商品：0->不是；1->是")
    private Integer previewStatus;

    @ApiModelProperty(value = "以逗号分割的产品服务：1->无忧退货；2->快速退款；3->免费包邮")
    private String serviceIds;

    private String keywords;

    private String note;

    @ApiModelProperty(value = "画册图片，连产品图片限制为5张，以逗号分割")
    private String albumPics;

    private String detailTitle;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @ApiModelProperty(value = "创建时间")
    private Date promotionStartTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @ApiModelProperty(value = "更新时间")
    private Date promotionEndTime;

    @ApiModelProperty(value = "活动限购数量")
    private Integer promotionPerLimit;

    @ApiModelProperty(value = "促销类型：0->没有促销使用原价;1->使用促销价；2->使用会员价；3->使用阶梯价格；4->使用满减价格；5->限时购")
    private Integer promotionType;

    @ApiModelProperty(value = "品牌名称")
    private String brandName;

    @ApiModelProperty(value = "商品分类名称")
    private String productCategoryName;

    @ApiModelProperty(value = "还款方式：0->非灵活还款 1->灵活还款")
    private Integer repaymentType;

    @ApiModelProperty(value = "买断方式：0->首期支付 1->尾期支付")
    private Integer buyoutType;

    @ApiModelProperty(value = "买断金额")
    private BigDecimal buyoutPrice;

    @ApiModelProperty(value = "发货地址")
    private String shippingAddress;

    @ApiModelProperty(value = "增值服务内容")
    private String addServer;

    @ApiModelProperty(value = "增值服务金额")
    private BigDecimal addServerPrice;

    @ApiModelProperty(value = "账单周期单位 0->月 1->周 2->日")
    private Integer billingCycleUnit;

    @ApiModelProperty(value = "账单周期值")
    private Integer billingCycle;

    @ApiModelProperty(value = "还款期数")
    private Integer repaymentNumber;

    @ApiModelProperty(value = "套餐名称 多个逗号隔开")
    private String packageName;

    @ApiModelProperty(value = "颜色 多个逗号隔开")
    private String color;

    @ApiModelProperty(value = "规格")
    private String specifications;

    @ApiModelProperty(value = "商品描述")
    private String description;

    private String detailDesc;

    @ApiModelProperty(value = "产品详情网页内容")
    private String detailHtml;

    @ApiModelProperty(value = "移动端网页详情")
    private String detailMobileHtml;

    @ApiModelProperty(value = "逾期罚金")
    private Integer latePenalty;

    private static final long serialVersionUID = 1L;

}
