package com.macro.mall.controller;

import com.google.gson.Gson;
import com.kuaidi100.sdk.response.SubscribePushParamResp;
import com.kuaidi100.sdk.response.SubscribeResp;
import com.kuaidi100.sdk.utils.SignUtils;
import com.macro.mall.common.api.CommonResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author songjd
 * @description: 回调控制器
 * @date 2023/8/23
 */
@RestController
@RequestMapping("api/callBack")
public class CallBackController {


    /**
     * 订阅推送处理(参照)
     * 订阅成功后，如果该快递单号有轨迹（包括已经签收的单），快递100将会在15分钟-4个小时推送；后面将会4个小时跟踪一次，跟踪到有轨迹变化则推送；
     * 如果订阅成功后，3天查无结果（录错单/快递公司错了/揽收比较晚），快递100将会推送3天查无结果，可以继续重新订阅。
     *
     * 回调接口支持自定义参数,比如订阅时回调地址填写的是 http://www.xxx.com?orderId=1233333
     * 可以通过下面这种方式获取到orderId： String orderId = request.getParameter("orderId");
     *
     * 返回值必须是下面这样的格式，否则快递100将认为该推送失败，快递100将会重试3次该推送，时间间隔35分钟；
     * 成功结果返回例子： {"result":true,"returnCode":"200","message":"提交成功"}
     *
     * @param request
     * @return: com.kuaidi100.sdk.response.SubscribeResp
     * @author: api.kuaidi100.com
     */
    @PostMapping("Kuaidi100CallBack")
    public CommonResult Kuaidi100CallBack(HttpServletRequest request){
        String param = request.getParameter("param");
        String sign = request.getParameter("sign");
        //建议记录一下这个回调的内容，方便出问题后双方排查问题
        //log.debug("快递100订阅推送回调结果|{}|{}",param,sign);
        //订阅时传的salt,没有可以忽略
        String salt = "";
        String ourSign = SignUtils.sign(param + salt);
        SubscribeResp subscribeResp = new SubscribeResp();
        subscribeResp.setResult(Boolean.TRUE);
        subscribeResp.setReturnCode("200");
        subscribeResp.setMessage("成功");
        //加密如果相等，属于快递100推送；否则可以忽略掉当前请求
        if (ourSign.equals(sign)){
            //TODO 业务处理
            SubscribePushParamResp subscribePushParamResp = new Gson().fromJson(param, SubscribePushParamResp.class);
        }
        return null;
    }
}
