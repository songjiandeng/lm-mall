package com.macro.mall.mapper;

import com.macro.mall.model.PmsProductRent;
import com.macro.mall.model.PmsProductRentExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PmsProductRentMapper {
    long countByExample(PmsProductRentExample example);

    int deleteByExample(PmsProductRentExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PmsProductRent row);

    int insertSelective(PmsProductRent row);

    List<PmsProductRent> selectByExample(PmsProductRentExample example);

    PmsProductRent selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") PmsProductRent row, @Param("example") PmsProductRentExample example);

    int updateByExample(@Param("row") PmsProductRent row, @Param("example") PmsProductRentExample example);

    int updateByPrimaryKeySelective(PmsProductRent row);

    int updateByPrimaryKey(PmsProductRent row);
}