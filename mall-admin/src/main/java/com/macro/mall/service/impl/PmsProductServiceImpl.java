package com.macro.mall.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.macro.mall.dao.*;
import com.macro.mall.dto.PmsProductParam;
import com.macro.mall.dto.PmsProductQueryParam;
import com.macro.mall.dto.PmsProductResult;
import com.macro.mall.mapper.*;
import com.macro.mall.model.*;
import com.macro.mall.service.PmsProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品管理Service实现类
 *
 * @author songjd
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PmsProductServiceImpl implements PmsProductService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PmsProductServiceImpl.class);

    private final PmsProductMapper productMapper;

    private final PmsMemberPriceDao memberPriceDao;

    private final PmsMemberPriceMapper memberPriceMapper;

    private final PmsProductLadderDao productLadderDao;

    private final PmsProductLadderMapper productLadderMapper;

    private final PmsProductFullReductionDao productFullReductionDao;

    private final PmsProductFullReductionMapper productFullReductionMapper;

    private final PmsSkuStockDao skuStockDao;

    private final PmsSkuStockMapper skuStockMapper;

    private final PmsProductAttributeValueDao productAttributeValueDao;

    private final PmsProductAttributeValueMapper productAttributeValueMapper;

    private final CmsSubjectProductRelationDao subjectProductRelationDao;

    private final CmsSubjectProductRelationMapper subjectProductRelationMapper;

    private final CmsPrefrenceAreaProductRelationDao prefrenceAreaProductRelationDao;

    private final CmsPrefrenceAreaProductRelationMapper prefrenceAreaProductRelationMapper;

    private final PmsProductDao productDao;

    private final PmsProductVertifyRecordDao productVertifyRecordDao;

    private final PmsProductRentMapper pmsProductRentMapper;

    private final PmsProductCategoryMapper pmsProductCategoryMapper;

    @Override
    public int create(PmsProductParam productParam) {
        //创建商品
        PmsProduct product = productParam;
        product.setId(null);
        product.setDeleteStatus(0);
        product.setProductSn(String.valueOf(System.currentTimeMillis()));
        product.setPromotionStartTime(new Date());
        product.setPromotionEndTime(new Date());
        Long productCategoryId = productParam.getProductCategoryId();
        String categoryName = dealCategoryName(productCategoryId);
        product.setProductCategoryName(categoryName);

        if (StringUtils.isNotBlank(product.getAlbumPics())) {
            product.setPic(product.getAlbumPics().split(",")[0]);
        }
        List<PmsSkuStock> skuStockList = productParam.getSkuStockList();
        List<BigDecimal> priceList = skuStockList.stream().map(PmsSkuStock::getPrice).sorted().collect(Collectors.toList());
        product.setNote(StringUtils.join(priceList, "/"));

        int count = productMapper.insertSelective(product);
        //根据促销类型设置价格：会员价格、阶梯价格、满减价格
        Long productId = product.getId();
        //会员价格
        //relateAndInsertList(memberPriceDao, productParam.getMemberPriceList(), productId);
        //阶梯价格
        //relateAndInsertList(productLadderDao, productParam.getProductLadderList(), productId);
        //满减价格
        //relateAndInsertList(productFullReductionDao, productParam.getProductFullReductionList(), productId);
        //处理sku的编码
        //handleSkuStockCode(productParam.getSkuStockList(), productId);
        //添加sku库存信息
        relateAndInsertList(skuStockDao, productParam.getSkuStockList(), productId);
        //处理sku的租金
        handleSkuRent(productParam.getSkuStockList(), productParam.getProductRentList(), product);
        //添加商品参数,添加自定义商品规格
        //relateAndInsertList(productAttributeValueDao, productParam.getProductAttributeValueList(), productId);
        //关联专题
        //relateAndInsertList(subjectProductRelationDao, productParam.getSubjectProductRelationList(), productId);
        //关联优选
        //relateAndInsertList(prefrenceAreaProductRelationDao, productParam.getPrefrenceAreaProductRelationList(), productId);
        //添加租金方案
        return count;
    }

    private String dealCategoryName(Long productCategoryId) {
        String categoryName = null;
        PmsProductCategory pmsProductCategory = pmsProductCategoryMapper.selectByPrimaryKey(productCategoryId);
        while (pmsProductCategory != null) {
            if (StringUtils.isBlank(categoryName)) {
                categoryName = pmsProductCategory.getName();
            } else {
                categoryName = pmsProductCategory.getName() + "/" + categoryName;
            }
            Long parentId = pmsProductCategory.getParentId();
            pmsProductCategory = pmsProductCategoryMapper.selectByPrimaryKey(parentId);
        }
        return categoryName;
    }

    private void handleSkuRent(List<PmsSkuStock> skuStockList, List<PmsProductRent> productRentList, PmsProduct pmsProduct) {

        //删除原来的
        PmsProductRentExample example = new PmsProductRentExample();
        example.createCriteria().andProductIdEqualTo(pmsProduct.getId());
        pmsProductRentMapper.deleteByExample(example);

        //重新添加
        Integer repaymentType = pmsProduct.getRepaymentType();
        //灵活还款
        if (repaymentType == 1) {
            PmsSkuStock pmsSkuStock = skuStockList.get(0);
            for (int i = 0; i < productRentList.size(); i++) {
                PmsProductRent pmsProductRent = productRentList.get(0);
                pmsProductRent.setSkuId(pmsSkuStock.getId());
                pmsProductRent.setProductId(pmsProduct.getId());
                pmsProductRentMapper.insert(pmsProductRent);
            }
        } else {
            //还款期数
            Integer repaymentNumber = pmsProduct.getRepaymentNumber();
            skuStockList.forEach(skuStock -> {
                BigDecimal rentAvg = skuStock.getPrice().divide(new BigDecimal(repaymentNumber), 2, RoundingMode.HALF_UP);
                for (int i = 0; i < repaymentNumber; i++) {
                    PmsProductRent pmsProductRent = PmsProductRent.builder().productId(pmsProduct.getId()).skuId(skuStock.getId())
                            .stages(i + 1).stagesPrice(rentAvg).build();

                    pmsProductRentMapper.insert(pmsProductRent);
                }

            });

        }

    }

    private void handleSkuStockCode(List<PmsSkuStock> skuStockList, Long productId) {
        if (CollectionUtils.isEmpty(skuStockList)) {
            return;
        }
        for (int i = 0; i < skuStockList.size(); i++) {
            PmsSkuStock skuStock = skuStockList.get(i);
            if (StrUtil.isEmpty(skuStock.getSkuCode())) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                StringBuilder sb = new StringBuilder();
                //日期
                sb.append(sdf.format(new Date()));
                //四位商品id
                sb.append(String.format("%04d", productId));
                //三位索引id
                sb.append(String.format("%03d", i + 1));
                skuStock.setSkuCode(sb.toString());
            }
        }
    }

    @Override
    public PmsProductResult getUpdateInfo(Long id) {
        return productDao.getUpdateInfo(id);
    }

    @Override
    public int update(Long id, PmsProductParam productParam) {
        int count;
        //更新商品信息
        PmsProduct product = productParam;
        product.setId(id);
        Long productCategoryId = productParam.getProductCategoryId();
        String categoryName = dealCategoryName(productCategoryId);
        product.setProductCategoryName(categoryName);
        product.setPromotionEndTime(new Date());

        if (StringUtils.isNotBlank(product.getAlbumPics())) {
            product.setPic(product.getAlbumPics().split(",")[0]);
        }
        productMapper.updateByPrimaryKeySelective(product);
        //会员价格
//        PmsMemberPriceExample pmsMemberPriceExample = new PmsMemberPriceExample();
//        pmsMemberPriceExample.createCriteria().andProductIdEqualTo(id);
//        memberPriceMapper.deleteByExample(pmsMemberPriceExample);
//        relateAndInsertList(memberPriceDao, productParam.getMemberPriceList(), id);
        //阶梯价格
//        PmsProductLadderExample ladderExample = new PmsProductLadderExample();
//        ladderExample.createCriteria().andProductIdEqualTo(id);
//        productLadderMapper.deleteByExample(ladderExample);
//        relateAndInsertList(productLadderDao, productParam.getProductLadderList(), id);
        //满减价格
//        PmsProductFullReductionExample fullReductionExample = new PmsProductFullReductionExample();
//        fullReductionExample.createCriteria().andProductIdEqualTo(id);
//        productFullReductionMapper.deleteByExample(fullReductionExample);
//        relateAndInsertList(productFullReductionDao, productParam.getProductFullReductionList(), id);
        //修改sku库存信息
        handleUpdateSkuStockList(id, productParam);
        //处理sku的租金
        handleSkuRent(productParam.getSkuStockList(), productParam.getProductRentList(), product);
        //修改商品参数,添加自定义商品规格
//        PmsProductAttributeValueExample productAttributeValueExample = new PmsProductAttributeValueExample();
//        productAttributeValueExample.createCriteria().andProductIdEqualTo(id);
//        productAttributeValueMapper.deleteByExample(productAttributeValueExample);
//        relateAndInsertList(productAttributeValueDao, productParam.getProductAttributeValueList(), id);
        //关联专题
//        CmsSubjectProductRelationExample subjectProductRelationExample = new CmsSubjectProductRelationExample();
//        subjectProductRelationExample.createCriteria().andProductIdEqualTo(id);
//        subjectProductRelationMapper.deleteByExample(subjectProductRelationExample);
//        relateAndInsertList(subjectProductRelationDao, productParam.getSubjectProductRelationList(), id);
//        //关联优选
//        CmsPrefrenceAreaProductRelationExample prefrenceAreaExample = new CmsPrefrenceAreaProductRelationExample();
//        prefrenceAreaExample.createCriteria().andProductIdEqualTo(id);
//        prefrenceAreaProductRelationMapper.deleteByExample(prefrenceAreaExample);
//        relateAndInsertList(prefrenceAreaProductRelationDao, productParam.getPrefrenceAreaProductRelationList(), id);
        count = 1;
        return count;
    }

    private void handleUpdateSkuStockList(Long id, PmsProductParam productParam) {
        //当前的sku信息
        List<PmsSkuStock> currSkuList = productParam.getSkuStockList();
        //当前没有sku直接删除
        if (CollUtil.isEmpty(currSkuList)) {
            PmsSkuStockExample skuStockExample = new PmsSkuStockExample();
            skuStockExample.createCriteria().andProductIdEqualTo(id);
            skuStockMapper.deleteByExample(skuStockExample);
            return;
        }
        //获取初始sku信息
        PmsSkuStockExample skuStockExample = new PmsSkuStockExample();
        skuStockExample.createCriteria().andProductIdEqualTo(id);
        List<PmsSkuStock> oriStuList = skuStockMapper.selectByExample(skuStockExample);
        //获取新增sku信息
        List<PmsSkuStock> insertSkuList = currSkuList.stream().filter(item -> item.getId() == null).collect(Collectors.toList());
        //获取需要更新的sku信息
        List<PmsSkuStock> updateSkuList = currSkuList.stream().filter(item -> item.getId() != null).collect(Collectors.toList());
        List<Long> updateSkuIds = updateSkuList.stream().map(PmsSkuStock::getId).collect(Collectors.toList());
        //获取需要删除的sku信息
        List<PmsSkuStock> removeSkuList = oriStuList.stream().filter(item -> !updateSkuIds.contains(item.getId())).collect(Collectors.toList());
        handleSkuStockCode(insertSkuList, id);
        handleSkuStockCode(updateSkuList, id);
        //新增sku
        if (CollUtil.isNotEmpty(insertSkuList)) {
            relateAndInsertList(skuStockDao, insertSkuList, id);
        }
        //删除sku
        if (CollUtil.isNotEmpty(removeSkuList)) {
            List<Long> removeSkuIds = removeSkuList.stream().map(PmsSkuStock::getId).collect(Collectors.toList());
            PmsSkuStockExample removeExample = new PmsSkuStockExample();
            removeExample.createCriteria().andIdIn(removeSkuIds);
            skuStockMapper.deleteByExample(removeExample);
        }
        //修改sku
        if (CollUtil.isNotEmpty(updateSkuList)) {
            for (PmsSkuStock pmsSkuStock : updateSkuList) {
                skuStockMapper.updateByPrimaryKeySelective(pmsSkuStock);
            }
        }

    }

    @Override
    public List<PmsProduct> list(PmsProductQueryParam productQueryParam, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        PmsProductExample productExample = new PmsProductExample();
        PmsProductExample.Criteria criteria = productExample.createCriteria();
        criteria.andDeleteStatusEqualTo(0);
        if (productQueryParam.getPublishStatus() != null) {
            criteria.andPublishStatusEqualTo(productQueryParam.getPublishStatus());
        }
        if (!StrUtil.isEmpty(productQueryParam.getKeyword())) {
            criteria.andNameLike("%" + productQueryParam.getKeyword() + "%");
        }
        if (!StrUtil.isEmpty(productQueryParam.getProductSn())) {
            criteria.andProductSnEqualTo(productQueryParam.getProductSn());
        }
        if (productQueryParam.getProductCategoryId() != null) {
            criteria.andProductCategoryIdEqualTo(productQueryParam.getProductCategoryId());
        }
        if (productQueryParam.getShowStatus() != null) {
            criteria.andShowStatusEqualTo(productQueryParam.getShowStatus());
        }

        productExample.setOrderByClause("promotion_start_time desc");
        return productMapper.selectByExample(productExample);
    }

    @Override
    public int updateVerifyStatus(List<Long> ids, Integer verifyStatus, String detail) {
        PmsProduct product = new PmsProduct();
        product.setVerifyStatus(verifyStatus);
        PmsProductExample example = new PmsProductExample();
        example.createCriteria().andIdIn(ids);
        List<PmsProductVertifyRecord> list = new ArrayList<>();
        int count = productMapper.updateByExampleSelective(product, example);
        //修改完审核状态后插入审核记录
        for (Long id : ids) {
            PmsProductVertifyRecord record = new PmsProductVertifyRecord();
            record.setProductId(id);
            record.setCreateTime(new Date());
            record.setDetail(detail);
            record.setStatus(verifyStatus);
            record.setVertifyMan("test");
            list.add(record);
        }
        productVertifyRecordDao.insertList(list);
        return count;
    }

    @Override
    public int updatePublishStatus(List<Long> ids, Integer publishStatus) {
        PmsProduct record = new PmsProduct();
        record.setPublishStatus(publishStatus);
        PmsProductExample example = new PmsProductExample();
        example.createCriteria().andIdIn(ids);
        return productMapper.updateByExampleSelective(record, example);
    }

//    @Override
//    public int updateRecommendStatus(List<Long> ids, Integer recommendStatus) {
//        PmsProduct record = new PmsProduct();
//        record.setRecommandStatus(recommendStatus);
//        PmsProductExample example = new PmsProductExample();
//        example.createCriteria().andIdIn(ids);
//        return productMapper.updateByExampleSelective(record, example);
//    }

//    @Override
//    public int updateNewStatus(List<Long> ids, Integer newStatus) {
//        PmsProduct record = new PmsProduct();
//        record.setNewStatus(newStatus);
//        PmsProductExample example = new PmsProductExample();
//        example.createCriteria().andIdIn(ids);
//        return productMapper.updateByExampleSelective(record, example);
//    }

    @Override
    public int updateDeleteStatus(List<Long> ids, Integer deleteStatus) {
        PmsProduct record = new PmsProduct();
        record.setDeleteStatus(deleteStatus);
        PmsProductExample example = new PmsProductExample();
        example.createCriteria().andIdIn(ids);
        return productMapper.updateByExampleSelective(record, example);
    }

    @Override
    public List<PmsProduct> list(String keyword) {
        PmsProductExample productExample = new PmsProductExample();
        PmsProductExample.Criteria criteria = productExample.createCriteria();
        criteria.andDeleteStatusEqualTo(0);
        if (!StrUtil.isEmpty(keyword)) {
            criteria.andNameLike("%" + keyword + "%");
            productExample.or().andDeleteStatusEqualTo(0).andProductSnLike("%" + keyword + "%");
        }
        return productMapper.selectByExample(productExample);
    }

    /**
     * 建立和插入关系表操作
     *
     * @param dao       可以操作的dao
     * @param dataList  要插入的数据
     * @param productId 建立关系的id
     */
    private void relateAndInsertList(Object dao, List dataList, Long productId) {
        try {
            if (CollectionUtils.isEmpty(dataList)) return;
            for (Object item : dataList) {
                Method setId = item.getClass().getMethod("setId", Long.class);
                setId.invoke(item, (Long) null);
                Method setProductId = item.getClass().getMethod("setProductId", Long.class);
                setProductId.invoke(item, productId);
            }
            Method insertList = dao.getClass().getMethod("insertList", List.class);
            insertList.invoke(dao, dataList);
        } catch (Exception e) {
            LOGGER.warn("创建产品出错:{}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

}
