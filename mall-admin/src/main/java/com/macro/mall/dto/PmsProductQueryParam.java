package com.macro.mall.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品查询参数
 * fisher
 */
@Data
@EqualsAndHashCode
public class PmsProductQueryParam {
    @ApiModelProperty("上架状态")
    private Integer publishStatus;
    @ApiModelProperty("上架平台：1->APP；2->微信小程序；3->支付宝小程序")
    private Integer platformId;
    @ApiModelProperty("商品名称模糊关键字")
    private String keyword;
    @ApiModelProperty("商品编号")
    private String productSn;
    @ApiModelProperty("商品类目id")
    private Long productCategoryId;
    @ApiModelProperty("显示状态 0->隐藏；1->显示")
    private Integer showStatus;
}
