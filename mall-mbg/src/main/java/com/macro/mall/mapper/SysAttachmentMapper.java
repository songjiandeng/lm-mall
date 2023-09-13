package com.macro.mall.mapper;

import com.macro.mall.model.SysAttachment;
import com.macro.mall.model.SysAttachmentExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SysAttachmentMapper {
    long countByExample(SysAttachmentExample example);

    int deleteByExample(SysAttachmentExample example);

    int deleteByPrimaryKey(Long id);

    int insert(SysAttachment row);

    int insertSelective(SysAttachment row);

    List<SysAttachment> selectByExample(SysAttachmentExample example);

    SysAttachment selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") SysAttachment row, @Param("example") SysAttachmentExample example);

    int updateByExample(@Param("row") SysAttachment row, @Param("example") SysAttachmentExample example);

    int updateByPrimaryKeySelective(SysAttachment row);

    int updateByPrimaryKey(SysAttachment row);
}