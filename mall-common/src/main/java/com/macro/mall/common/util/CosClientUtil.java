package com.macro.mall.common.util;


import com.macro.mall.common.config.CosConfig;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 上传工具类
 *
 * @author： dongao
 * @create: 2019/10/16
 */
@Component
public class CosClientUtil {

    private static CosConfig cosConfig = SpringUtils.getBean(CosConfig.class);

    /**
     * 初始化密钥信息
     */
    private COSCredentials cred = new BasicCOSCredentials(cosConfig.getSecretId(), cosConfig.getSecretKey());
    /**
     * 初始化客户端配置,设置bucket所在的区域
     */
    private ClientConfig clientConfig = new ClientConfig(new Region(cosConfig.getRegion()));
    /**
     * 初始化cOSClient
     */
    private COSClient cosClient = new COSClient(cred, clientConfig);

    /**
     * 上传图片
     *
     * @param file
     * @param businessName
     * @return
     * @throws Exception
     */
    public String uploadImgToCos(MultipartFile file, String businessName) throws Exception {
        int imageSize = Integer.parseInt(cosConfig.getImageSize());
        int maxSize = imageSize << 20;
        if (file.getSize() > maxSize) {
            throw new Exception("上传文件大小不能超过" + imageSize + "M！");
        }
        if (StringUtils.isEmpty(businessName)) {
            businessName = cosConfig.getCommon();
        }
        //生成文件夹层级
        Calendar cale = Calendar.getInstance();
        int year = cale.get(Calendar.YEAR);
        SimpleDateFormat sdf = new SimpleDateFormat("MM");
        Date dd = cale.getTime();
        String month = sdf.format(dd);
        String folderName = cosConfig.getProjectName() + "/image/" + businessName + "/" + year + "/" + month + "/";
        //图片名称
        String originalFilename = file.getOriginalFilename();
        Random random = new Random();
        //生成新的图片名称(随机数0-9999+系统当前时间+上传图片名)
        String name;
        if (originalFilename.lastIndexOf(".") != -1) {
            name = random.nextInt(10000) + System.currentTimeMillis() + originalFilename.substring(originalFilename.lastIndexOf("."));
        } else {
            String extension = file.getContentType();
            name = random.nextInt(10000) + System.currentTimeMillis() + "." + extension;
        }
        //生成对象键
        String key = folderName + name;
        try {
            InputStream inputStream = file.getInputStream();
            this.uploadFileToCos(inputStream, key);
            return key;
        } catch (Exception e) {
            throw new Exception("文件上传失败");
        }
    }

    /**
     * 以文件流方式上传图片
     *
     * @param is
     * @param businessName
     * @param originalFilename
     * @param fileSize
     * @return
     * @throws Exception
     */
    public String uploadImgToCos(InputStream is, String originalFilename, long fileSize, String businessName) throws Exception {
        int imageSize = Integer.parseInt(cosConfig.getImageSize());
        int maxSize = imageSize << 20;
        if (fileSize > maxSize) {
            throw new Exception("上传文件大小不能超过" + imageSize + "M！");
        }
        if (StringUtils.isEmpty(businessName)) {
            businessName = cosConfig.getCommon();
        }
        //生成文件夹层级
        Calendar cale = Calendar.getInstance();
        int year = cale.get(Calendar.YEAR);
        SimpleDateFormat sdf = new SimpleDateFormat("MM");
        Date dd = cale.getTime();
        String month = sdf.format(dd);
        String folderName = cosConfig.getProjectName() + "/image/" + businessName + "/" + year + "/" + month + "/";
        //图片名称
        Random random = new Random();
        //生成新的图片名称(随机数0-9999+系统当前时间+上传图片名)
        String name = random.nextInt(10000) + System.currentTimeMillis() + originalFilename.substring(originalFilename.lastIndexOf("."));
        //生成对象键
        String key = folderName + name;
        try {
            this.uploadFileToCos(is, key);
            //return "http://" + cosConfig.getBucketName() + ".cos."+cosConfig.getRegion()+".myqcloud.com/" + key;
            return cosConfig.getPrefixDomain() + key;
        } catch (Exception e) {
            throw new Exception("文件上传失败", e);
        }
    }

    /**
     * 上传到COS服务器 如果同名文件会覆盖服务器上的
     *
     * @param instream
     * @param key
     * @return 出错返回"" ,唯一MD5数字签名
     */
    public String uploadFileToCos(InputStream instream, String key) {
        String etag = "";
        try {
            // 创建上传Object的Metadata
            ObjectMetadata objectMetadata = new ObjectMetadata();
            // 设置输入流长度为500
            objectMetadata.setContentLength(instream.available());
            // 设置 Content type
            objectMetadata.setContentType(getcontentType(key.substring(key.lastIndexOf("."))));
            // 上传文件
            PutObjectResult putResult = cosClient.putObject("lm-app-1320139518", key, instream, objectMetadata);
            etag = putResult.getETag();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (instream != null) {
                    //关闭输入流
                    instream.close();
                }
                // 关闭客户端(关闭后台线程)
                cosClient.shutdown();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return etag;
    }

    /**
     * Description: 判断Cos服务文件上传时文件的contentType
     *
     * @param filenameExtension 文件后缀
     * @return String
     */
    public String getcontentType(String filenameExtension) {
        String bmp = "bmp";
        if (bmp.equalsIgnoreCase(filenameExtension)) {
            return "image/bmp";
        }
        String gif = "gif";
        if (gif.equalsIgnoreCase(filenameExtension)) {
            return "image/gif";
        }
        String jpeg = "jpeg";
        String jpg = "jpg";
        String png = "png";
        if (jpeg.equalsIgnoreCase(filenameExtension) || jpg.equalsIgnoreCase(filenameExtension)
                || png.equalsIgnoreCase(filenameExtension)) {
            return "image/jpeg";
        }
        String html = "html";
        if (html.equalsIgnoreCase(filenameExtension)) {
            return "text/html";
        }
        String txt = "txt";
        if (txt.equalsIgnoreCase(filenameExtension)) {
            return "text/plain";
        }
        String vsd = "vsd";
        if (vsd.equalsIgnoreCase(filenameExtension)) {
            return "application/vnd.visio";
        }
        String pptx = "pptx";
        String ppt = "ppt";
        if (pptx.equalsIgnoreCase(filenameExtension) || ppt.equalsIgnoreCase(filenameExtension)) {
            return "application/vnd.ms-powerpoint";
        }
        String docx = ".docx";
        String doc = ".doc";
        if (docx.equalsIgnoreCase(filenameExtension) || doc.equalsIgnoreCase(filenameExtension)) {
            return "application/msword";
        }
        String xml = "xml";
        if (xml.equalsIgnoreCase(filenameExtension)) {
            return "text/xml";
        }
        String mp4 = ".mp4";
        if (mp4.equalsIgnoreCase(filenameExtension)) {
            return "application/octet-stream";
        }
        String pdf = ".pdf";
        if (pdf.equalsIgnoreCase(filenameExtension)) {
            // 使用流的形式进行上传，防止下载文件的时候访问url会预览而不是下载。  return "application/pdf";
            return "application/octet-stream";
        }
        String xls = ".xls";
        String xlsx = ".xlsx";
        if (xls.equalsIgnoreCase(filenameExtension) || xlsx.equalsIgnoreCase(filenameExtension)) {
            return "application/vnd.ms-excel";
        }
        String mp3 = ".mp3";
        if (mp3.equalsIgnoreCase(filenameExtension)) {
            return "audio/mp3";
        }
        String wav = ".wav";
        if (wav.equalsIgnoreCase(filenameExtension)) {
            return "audio/wav";
        }
        return "image/jpeg";
    }

    /**
     * 获取预签名URL
     *
     * @param urlKey
     * @return
     */
    public String getPresignedUrl(String urlKey) {
        URL url = null;
        try {
            GeneratePresignedUrlRequest req =
                    new GeneratePresignedUrlRequest(cosConfig.getBucketName(), urlKey, HttpMethodName.GET);
            // 设置签名过期时间(可选), 若未进行设置, 则默认使用 ClientConfig 中的签名过期时间(1小时)
            // 可以设置任意一个未来的时间，推荐是设置 10 分钟到 3 天的过期时间
            // 这里设置签名在半个小时后过期
            Date expirationDate = new Date(System.currentTimeMillis() + cosConfig.getExpiration() * 60L * 1000L);
            req.setExpiration(expirationDate);
            url = cosClient.generatePresignedUrl(req);
        } catch (CosClientException e) {
            e.printStackTrace();
        } finally {
            cosClient.shutdown();
        }
        return url.toString();
    }

    /**
     * 获取预签名URL
     *
     * @param urlKey              资源路径
     * @param requestParameter    本次请求的参数
     * @param customRequestHeader 本次请求的头部。Host 头部会自动补全，不需要填写
     * @return
     */
    public String getPresignedUrl(String urlKey, Map<String, String> requestParameter, Map<String, String> customRequestHeader) {
        URL url = null;
        try {
            GeneratePresignedUrlRequest req =
                    new GeneratePresignedUrlRequest(cosConfig.getBucketName(), urlKey, HttpMethodName.GET);
            // 设置签名过期时间(可选), 若未进行设置, 则默认使用 ClientConfig 中的签名过期时间(1小时)
            // 可以设置任意一个未来的时间，推荐是设置 10 分钟到 3 天的过期时间
            // 这里设置签名在半个小时后过期
            Date expirationDate = new Date(System.currentTimeMillis() + cosConfig.getExpiration() * 60L * 1000L);
            req.setExpiration(expirationDate);

            // 填写本次请求的参数
            if (!requestParameter.isEmpty()) {
                Iterator<Map.Entry<String, String>> iterator = requestParameter.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, String> next = iterator.next();
                    String key = next.getKey();
                    String value = next.getValue();
                    req.addRequestParameter(key, value);
                }
            }
            // 填写本次请求的头部。Host 头部会自动补全，不需要填写
            if (!customRequestHeader.isEmpty()) {
                Iterator<Map.Entry<String, String>> iterator = customRequestHeader.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, String> next = iterator.next();
                    String key = next.getKey();
                    String value = next.getValue();
                    req.putCustomRequestHeader(key, value);
                }
            }
            url = cosClient.generatePresignedUrl(req);
        } catch (CosClientException e) {
            e.printStackTrace();
        } finally {
            cosClient.shutdown();
        }
        return url.toString();
    }

    /**
     * 在指定账号下创建一个存储桶。同一用户账号下，可以创建多个存储桶，数量上限是200个（不区分地域），存储桶中的对象数量没有限制。
     * 创建存储桶是低频操作，一般建议在控制台创建 Bucket，在 SDK 进行 Object 的操作。
     *
     * @return
     */
    public Bucket createBucket(String bucketName) {
        Bucket bucketResult = null;
        try {
            //存储桶名称，格式：BucketName-APPID
            String bucket = bucketName + "-1320139518";
            CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucket);
            // 设置 bucket 的权限为 Private(私有读写), 其他可选有公有读私有写, 公有读写
            createBucketRequest.setCannedAcl(CannedAccessControlList.Private);
            bucketResult = cosClient.createBucket(createBucketRequest);
        } catch (CosClientException e) {
            e.printStackTrace();
        } finally {
            cosClient.shutdown();
        }
        return bucketResult;
    }

    /**
     * 查询指定账号下所有的存储桶列表
     *
     * @return
     */
    public List<Bucket> listBuckets() {
        List<Bucket> buckets = null;
        try {
            // 如果只调用 listBuckets 方法，则创建 cosClient 时指定 region 为 new Region("") 即可
            buckets = cosClient.listBuckets();
        } catch (CosClientException e) {
            e.printStackTrace();
        } finally {
            cosClient.shutdown();
        }
        return buckets;
    }

    /**
     * 检索存储桶是否存在且是否有权限访问
     *
     * @param bucketName
     * @return
     */
    public boolean doesBucketExist(String bucketName) {
        boolean bucketExistFlag = false;
        try {
            // bucket的命名规则为 BucketName-APPID ，此处填写的存储桶名称必须为此格式
            String bucket = bucketName + "-1320139518";
            bucketExistFlag = cosClient.doesBucketExist(bucket);
        } catch (CosClientException e) {
            e.printStackTrace();
        } finally {
            cosClient.shutdown();
        }
        return bucketExistFlag;
    }

    /**
     * 删除指定账号下的空存储桶
     *
     * @param bucketName
     */
    public void deleteBucket(String bucketName) {
        try {
            // bucket的命名规则为 BucketName-APPID ，此处填写的存储桶名称必须为此格式
            String bucket = bucketName + "-1320139518";
            cosClient.deleteBucket(bucket);
        } catch (CosClientException e) {
            e.printStackTrace();
        } finally {
            cosClient.shutdown();
        }
    }

    /**
     * 获取存储桶的权限信息
     *
     * @param bucketName
     * @return
     */
    public CannedAccessControlList getBucketAcl(String bucketName) {
        CannedAccessControlList cannedAccessControlList = null;
        try {
            // bucket的命名规则为 BucketName-APPID ，此处填写的存储桶名称必须为此格式
            String bucket = bucketName + "-1320139518";
            AccessControlList accessControlList = cosClient.getBucketAcl(bucket);
            // 将存储桶权限转换为预设 ACL, 可选值为：Private, PublicRead, PublicReadWrite
            cannedAccessControlList = accessControlList.getCannedAccessControl();
        } catch (CosClientException e) {
            e.printStackTrace();
        } finally {
            cosClient.shutdown();
        }
        return cannedAccessControlList;
    }
}
