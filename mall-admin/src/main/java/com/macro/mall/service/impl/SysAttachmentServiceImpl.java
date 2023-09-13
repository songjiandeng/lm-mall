package com.macro.mall.service.impl;

import com.macro.mall.mapper.SysAttachmentMapper;
import com.macro.mall.model.*;
import com.macro.mall.service.SysAttachmentService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author songjd
 * @description 文件服务实现类
 * @date 2023/8/22 下午 8:54
 */

@Service
@RequiredArgsConstructor
public class SysAttachmentServiceImpl implements SysAttachmentService {

    private final SysAttachmentMapper sysAttachmentMapper;

    @Override
    public SysAttachment findByFileId(String encodeFilename) {
        SysAttachmentExample sysAttachmentExample = new SysAttachmentExample();
        sysAttachmentExample.createCriteria().andFileIdEqualTo(encodeFilename);
        List<SysAttachment> sysAttachments = sysAttachmentMapper.selectByExample(sysAttachmentExample);
        if (CollectionUtils.isNotEmpty(sysAttachments)) {
            return sysAttachments.get(0);
        }
        return null;
    }

    @Override
    public int save(SysAttachment sysAttachment) {
        return sysAttachmentMapper.insert(sysAttachment);
    }
}
