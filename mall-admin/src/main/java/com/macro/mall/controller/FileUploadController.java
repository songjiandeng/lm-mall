package com.macro.mall.controller;

import com.google.common.collect.Lists;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.config.CosConfig;
import com.macro.mall.common.util.CosClientUtil;
import com.macro.mall.common.util.Encodes;
import com.macro.mall.model.SysAttachment;
import com.macro.mall.service.SysAttachmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("cos")
@Api(tags = "文件上传")
@Tag(name = "文件上传", description = "文件上传")
@RequiredArgsConstructor
public class FileUploadController {
    private final SysAttachmentService sysAttachmentService;
    private final CosClientUtil cosClientUtil;

    @ApiOperation("COS文件上传")
    @PostMapping("uploadFile")
    public CommonResult uploadFileCos(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<MultipartFile> uploadFiles = getUploadFiles(request);
        List<SysAttachment> sysAttachments = Lists.newArrayList();
        for (int i = 0; i < uploadFiles.size(); i++) {
            MultipartFile file = uploadFiles.get(i);
            String encodeFilename = Encodes.encodeUrlSafeBase64(DigestUtils.md5DigestAsHex(file.getBytes()).getBytes());
            SysAttachment sysAttachment = sysAttachmentService.findByFileId(encodeFilename);
            if (sysAttachment == null) {
                String extension = FilenameUtils.getExtension(file.getOriginalFilename());
                if (!StringUtils.hasText(extension)) {
                    extension = "png";
                }

                InputStream inputStream = file.getInputStream();
                String path = cosClientUtil.uploadImgToCos(inputStream, encodeFilename + "." + extension, file.getSize(), "");

                if (path.contains("上传失败")) {
                    continue;
                }
                sysAttachment = new SysAttachment();
                sysAttachment.setStatus(1);
                sysAttachment.setFileId(encodeFilename);
                sysAttachment.setFileName(file.getOriginalFilename());
                sysAttachment.setFileSize(file.getSize());
                sysAttachment.setFileExtension(extension);
                sysAttachment.setFileUrl(path);
                sysAttachment.setUpdateTime(new Date());
                sysAttachment.setCreateTime(sysAttachment.getUpdateTime());
                sysAttachmentService.save(sysAttachment);
            }
            sysAttachment.setFileName(file.getOriginalFilename());
            CosConfig cosConfig = new CosConfig();
            //sysAttachment.setAbsoluteFileUrl(cosConfig.getPrefixDomain() + sysAttachment.getFileUrl());
            sysAttachments.add(sysAttachment);
        }
        return CommonResult.success(sysAttachments);
    }

    // outputStream转inputStream
    public ByteArrayInputStream parse(final OutputStream out) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos = (ByteArrayOutputStream) out;
        final ByteArrayInputStream swapStream = new ByteArrayInputStream(baos.toByteArray());
        return swapStream;
    }


    /**
     * 获得批量的上传文件集合
     *
     * @param request
     * @return
     */
    public List<MultipartFile> getUploadFiles(HttpServletRequest request) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        List<MultipartFile> files = new ArrayList<MultipartFile>();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile mf = entity.getValue();
            files.add(mf);
        }
        return files;
    }
}
