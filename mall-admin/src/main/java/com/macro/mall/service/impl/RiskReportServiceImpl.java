package com.macro.mall.service.impl;

import cn.hutool.Hutool;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.macro.mall.common.util.RiskReportServiceUtil;
import com.macro.mall.mapper.CmsPrefrenceAreaMapper;
import com.macro.mall.mapper.TRiskReportMapper;
import com.macro.mall.model.CmsPrefrenceArea;
import com.macro.mall.model.CmsPrefrenceAreaExample;
import com.macro.mall.model.TRiskReport;
import com.macro.mall.model.TRiskReportExample;
import com.macro.mall.service.CmsPrefrenceAreaService;
import com.macro.mall.service.RiskReportService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RiskReportServiceImpl implements RiskReportService {

    private final TRiskReportMapper riskReportMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TRiskReport getRiskReport(String name, String idNumber, String phone) {
        TRiskReportExample example = new TRiskReportExample();
        example.createCriteria().andIdNumberEqualTo(idNumber).andStatusEqualTo(1);
        List<TRiskReport> tRiskReports = riskReportMapper.selectByExample(example);
        if (CollectionUtils.isNotEmpty(tRiskReports)) {
            return tRiskReports.get(0);
        }
        //如果没有则查询接口
        String riskReportStr = RiskReportServiceUtil.queryRiskReport(name, idNumber, phone);
        JSONObject riskReportJson = JSONObject.parseObject(riskReportStr);
        String respCode = riskReportJson.getString("resp_code");
        if (!"SW0000".equals(respCode)) {
            throw new RuntimeException("查询第三方接口失败");
        }
        JSONObject data = riskReportJson.getJSONObject("resp_data");
        TRiskReport riskReport = converRiskReport(data, name, idNumber, phone);
        riskReportMapper.insert(riskReport);
        return riskReport;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TRiskReport getRealTimeRiskReport(String name, String idNumber, String phone) {

        TRiskReport riskReport;
        TRiskReportExample example = new TRiskReportExample();
        example.createCriteria().andIdNumberEqualTo(idNumber).andStatusEqualTo(1);
        List<TRiskReport> tRiskReports = riskReportMapper.selectByExample(example);
        if (CollectionUtils.isNotEmpty(tRiskReports)) {
            riskReport = tRiskReports.get(0);
            //删除原来的记录
            riskReport.setStatus(0);
            riskReportMapper.updateByPrimaryKey(riskReport);
        }
        //如果没有则查询接口
        String riskReportStr = RiskReportServiceUtil.queryRiskReport(name, idNumber, phone);
        JSONObject riskReportJson = JSONObject.parseObject(riskReportStr);
        String respCode = riskReportJson.getString("resp_code");
        if (!"SW0000".equals(respCode)) {
            throw new RuntimeException("查询第三方接口失败");
        }
        JSONObject data = riskReportJson.getJSONObject("resp_data");
        riskReport = converRiskReport(data, name, idNumber, phone);
        riskReportMapper.insert(riskReport);
        return riskReport;
    }


    public TRiskReport converRiskReport(JSONObject riskReportJson, String name, String idNumber, String phone) {
        //拼接成对象
        TRiskReport riskReport = new TRiskReport();
        riskReport.setName(name);
        riskReport.setIdNumber(idNumber);
        riskReport.setStatus(1);
        riskReport.setReportCode(RandomUtil.randomNumbers(32));
        riskReport.setBaseInfo(riskReportJson.getString("base_info"));
        riskReport.setHitRiskTagging(riskReportJson.getString("hit_risk_tagging"));
        riskReport.setRelevanceRiskCheck(riskReportJson.getString("relevance_risk_check"));
        riskReport.setRiskListCheck(riskReportJson.getString("risk_list_check"));
        riskReport.setIdentInfoDict(riskReportJson.getString("ident_info_dict"));
        riskReport.setVerifyRecomment(riskReportJson.getString("verify_recomment"));
        riskReport.setScoreNormExplain(riskReportJson.getString("score_norm_explain"));
        riskReport.setRentHistory(riskReportJson.getString("rent_history"));
        riskReport.setPersonalOverdueHistory(riskReportJson.getString("personal_overdue_history"));
        riskReport.setPersonalLoanS(riskReportJson.getString("personal_loan_s"));
        riskReport.setPersonalLoanF(riskReportJson.getString("personal_loan_f"));
        riskReport.setPersonalLoanH(riskReportJson.getString("personal_loan_h"));
        riskReport.setSuggestCash(riskReportJson.getInteger("suggest_cash"));
        riskReport.setPersonalLoanDemand(riskReportJson.getString("personal_loan_demand"));
        riskReport.setCourtRiskInfoList(riskReportJson.getString("court_risk_info_list"));
        riskReport.setCreateTime(new Date());
        riskReport.setUpdateTime(new Date());
        return riskReport;
    }
}
