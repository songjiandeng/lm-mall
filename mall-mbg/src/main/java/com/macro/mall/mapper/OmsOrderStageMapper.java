package com.macro.mall.mapper;

import com.macro.mall.model.OmsOrderStage;
import com.macro.mall.model.OmsOrderStageExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OmsOrderStageMapper {
    long countByExample(OmsOrderStageExample example);

    int deleteByExample(OmsOrderStageExample example);

    int deleteByPrimaryKey(Long id);

    int insert(OmsOrderStage row);

    int insertSelective(OmsOrderStage row);

    List<OmsOrderStage> selectByExample(OmsOrderStageExample example);

    OmsOrderStage selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") OmsOrderStage row, @Param("example") OmsOrderStageExample example);

    int updateByExample(@Param("row") OmsOrderStage row, @Param("example") OmsOrderStageExample example);

    int updateByPrimaryKeySelective(OmsOrderStage row);

    int updateByPrimaryKey(OmsOrderStage row);
}