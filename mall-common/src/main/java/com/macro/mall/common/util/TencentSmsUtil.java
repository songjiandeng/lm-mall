package com.macro.mall.common.util;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;

import static com.macro.mall.common.constant.TencentSmsConstant.*;

/**
 * @author songjd
 * @description Tencent Cloud Sms Sendsms
 * @date 2023/8/26 下午 3:09
 */

public class TencentSmsUtil {


    /**
     * 使用腾讯云短信发生验证码
     *
     * @param telephoneNumber  发送对象
     * @param templateId       短信模版ID（使用枚举类型）
     * @param templateParamSet 短信模版参数（这里一般为验证码）
     * @return 是否成功
     */
    public static boolean sendMessage(String telephoneNumber, String templateId, String... templateParamSet) {
        /* 必要步骤：
         * 实例化一个认证对象，入参需要传入腾讯云账户密钥对secretId，secretKey。
         * 这里采用的是从环境变量读取的方式，需要在环境变量中先设置这两个值。
         * 你也可以直接在代码中写死密钥对，但是小心不要将代码复制、上传或者分享给他人，
         * 以免泄露密钥对危及你的财产安全。
         * CAM密匙查询: https://console.cloud.tencent.com/cam/capi*/
        Credential cred = new Credential(secretId, secretKey);
        SendSmsRequest req = new SendSmsRequest();
        SmsClient client = new SmsClient(cred, "ap-guangzhou");

        /* 填充请求参数,这里request对象的成员变量即对应接口的入参
         * 你可以通过官网接口文档或跳转到request对象的定义处查看请求参数的定义
         * 基本类型的设置:
         * 帮助链接：
         * sms helper: https://cloud.tencent.com/document/product/382/3773 */
        req.setSmsSdkAppId(sdkAppId);

        /* 短信签名内容: 使用 UTF-8 编码，必须填写已审核通过的签名，签名信息可登录 [短信控制台] 查看 */
        req.setSignName(signName);

        /* 用户的 session 内容: 可以携带用户侧 ID 等上下文信息，server 会原样返回 */
        String sessionContext = "xxx";
        req.setSessionContext(sessionContext);
        req.setTemplateId(templateId);
        req.setPhoneNumberSet(new String[]{"+86" + telephoneNumber});
        /* 模板参数: 若无模板参数，则设置为空 */
        req.setTemplateParamSet(templateParamSet);
        /* 通过 client 对象调用 SendSms 方法发起请求。注意请求方法名与请求对象是对应的
         * 返回的 res 是一个 SendSmsResponse 类的实例，与请求对象对应 */
        SendSmsResponse res = null;
        try {
            res = client.SendSms(req);
        } catch (TencentCloudSDKException ex) {
            ex.printStackTrace();
            return false;
        }
        // 输出json格式的字符串回包
        System.out.println(SendSmsResponse.toJsonString(res));
        // 也可以取出单个值，你可以通过官网接口文档或跳转到response对象的定义处查看返回字段的定义
        System.out.println(res.getRequestId());
        return true;
    }

    public static void main(String[] args) {
        System.out.println(sendMessage("15394415675","1901811","1234"));
    }
}
