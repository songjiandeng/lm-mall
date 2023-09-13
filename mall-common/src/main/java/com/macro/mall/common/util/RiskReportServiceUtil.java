package com.macro.mall.common.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author songjd
 * @description: 风控服务
 * @date 2023/8/26
 */
@Slf4j
public class RiskReportServiceUtil {

    //生产环境  https://shouwei.shouxin168.com/api/lightning/product/query
    private final static String URL = "http://sit-shouwei.shouxin168.com/sandbox/lightning/product/query";
    private final static String COMPANY_ID = "d6518f1e-9270-11e9-890c-9801a79f5a77";
    private final static String KEY = "Q0ymUIe1t26ZfG7s";

    /**
     * 查询风控报告
     *
     * @param name     姓名
     * @param idNumber 身份证
     * @param phone    手机号
     * @return
     */
    public static String queryRiskReport(String name, String idNumber, String phone) {
        try {
            Map<String, Object> customData = new TreeMap<>();
            // 请求参数（根据文档填值），参数不固定有多少，具体请求参数和说明在对接文档里有说明
            // 请替换成真实参数
            customData.put("name", name);
            customData.put("ident_number", idNumber);
            customData.put("phone", phone);
            // 非必传参数，根据业务情况传
//            customData.put("goods_type", 1);
//            customData.put("status", 0);
//            customData.put("total_rent", 130.20);
//            customData.put("total_periods", 2);
//            customData.put("price", 1200.00);
            customData.put("service", "risk_report_service");
            customData.put("mode", "JC604b4ad_CL5803109");
            // 上述参数都是需要更换的参数，根据自己的需要参考文档更换
            String bizDataRaw = JSONObject.toJSON(customData).toString();
            System.out.println(bizDataRaw);
            //加密得到的加密串
            String bizDataEncoded = AES.encrypt(bizDataRaw, KEY);
            System.out.println(bizDataEncoded);
            System.out.println("原始数据是：");
            System.out.println(AES.decrypt(AES.encrypt(bizDataRaw, KEY), KEY));
            // 请求接口
            Map<String, Object> data = new HashMap<>();
            // 公司id
            data.put("institution_id", COMPANY_ID);
            // biz_data参数使用AES加密后得到的加密串
            data.put("biz_data", bizDataEncoded);
            System.out.println(data);
            // 发送请求
            String result = HttpUtil.invokePostWithMap(URL, data);
            System.out.println("返回结果为：" + result);
            return result;
        } catch (Exception e) {
            log.error("接口异常", e);
            return null;
        }
    }
}
