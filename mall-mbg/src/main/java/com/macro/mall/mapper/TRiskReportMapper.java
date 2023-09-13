package com.macro.mall.mapper;

import com.macro.mall.model.TRiskReport;
import com.macro.mall.model.TRiskReportExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TRiskReportMapper {
    long countByExample(TRiskReportExample example);

    int deleteByExample(TRiskReportExample example);

    int deleteByPrimaryKey(Long id);

    int insert(TRiskReport row);

    int insertSelective(TRiskReport row);

    List<TRiskReport> selectByExample(TRiskReportExample example);

    TRiskReport selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") TRiskReport row, @Param("example") TRiskReportExample example);

    int updateByExample(@Param("row") TRiskReport row, @Param("example") TRiskReportExample example);

    int updateByPrimaryKeySelective(TRiskReport row);

    int updateByPrimaryKey(TRiskReport row);
}