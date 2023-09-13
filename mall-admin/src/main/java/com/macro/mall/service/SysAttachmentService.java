package com.macro.mall.service;

import com.macro.mall.dto.PmsProductCategoryParam;
import com.macro.mall.dto.PmsProductCategoryWithChildrenItem;
import com.macro.mall.model.PmsProductCategory;
import com.macro.mall.model.SysAttachment;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author songjd
 * @description 文件服务
 * @date 2023/8/22 下午 8:53
 */

public interface SysAttachmentService {
    SysAttachment findByFileId(String encodeFilename);

    int save(SysAttachment sysAttachment);
}
