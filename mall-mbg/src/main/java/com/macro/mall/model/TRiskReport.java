package com.macro.mall.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;

public class TRiskReport implements Serializable {
    private Long id;

    private String name;

    private String idNumber;

    @ApiModelProperty(value = "报告编码")
    private String reportCode;

    @ApiModelProperty(value = "基本信息")
    private String baseInfo;

    @ApiModelProperty(value = "法院风险信息")
    private String courtRiskInfoList;

    @ApiModelProperty(value = "命中风险标注")
    private String hitRiskTagging;

    @ApiModelProperty(value = "近期贷款需求")
    private String personalLoanDemand;

    @ApiModelProperty(value = "贷款放款信息 ")
    private String personalLoanF;

    @ApiModelProperty(value = "贷款申请信息")
    private String personalLoanS;

    @ApiModelProperty(value = "贷款还款信息")
    private String personalLoanH;

    @ApiModelProperty(value = "历史逾期记录")
    private String personalOverdueHistory;

    @ApiModelProperty(value = "关联风险检测")
    private String relevanceRiskCheck;

    @ApiModelProperty(value = "风险名单检测")
    private String riskListCheck;

    @ApiModelProperty(value = "历史租赁印记")
    private String rentHistory;

    @ApiModelProperty(value = "身份信息核验字段")
    private String identInfoDict;

    @ApiModelProperty(value = "审核建议")
    private String verifyRecomment;

    @ApiModelProperty(value = "分值在【0，650】之间，得分越低，风险越高：【0，450】建议拒绝；（450，530】建议审核；（530，650】，建议通过。")
    private String scoreNormExplain;

    @ApiModelProperty(value = "押金建议")
    private Integer suggestCash;

    private Integer status;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getReportCode() {
        return reportCode;
    }

    public void setReportCode(String reportCode) {
        this.reportCode = reportCode;
    }

    public String getBaseInfo() {
        return baseInfo;
    }

    public void setBaseInfo(String baseInfo) {
        this.baseInfo = baseInfo;
    }

    public String getCourtRiskInfoList() {
        return courtRiskInfoList;
    }

    public void setCourtRiskInfoList(String courtRiskInfoList) {
        this.courtRiskInfoList = courtRiskInfoList;
    }

    public String getHitRiskTagging() {
        return hitRiskTagging;
    }

    public void setHitRiskTagging(String hitRiskTagging) {
        this.hitRiskTagging = hitRiskTagging;
    }

    public String getPersonalLoanDemand() {
        return personalLoanDemand;
    }

    public void setPersonalLoanDemand(String personalLoanDemand) {
        this.personalLoanDemand = personalLoanDemand;
    }

    public String getPersonalLoanF() {
        return personalLoanF;
    }

    public void setPersonalLoanF(String personalLoanF) {
        this.personalLoanF = personalLoanF;
    }

    public String getPersonalLoanS() {
        return personalLoanS;
    }

    public void setPersonalLoanS(String personalLoanS) {
        this.personalLoanS = personalLoanS;
    }

    public String getPersonalLoanH() {
        return personalLoanH;
    }

    public void setPersonalLoanH(String personalLoanH) {
        this.personalLoanH = personalLoanH;
    }

    public String getPersonalOverdueHistory() {
        return personalOverdueHistory;
    }

    public void setPersonalOverdueHistory(String personalOverdueHistory) {
        this.personalOverdueHistory = personalOverdueHistory;
    }

    public String getRelevanceRiskCheck() {
        return relevanceRiskCheck;
    }

    public void setRelevanceRiskCheck(String relevanceRiskCheck) {
        this.relevanceRiskCheck = relevanceRiskCheck;
    }

    public String getRiskListCheck() {
        return riskListCheck;
    }

    public void setRiskListCheck(String riskListCheck) {
        this.riskListCheck = riskListCheck;
    }

    public String getRentHistory() {
        return rentHistory;
    }

    public void setRentHistory(String rentHistory) {
        this.rentHistory = rentHistory;
    }

    public String getIdentInfoDict() {
        return identInfoDict;
    }

    public void setIdentInfoDict(String identInfoDict) {
        this.identInfoDict = identInfoDict;
    }

    public String getVerifyRecomment() {
        return verifyRecomment;
    }

    public void setVerifyRecomment(String verifyRecomment) {
        this.verifyRecomment = verifyRecomment;
    }

    public String getScoreNormExplain() {
        return scoreNormExplain;
    }

    public void setScoreNormExplain(String scoreNormExplain) {
        this.scoreNormExplain = scoreNormExplain;
    }

    public Integer getSuggestCash() {
        return suggestCash;
    }

    public void setSuggestCash(Integer suggestCash) {
        this.suggestCash = suggestCash;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", name=").append(name);
        sb.append(", idNumber=").append(idNumber);
        sb.append(", reportCode=").append(reportCode);
        sb.append(", baseInfo=").append(baseInfo);
        sb.append(", courtRiskInfoList=").append(courtRiskInfoList);
        sb.append(", hitRiskTagging=").append(hitRiskTagging);
        sb.append(", personalLoanDemand=").append(personalLoanDemand);
        sb.append(", personalLoanF=").append(personalLoanF);
        sb.append(", personalLoanS=").append(personalLoanS);
        sb.append(", personalLoanH=").append(personalLoanH);
        sb.append(", personalOverdueHistory=").append(personalOverdueHistory);
        sb.append(", relevanceRiskCheck=").append(relevanceRiskCheck);
        sb.append(", riskListCheck=").append(riskListCheck);
        sb.append(", rentHistory=").append(rentHistory);
        sb.append(", identInfoDict=").append(identInfoDict);
        sb.append(", verifyRecomment=").append(verifyRecomment);
        sb.append(", scoreNormExplain=").append(scoreNormExplain);
        sb.append(", suggestCash=").append(suggestCash);
        sb.append(", status=").append(status);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
