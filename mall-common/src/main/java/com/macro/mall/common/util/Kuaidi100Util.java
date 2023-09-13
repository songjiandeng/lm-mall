package com.macro.mall.common.util;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.kuaidi100.sdk.api.QueryTrack;
import com.kuaidi100.sdk.api.Subscribe;
import com.kuaidi100.sdk.contant.ApiInfoConstant;
import com.kuaidi100.sdk.contant.CompanyConstant;
import com.kuaidi100.sdk.core.IBaseClient;
import com.kuaidi100.sdk.pojo.HttpResult;
import com.kuaidi100.sdk.request.*;
import com.kuaidi100.sdk.response.QueryTrackData;
import com.kuaidi100.sdk.response.QueryTrackResp;
import com.kuaidi100.sdk.response.SubscribeResp;
import com.kuaidi100.sdk.utils.SignUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author songjd
 * @description: 快递100 工具类
 * @date 2023/8/22
 */
@Component
@Slf4j
public class Kuaidi100Util {

    private static final String CALLBACK_URL = "/api/callBack/Kuaidi100CallBack";


    private String key = "vnoKlNLo7178";

    private String customer = "A38B72535788FC124F0A3A2728FD6EB4";


    /**
     * 查询实时快递单轨迹
     *
     * @param company
     * @param orderNum
     * @param phone
     * @return
     * @throws Exception
     */
    public List<QueryTrackData> queryTrace(String company, String orderNum, String phone) throws Exception {

        company = StringUtils.isBlank(company) ? "yuantong" : company;
        phone = StringUtils.isBlank(phone) ? "15394415675" : phone;
        orderNum = StringUtils.isBlank(orderNum) ? "YT8921673590210" : orderNum;

        QueryTrackReq queryTrackReq = new QueryTrackReq();
        QueryTrackParam queryTrackParam = new QueryTrackParam();
        queryTrackParam.setCom(company);
        queryTrackParam.setNum(orderNum);
        queryTrackParam.setPhone(phone);
        String param = new Gson().toJson(queryTrackParam);

        queryTrackReq.setParam(param);
        queryTrackReq.setCustomer(customer);
        queryTrackReq.setSign(SignUtils.querySign(param, key, customer));

        IBaseClient baseClient = new QueryTrack();
        HttpResult httpResult = baseClient.execute(queryTrackReq);
        if (httpResult.getStatus() == HttpStatus.SC_OK && StringUtils.isNotBlank(httpResult.getBody())) {
            QueryTrackResp queryTrackResp = new Gson().fromJson(httpResult.getBody(), QueryTrackResp.class);
            return queryTrackResp.getData();
        }
        //异常信息打印
        log.info("【快递实时轨迹查询异常】【入参】：{}，{}，{} 【异常信息】:{}", company, orderNum, phone, new Gson().fromJson(httpResult.getBody(), QueryTrackResp.class));
        return null;
    }

    /**
     * 订阅接口  订阅快递单号，根据订单状态回调
     *
     * @param company
     * @param phone
     * @param orderNum
     * @return
     * @throws Exception
     */
    public SubscribeResp subscribe(String company, String phone, String orderNum) throws Exception {
        SubscribeParameters subscribeParameters = new SubscribeParameters();
        subscribeParameters.setCallbackurl(CALLBACK_URL);
        subscribeParameters.setPhone(phone);

        SubscribeParam subscribeParam = new SubscribeParam();
        subscribeParam.setParameters(subscribeParameters);
        //CompanyConstant.ST
        subscribeParam.setCompany(company);
        subscribeParam.setNumber(orderNum);
        subscribeParam.setKey(key);

        SubscribeReq subscribeReq = new SubscribeReq();
        subscribeReq.setSchema(ApiInfoConstant.SUBSCRIBE_SCHEMA);
        subscribeReq.setParam(new Gson().toJson(subscribeParam));

        IBaseClient subscribe = new Subscribe();
        HttpResult httpResult = subscribe.execute(subscribeReq);
        if (httpResult.getStatus() == HttpStatus.SC_OK && StringUtils.isNotBlank(httpResult.getBody())) {
            return new Gson().fromJson(httpResult.getBody(), SubscribeResp.class);
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        Kuaidi100Util kuaidi100Util = new Kuaidi100Util();
        List<QueryTrackData> queryTrackData = kuaidi100Util.queryTrace("yuantong", "YT8921673590210", "15394415675");
        System.out.println(JSONObject.toJSONString(queryTrackData));
    }
}
