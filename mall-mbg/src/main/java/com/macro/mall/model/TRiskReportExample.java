package com.macro.mall.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TRiskReportExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public TRiskReportExample() {
        oredCriteria = new ArrayList<>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Long value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Long value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Long value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Long value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Long value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Long value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Long> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Long> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Long value1, Long value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Long value1, Long value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andNameIsNull() {
            addCriterion("name is null");
            return (Criteria) this;
        }

        public Criteria andNameIsNotNull() {
            addCriterion("name is not null");
            return (Criteria) this;
        }

        public Criteria andNameEqualTo(String value) {
            addCriterion("name =", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotEqualTo(String value) {
            addCriterion("name <>", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameGreaterThan(String value) {
            addCriterion("name >", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameGreaterThanOrEqualTo(String value) {
            addCriterion("name >=", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLessThan(String value) {
            addCriterion("name <", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLessThanOrEqualTo(String value) {
            addCriterion("name <=", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLike(String value) {
            addCriterion("name like", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotLike(String value) {
            addCriterion("name not like", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameIn(List<String> values) {
            addCriterion("name in", values, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotIn(List<String> values) {
            addCriterion("name not in", values, "name");
            return (Criteria) this;
        }

        public Criteria andNameBetween(String value1, String value2) {
            addCriterion("name between", value1, value2, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotBetween(String value1, String value2) {
            addCriterion("name not between", value1, value2, "name");
            return (Criteria) this;
        }

        public Criteria andIdNumberIsNull() {
            addCriterion("id_number is null");
            return (Criteria) this;
        }

        public Criteria andIdNumberIsNotNull() {
            addCriterion("id_number is not null");
            return (Criteria) this;
        }

        public Criteria andIdNumberEqualTo(String value) {
            addCriterion("id_number =", value, "idNumber");
            return (Criteria) this;
        }

        public Criteria andIdNumberNotEqualTo(String value) {
            addCriterion("id_number <>", value, "idNumber");
            return (Criteria) this;
        }

        public Criteria andIdNumberGreaterThan(String value) {
            addCriterion("id_number >", value, "idNumber");
            return (Criteria) this;
        }

        public Criteria andIdNumberGreaterThanOrEqualTo(String value) {
            addCriterion("id_number >=", value, "idNumber");
            return (Criteria) this;
        }

        public Criteria andIdNumberLessThan(String value) {
            addCriterion("id_number <", value, "idNumber");
            return (Criteria) this;
        }

        public Criteria andIdNumberLessThanOrEqualTo(String value) {
            addCriterion("id_number <=", value, "idNumber");
            return (Criteria) this;
        }

        public Criteria andIdNumberLike(String value) {
            addCriterion("id_number like", value, "idNumber");
            return (Criteria) this;
        }

        public Criteria andIdNumberNotLike(String value) {
            addCriterion("id_number not like", value, "idNumber");
            return (Criteria) this;
        }

        public Criteria andIdNumberIn(List<String> values) {
            addCriterion("id_number in", values, "idNumber");
            return (Criteria) this;
        }

        public Criteria andIdNumberNotIn(List<String> values) {
            addCriterion("id_number not in", values, "idNumber");
            return (Criteria) this;
        }

        public Criteria andIdNumberBetween(String value1, String value2) {
            addCriterion("id_number between", value1, value2, "idNumber");
            return (Criteria) this;
        }

        public Criteria andIdNumberNotBetween(String value1, String value2) {
            addCriterion("id_number not between", value1, value2, "idNumber");
            return (Criteria) this;
        }

        public Criteria andReportCodeIsNull() {
            addCriterion("report_code is null");
            return (Criteria) this;
        }

        public Criteria andReportCodeIsNotNull() {
            addCriterion("report_code is not null");
            return (Criteria) this;
        }

        public Criteria andReportCodeEqualTo(String value) {
            addCriterion("report_code =", value, "reportCode");
            return (Criteria) this;
        }

        public Criteria andReportCodeNotEqualTo(String value) {
            addCriterion("report_code <>", value, "reportCode");
            return (Criteria) this;
        }

        public Criteria andReportCodeGreaterThan(String value) {
            addCriterion("report_code >", value, "reportCode");
            return (Criteria) this;
        }

        public Criteria andReportCodeGreaterThanOrEqualTo(String value) {
            addCriterion("report_code >=", value, "reportCode");
            return (Criteria) this;
        }

        public Criteria andReportCodeLessThan(String value) {
            addCriterion("report_code <", value, "reportCode");
            return (Criteria) this;
        }

        public Criteria andReportCodeLessThanOrEqualTo(String value) {
            addCriterion("report_code <=", value, "reportCode");
            return (Criteria) this;
        }

        public Criteria andReportCodeLike(String value) {
            addCriterion("report_code like", value, "reportCode");
            return (Criteria) this;
        }

        public Criteria andReportCodeNotLike(String value) {
            addCriterion("report_code not like", value, "reportCode");
            return (Criteria) this;
        }

        public Criteria andReportCodeIn(List<String> values) {
            addCriterion("report_code in", values, "reportCode");
            return (Criteria) this;
        }

        public Criteria andReportCodeNotIn(List<String> values) {
            addCriterion("report_code not in", values, "reportCode");
            return (Criteria) this;
        }

        public Criteria andReportCodeBetween(String value1, String value2) {
            addCriterion("report_code between", value1, value2, "reportCode");
            return (Criteria) this;
        }

        public Criteria andReportCodeNotBetween(String value1, String value2) {
            addCriterion("report_code not between", value1, value2, "reportCode");
            return (Criteria) this;
        }

        public Criteria andBaseInfoIsNull() {
            addCriterion("base_info is null");
            return (Criteria) this;
        }

        public Criteria andBaseInfoIsNotNull() {
            addCriterion("base_info is not null");
            return (Criteria) this;
        }

        public Criteria andBaseInfoEqualTo(String value) {
            addCriterion("base_info =", value, "baseInfo");
            return (Criteria) this;
        }

        public Criteria andBaseInfoNotEqualTo(String value) {
            addCriterion("base_info <>", value, "baseInfo");
            return (Criteria) this;
        }

        public Criteria andBaseInfoGreaterThan(String value) {
            addCriterion("base_info >", value, "baseInfo");
            return (Criteria) this;
        }

        public Criteria andBaseInfoGreaterThanOrEqualTo(String value) {
            addCriterion("base_info >=", value, "baseInfo");
            return (Criteria) this;
        }

        public Criteria andBaseInfoLessThan(String value) {
            addCriterion("base_info <", value, "baseInfo");
            return (Criteria) this;
        }

        public Criteria andBaseInfoLessThanOrEqualTo(String value) {
            addCriterion("base_info <=", value, "baseInfo");
            return (Criteria) this;
        }

        public Criteria andBaseInfoLike(String value) {
            addCriterion("base_info like", value, "baseInfo");
            return (Criteria) this;
        }

        public Criteria andBaseInfoNotLike(String value) {
            addCriterion("base_info not like", value, "baseInfo");
            return (Criteria) this;
        }

        public Criteria andBaseInfoIn(List<String> values) {
            addCriterion("base_info in", values, "baseInfo");
            return (Criteria) this;
        }

        public Criteria andBaseInfoNotIn(List<String> values) {
            addCriterion("base_info not in", values, "baseInfo");
            return (Criteria) this;
        }

        public Criteria andBaseInfoBetween(String value1, String value2) {
            addCriterion("base_info between", value1, value2, "baseInfo");
            return (Criteria) this;
        }

        public Criteria andBaseInfoNotBetween(String value1, String value2) {
            addCriterion("base_info not between", value1, value2, "baseInfo");
            return (Criteria) this;
        }

        public Criteria andCourtRiskInfoListIsNull() {
            addCriterion("court_risk_info_list is null");
            return (Criteria) this;
        }

        public Criteria andCourtRiskInfoListIsNotNull() {
            addCriterion("court_risk_info_list is not null");
            return (Criteria) this;
        }

        public Criteria andCourtRiskInfoListEqualTo(String value) {
            addCriterion("court_risk_info_list =", value, "courtRiskInfoList");
            return (Criteria) this;
        }

        public Criteria andCourtRiskInfoListNotEqualTo(String value) {
            addCriterion("court_risk_info_list <>", value, "courtRiskInfoList");
            return (Criteria) this;
        }

        public Criteria andCourtRiskInfoListGreaterThan(String value) {
            addCriterion("court_risk_info_list >", value, "courtRiskInfoList");
            return (Criteria) this;
        }

        public Criteria andCourtRiskInfoListGreaterThanOrEqualTo(String value) {
            addCriterion("court_risk_info_list >=", value, "courtRiskInfoList");
            return (Criteria) this;
        }

        public Criteria andCourtRiskInfoListLessThan(String value) {
            addCriterion("court_risk_info_list <", value, "courtRiskInfoList");
            return (Criteria) this;
        }

        public Criteria andCourtRiskInfoListLessThanOrEqualTo(String value) {
            addCriterion("court_risk_info_list <=", value, "courtRiskInfoList");
            return (Criteria) this;
        }

        public Criteria andCourtRiskInfoListLike(String value) {
            addCriterion("court_risk_info_list like", value, "courtRiskInfoList");
            return (Criteria) this;
        }

        public Criteria andCourtRiskInfoListNotLike(String value) {
            addCriterion("court_risk_info_list not like", value, "courtRiskInfoList");
            return (Criteria) this;
        }

        public Criteria andCourtRiskInfoListIn(List<String> values) {
            addCriterion("court_risk_info_list in", values, "courtRiskInfoList");
            return (Criteria) this;
        }

        public Criteria andCourtRiskInfoListNotIn(List<String> values) {
            addCriterion("court_risk_info_list not in", values, "courtRiskInfoList");
            return (Criteria) this;
        }

        public Criteria andCourtRiskInfoListBetween(String value1, String value2) {
            addCriterion("court_risk_info_list between", value1, value2, "courtRiskInfoList");
            return (Criteria) this;
        }

        public Criteria andCourtRiskInfoListNotBetween(String value1, String value2) {
            addCriterion("court_risk_info_list not between", value1, value2, "courtRiskInfoList");
            return (Criteria) this;
        }

        public Criteria andHitRiskTaggingIsNull() {
            addCriterion("hit_risk_tagging is null");
            return (Criteria) this;
        }

        public Criteria andHitRiskTaggingIsNotNull() {
            addCriterion("hit_risk_tagging is not null");
            return (Criteria) this;
        }

        public Criteria andHitRiskTaggingEqualTo(String value) {
            addCriterion("hit_risk_tagging =", value, "hitRiskTagging");
            return (Criteria) this;
        }

        public Criteria andHitRiskTaggingNotEqualTo(String value) {
            addCriterion("hit_risk_tagging <>", value, "hitRiskTagging");
            return (Criteria) this;
        }

        public Criteria andHitRiskTaggingGreaterThan(String value) {
            addCriterion("hit_risk_tagging >", value, "hitRiskTagging");
            return (Criteria) this;
        }

        public Criteria andHitRiskTaggingGreaterThanOrEqualTo(String value) {
            addCriterion("hit_risk_tagging >=", value, "hitRiskTagging");
            return (Criteria) this;
        }

        public Criteria andHitRiskTaggingLessThan(String value) {
            addCriterion("hit_risk_tagging <", value, "hitRiskTagging");
            return (Criteria) this;
        }

        public Criteria andHitRiskTaggingLessThanOrEqualTo(String value) {
            addCriterion("hit_risk_tagging <=", value, "hitRiskTagging");
            return (Criteria) this;
        }

        public Criteria andHitRiskTaggingLike(String value) {
            addCriterion("hit_risk_tagging like", value, "hitRiskTagging");
            return (Criteria) this;
        }

        public Criteria andHitRiskTaggingNotLike(String value) {
            addCriterion("hit_risk_tagging not like", value, "hitRiskTagging");
            return (Criteria) this;
        }

        public Criteria andHitRiskTaggingIn(List<String> values) {
            addCriterion("hit_risk_tagging in", values, "hitRiskTagging");
            return (Criteria) this;
        }

        public Criteria andHitRiskTaggingNotIn(List<String> values) {
            addCriterion("hit_risk_tagging not in", values, "hitRiskTagging");
            return (Criteria) this;
        }

        public Criteria andHitRiskTaggingBetween(String value1, String value2) {
            addCriterion("hit_risk_tagging between", value1, value2, "hitRiskTagging");
            return (Criteria) this;
        }

        public Criteria andHitRiskTaggingNotBetween(String value1, String value2) {
            addCriterion("hit_risk_tagging not between", value1, value2, "hitRiskTagging");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanDemandIsNull() {
            addCriterion("personal_loan_demand is null");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanDemandIsNotNull() {
            addCriterion("personal_loan_demand is not null");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanDemandEqualTo(String value) {
            addCriterion("personal_loan_demand =", value, "personalLoanDemand");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanDemandNotEqualTo(String value) {
            addCriterion("personal_loan_demand <>", value, "personalLoanDemand");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanDemandGreaterThan(String value) {
            addCriterion("personal_loan_demand >", value, "personalLoanDemand");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanDemandGreaterThanOrEqualTo(String value) {
            addCriterion("personal_loan_demand >=", value, "personalLoanDemand");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanDemandLessThan(String value) {
            addCriterion("personal_loan_demand <", value, "personalLoanDemand");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanDemandLessThanOrEqualTo(String value) {
            addCriterion("personal_loan_demand <=", value, "personalLoanDemand");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanDemandLike(String value) {
            addCriterion("personal_loan_demand like", value, "personalLoanDemand");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanDemandNotLike(String value) {
            addCriterion("personal_loan_demand not like", value, "personalLoanDemand");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanDemandIn(List<String> values) {
            addCriterion("personal_loan_demand in", values, "personalLoanDemand");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanDemandNotIn(List<String> values) {
            addCriterion("personal_loan_demand not in", values, "personalLoanDemand");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanDemandBetween(String value1, String value2) {
            addCriterion("personal_loan_demand between", value1, value2, "personalLoanDemand");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanDemandNotBetween(String value1, String value2) {
            addCriterion("personal_loan_demand not between", value1, value2, "personalLoanDemand");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanFIsNull() {
            addCriterion("personal_loan_f is null");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanFIsNotNull() {
            addCriterion("personal_loan_f is not null");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanFEqualTo(String value) {
            addCriterion("personal_loan_f =", value, "personalLoanF");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanFNotEqualTo(String value) {
            addCriterion("personal_loan_f <>", value, "personalLoanF");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanFGreaterThan(String value) {
            addCriterion("personal_loan_f >", value, "personalLoanF");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanFGreaterThanOrEqualTo(String value) {
            addCriterion("personal_loan_f >=", value, "personalLoanF");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanFLessThan(String value) {
            addCriterion("personal_loan_f <", value, "personalLoanF");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanFLessThanOrEqualTo(String value) {
            addCriterion("personal_loan_f <=", value, "personalLoanF");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanFLike(String value) {
            addCriterion("personal_loan_f like", value, "personalLoanF");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanFNotLike(String value) {
            addCriterion("personal_loan_f not like", value, "personalLoanF");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanFIn(List<String> values) {
            addCriterion("personal_loan_f in", values, "personalLoanF");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanFNotIn(List<String> values) {
            addCriterion("personal_loan_f not in", values, "personalLoanF");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanFBetween(String value1, String value2) {
            addCriterion("personal_loan_f between", value1, value2, "personalLoanF");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanFNotBetween(String value1, String value2) {
            addCriterion("personal_loan_f not between", value1, value2, "personalLoanF");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanSIsNull() {
            addCriterion("personal_loan_s is null");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanSIsNotNull() {
            addCriterion("personal_loan_s is not null");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanSEqualTo(String value) {
            addCriterion("personal_loan_s =", value, "personalLoanS");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanSNotEqualTo(String value) {
            addCriterion("personal_loan_s <>", value, "personalLoanS");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanSGreaterThan(String value) {
            addCriterion("personal_loan_s >", value, "personalLoanS");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanSGreaterThanOrEqualTo(String value) {
            addCriterion("personal_loan_s >=", value, "personalLoanS");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanSLessThan(String value) {
            addCriterion("personal_loan_s <", value, "personalLoanS");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanSLessThanOrEqualTo(String value) {
            addCriterion("personal_loan_s <=", value, "personalLoanS");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanSLike(String value) {
            addCriterion("personal_loan_s like", value, "personalLoanS");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanSNotLike(String value) {
            addCriterion("personal_loan_s not like", value, "personalLoanS");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanSIn(List<String> values) {
            addCriterion("personal_loan_s in", values, "personalLoanS");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanSNotIn(List<String> values) {
            addCriterion("personal_loan_s not in", values, "personalLoanS");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanSBetween(String value1, String value2) {
            addCriterion("personal_loan_s between", value1, value2, "personalLoanS");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanSNotBetween(String value1, String value2) {
            addCriterion("personal_loan_s not between", value1, value2, "personalLoanS");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanHIsNull() {
            addCriterion("personal_loan_h is null");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanHIsNotNull() {
            addCriterion("personal_loan_h is not null");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanHEqualTo(String value) {
            addCriterion("personal_loan_h =", value, "personalLoanH");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanHNotEqualTo(String value) {
            addCriterion("personal_loan_h <>", value, "personalLoanH");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanHGreaterThan(String value) {
            addCriterion("personal_loan_h >", value, "personalLoanH");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanHGreaterThanOrEqualTo(String value) {
            addCriterion("personal_loan_h >=", value, "personalLoanH");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanHLessThan(String value) {
            addCriterion("personal_loan_h <", value, "personalLoanH");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanHLessThanOrEqualTo(String value) {
            addCriterion("personal_loan_h <=", value, "personalLoanH");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanHLike(String value) {
            addCriterion("personal_loan_h like", value, "personalLoanH");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanHNotLike(String value) {
            addCriterion("personal_loan_h not like", value, "personalLoanH");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanHIn(List<String> values) {
            addCriterion("personal_loan_h in", values, "personalLoanH");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanHNotIn(List<String> values) {
            addCriterion("personal_loan_h not in", values, "personalLoanH");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanHBetween(String value1, String value2) {
            addCriterion("personal_loan_h between", value1, value2, "personalLoanH");
            return (Criteria) this;
        }

        public Criteria andPersonalLoanHNotBetween(String value1, String value2) {
            addCriterion("personal_loan_h not between", value1, value2, "personalLoanH");
            return (Criteria) this;
        }

        public Criteria andPersonalOverdueHistoryIsNull() {
            addCriterion("personal_overdue_history is null");
            return (Criteria) this;
        }

        public Criteria andPersonalOverdueHistoryIsNotNull() {
            addCriterion("personal_overdue_history is not null");
            return (Criteria) this;
        }

        public Criteria andPersonalOverdueHistoryEqualTo(String value) {
            addCriterion("personal_overdue_history =", value, "personalOverdueHistory");
            return (Criteria) this;
        }

        public Criteria andPersonalOverdueHistoryNotEqualTo(String value) {
            addCriterion("personal_overdue_history <>", value, "personalOverdueHistory");
            return (Criteria) this;
        }

        public Criteria andPersonalOverdueHistoryGreaterThan(String value) {
            addCriterion("personal_overdue_history >", value, "personalOverdueHistory");
            return (Criteria) this;
        }

        public Criteria andPersonalOverdueHistoryGreaterThanOrEqualTo(String value) {
            addCriterion("personal_overdue_history >=", value, "personalOverdueHistory");
            return (Criteria) this;
        }

        public Criteria andPersonalOverdueHistoryLessThan(String value) {
            addCriterion("personal_overdue_history <", value, "personalOverdueHistory");
            return (Criteria) this;
        }

        public Criteria andPersonalOverdueHistoryLessThanOrEqualTo(String value) {
            addCriterion("personal_overdue_history <=", value, "personalOverdueHistory");
            return (Criteria) this;
        }

        public Criteria andPersonalOverdueHistoryLike(String value) {
            addCriterion("personal_overdue_history like", value, "personalOverdueHistory");
            return (Criteria) this;
        }

        public Criteria andPersonalOverdueHistoryNotLike(String value) {
            addCriterion("personal_overdue_history not like", value, "personalOverdueHistory");
            return (Criteria) this;
        }

        public Criteria andPersonalOverdueHistoryIn(List<String> values) {
            addCriterion("personal_overdue_history in", values, "personalOverdueHistory");
            return (Criteria) this;
        }

        public Criteria andPersonalOverdueHistoryNotIn(List<String> values) {
            addCriterion("personal_overdue_history not in", values, "personalOverdueHistory");
            return (Criteria) this;
        }

        public Criteria andPersonalOverdueHistoryBetween(String value1, String value2) {
            addCriterion("personal_overdue_history between", value1, value2, "personalOverdueHistory");
            return (Criteria) this;
        }

        public Criteria andPersonalOverdueHistoryNotBetween(String value1, String value2) {
            addCriterion("personal_overdue_history not between", value1, value2, "personalOverdueHistory");
            return (Criteria) this;
        }

        public Criteria andRelevanceRiskCheckIsNull() {
            addCriterion("relevance_risk_check is null");
            return (Criteria) this;
        }

        public Criteria andRelevanceRiskCheckIsNotNull() {
            addCriterion("relevance_risk_check is not null");
            return (Criteria) this;
        }

        public Criteria andRelevanceRiskCheckEqualTo(String value) {
            addCriterion("relevance_risk_check =", value, "relevanceRiskCheck");
            return (Criteria) this;
        }

        public Criteria andRelevanceRiskCheckNotEqualTo(String value) {
            addCriterion("relevance_risk_check <>", value, "relevanceRiskCheck");
            return (Criteria) this;
        }

        public Criteria andRelevanceRiskCheckGreaterThan(String value) {
            addCriterion("relevance_risk_check >", value, "relevanceRiskCheck");
            return (Criteria) this;
        }

        public Criteria andRelevanceRiskCheckGreaterThanOrEqualTo(String value) {
            addCriterion("relevance_risk_check >=", value, "relevanceRiskCheck");
            return (Criteria) this;
        }

        public Criteria andRelevanceRiskCheckLessThan(String value) {
            addCriterion("relevance_risk_check <", value, "relevanceRiskCheck");
            return (Criteria) this;
        }

        public Criteria andRelevanceRiskCheckLessThanOrEqualTo(String value) {
            addCriterion("relevance_risk_check <=", value, "relevanceRiskCheck");
            return (Criteria) this;
        }

        public Criteria andRelevanceRiskCheckLike(String value) {
            addCriterion("relevance_risk_check like", value, "relevanceRiskCheck");
            return (Criteria) this;
        }

        public Criteria andRelevanceRiskCheckNotLike(String value) {
            addCriterion("relevance_risk_check not like", value, "relevanceRiskCheck");
            return (Criteria) this;
        }

        public Criteria andRelevanceRiskCheckIn(List<String> values) {
            addCriterion("relevance_risk_check in", values, "relevanceRiskCheck");
            return (Criteria) this;
        }

        public Criteria andRelevanceRiskCheckNotIn(List<String> values) {
            addCriterion("relevance_risk_check not in", values, "relevanceRiskCheck");
            return (Criteria) this;
        }

        public Criteria andRelevanceRiskCheckBetween(String value1, String value2) {
            addCriterion("relevance_risk_check between", value1, value2, "relevanceRiskCheck");
            return (Criteria) this;
        }

        public Criteria andRelevanceRiskCheckNotBetween(String value1, String value2) {
            addCriterion("relevance_risk_check not between", value1, value2, "relevanceRiskCheck");
            return (Criteria) this;
        }

        public Criteria andRiskListCheckIsNull() {
            addCriterion("risk_list_check is null");
            return (Criteria) this;
        }

        public Criteria andRiskListCheckIsNotNull() {
            addCriterion("risk_list_check is not null");
            return (Criteria) this;
        }

        public Criteria andRiskListCheckEqualTo(String value) {
            addCriterion("risk_list_check =", value, "riskListCheck");
            return (Criteria) this;
        }

        public Criteria andRiskListCheckNotEqualTo(String value) {
            addCriterion("risk_list_check <>", value, "riskListCheck");
            return (Criteria) this;
        }

        public Criteria andRiskListCheckGreaterThan(String value) {
            addCriterion("risk_list_check >", value, "riskListCheck");
            return (Criteria) this;
        }

        public Criteria andRiskListCheckGreaterThanOrEqualTo(String value) {
            addCriterion("risk_list_check >=", value, "riskListCheck");
            return (Criteria) this;
        }

        public Criteria andRiskListCheckLessThan(String value) {
            addCriterion("risk_list_check <", value, "riskListCheck");
            return (Criteria) this;
        }

        public Criteria andRiskListCheckLessThanOrEqualTo(String value) {
            addCriterion("risk_list_check <=", value, "riskListCheck");
            return (Criteria) this;
        }

        public Criteria andRiskListCheckLike(String value) {
            addCriterion("risk_list_check like", value, "riskListCheck");
            return (Criteria) this;
        }

        public Criteria andRiskListCheckNotLike(String value) {
            addCriterion("risk_list_check not like", value, "riskListCheck");
            return (Criteria) this;
        }

        public Criteria andRiskListCheckIn(List<String> values) {
            addCriterion("risk_list_check in", values, "riskListCheck");
            return (Criteria) this;
        }

        public Criteria andRiskListCheckNotIn(List<String> values) {
            addCriterion("risk_list_check not in", values, "riskListCheck");
            return (Criteria) this;
        }

        public Criteria andRiskListCheckBetween(String value1, String value2) {
            addCriterion("risk_list_check between", value1, value2, "riskListCheck");
            return (Criteria) this;
        }

        public Criteria andRiskListCheckNotBetween(String value1, String value2) {
            addCriterion("risk_list_check not between", value1, value2, "riskListCheck");
            return (Criteria) this;
        }

        public Criteria andRentHistoryIsNull() {
            addCriterion("rent_history is null");
            return (Criteria) this;
        }

        public Criteria andRentHistoryIsNotNull() {
            addCriterion("rent_history is not null");
            return (Criteria) this;
        }

        public Criteria andRentHistoryEqualTo(String value) {
            addCriterion("rent_history =", value, "rentHistory");
            return (Criteria) this;
        }

        public Criteria andRentHistoryNotEqualTo(String value) {
            addCriterion("rent_history <>", value, "rentHistory");
            return (Criteria) this;
        }

        public Criteria andRentHistoryGreaterThan(String value) {
            addCriterion("rent_history >", value, "rentHistory");
            return (Criteria) this;
        }

        public Criteria andRentHistoryGreaterThanOrEqualTo(String value) {
            addCriterion("rent_history >=", value, "rentHistory");
            return (Criteria) this;
        }

        public Criteria andRentHistoryLessThan(String value) {
            addCriterion("rent_history <", value, "rentHistory");
            return (Criteria) this;
        }

        public Criteria andRentHistoryLessThanOrEqualTo(String value) {
            addCriterion("rent_history <=", value, "rentHistory");
            return (Criteria) this;
        }

        public Criteria andRentHistoryLike(String value) {
            addCriterion("rent_history like", value, "rentHistory");
            return (Criteria) this;
        }

        public Criteria andRentHistoryNotLike(String value) {
            addCriterion("rent_history not like", value, "rentHistory");
            return (Criteria) this;
        }

        public Criteria andRentHistoryIn(List<String> values) {
            addCriterion("rent_history in", values, "rentHistory");
            return (Criteria) this;
        }

        public Criteria andRentHistoryNotIn(List<String> values) {
            addCriterion("rent_history not in", values, "rentHistory");
            return (Criteria) this;
        }

        public Criteria andRentHistoryBetween(String value1, String value2) {
            addCriterion("rent_history between", value1, value2, "rentHistory");
            return (Criteria) this;
        }

        public Criteria andRentHistoryNotBetween(String value1, String value2) {
            addCriterion("rent_history not between", value1, value2, "rentHistory");
            return (Criteria) this;
        }

        public Criteria andIdentInfoDictIsNull() {
            addCriterion("ident_info_dict is null");
            return (Criteria) this;
        }

        public Criteria andIdentInfoDictIsNotNull() {
            addCriterion("ident_info_dict is not null");
            return (Criteria) this;
        }

        public Criteria andIdentInfoDictEqualTo(String value) {
            addCriterion("ident_info_dict =", value, "identInfoDict");
            return (Criteria) this;
        }

        public Criteria andIdentInfoDictNotEqualTo(String value) {
            addCriterion("ident_info_dict <>", value, "identInfoDict");
            return (Criteria) this;
        }

        public Criteria andIdentInfoDictGreaterThan(String value) {
            addCriterion("ident_info_dict >", value, "identInfoDict");
            return (Criteria) this;
        }

        public Criteria andIdentInfoDictGreaterThanOrEqualTo(String value) {
            addCriterion("ident_info_dict >=", value, "identInfoDict");
            return (Criteria) this;
        }

        public Criteria andIdentInfoDictLessThan(String value) {
            addCriterion("ident_info_dict <", value, "identInfoDict");
            return (Criteria) this;
        }

        public Criteria andIdentInfoDictLessThanOrEqualTo(String value) {
            addCriterion("ident_info_dict <=", value, "identInfoDict");
            return (Criteria) this;
        }

        public Criteria andIdentInfoDictLike(String value) {
            addCriterion("ident_info_dict like", value, "identInfoDict");
            return (Criteria) this;
        }

        public Criteria andIdentInfoDictNotLike(String value) {
            addCriterion("ident_info_dict not like", value, "identInfoDict");
            return (Criteria) this;
        }

        public Criteria andIdentInfoDictIn(List<String> values) {
            addCriterion("ident_info_dict in", values, "identInfoDict");
            return (Criteria) this;
        }

        public Criteria andIdentInfoDictNotIn(List<String> values) {
            addCriterion("ident_info_dict not in", values, "identInfoDict");
            return (Criteria) this;
        }

        public Criteria andIdentInfoDictBetween(String value1, String value2) {
            addCriterion("ident_info_dict between", value1, value2, "identInfoDict");
            return (Criteria) this;
        }

        public Criteria andIdentInfoDictNotBetween(String value1, String value2) {
            addCriterion("ident_info_dict not between", value1, value2, "identInfoDict");
            return (Criteria) this;
        }

        public Criteria andVerifyRecommentIsNull() {
            addCriterion("verify_recomment is null");
            return (Criteria) this;
        }

        public Criteria andVerifyRecommentIsNotNull() {
            addCriterion("verify_recomment is not null");
            return (Criteria) this;
        }

        public Criteria andVerifyRecommentEqualTo(String value) {
            addCriterion("verify_recomment =", value, "verifyRecomment");
            return (Criteria) this;
        }

        public Criteria andVerifyRecommentNotEqualTo(String value) {
            addCriterion("verify_recomment <>", value, "verifyRecomment");
            return (Criteria) this;
        }

        public Criteria andVerifyRecommentGreaterThan(String value) {
            addCriterion("verify_recomment >", value, "verifyRecomment");
            return (Criteria) this;
        }

        public Criteria andVerifyRecommentGreaterThanOrEqualTo(String value) {
            addCriterion("verify_recomment >=", value, "verifyRecomment");
            return (Criteria) this;
        }

        public Criteria andVerifyRecommentLessThan(String value) {
            addCriterion("verify_recomment <", value, "verifyRecomment");
            return (Criteria) this;
        }

        public Criteria andVerifyRecommentLessThanOrEqualTo(String value) {
            addCriterion("verify_recomment <=", value, "verifyRecomment");
            return (Criteria) this;
        }

        public Criteria andVerifyRecommentLike(String value) {
            addCriterion("verify_recomment like", value, "verifyRecomment");
            return (Criteria) this;
        }

        public Criteria andVerifyRecommentNotLike(String value) {
            addCriterion("verify_recomment not like", value, "verifyRecomment");
            return (Criteria) this;
        }

        public Criteria andVerifyRecommentIn(List<String> values) {
            addCriterion("verify_recomment in", values, "verifyRecomment");
            return (Criteria) this;
        }

        public Criteria andVerifyRecommentNotIn(List<String> values) {
            addCriterion("verify_recomment not in", values, "verifyRecomment");
            return (Criteria) this;
        }

        public Criteria andVerifyRecommentBetween(String value1, String value2) {
            addCriterion("verify_recomment between", value1, value2, "verifyRecomment");
            return (Criteria) this;
        }

        public Criteria andVerifyRecommentNotBetween(String value1, String value2) {
            addCriterion("verify_recomment not between", value1, value2, "verifyRecomment");
            return (Criteria) this;
        }

        public Criteria andScoreNormExplainIsNull() {
            addCriterion("score_norm_explain is null");
            return (Criteria) this;
        }

        public Criteria andScoreNormExplainIsNotNull() {
            addCriterion("score_norm_explain is not null");
            return (Criteria) this;
        }

        public Criteria andScoreNormExplainEqualTo(String value) {
            addCriterion("score_norm_explain =", value, "scoreNormExplain");
            return (Criteria) this;
        }

        public Criteria andScoreNormExplainNotEqualTo(String value) {
            addCriterion("score_norm_explain <>", value, "scoreNormExplain");
            return (Criteria) this;
        }

        public Criteria andScoreNormExplainGreaterThan(String value) {
            addCriterion("score_norm_explain >", value, "scoreNormExplain");
            return (Criteria) this;
        }

        public Criteria andScoreNormExplainGreaterThanOrEqualTo(String value) {
            addCriterion("score_norm_explain >=", value, "scoreNormExplain");
            return (Criteria) this;
        }

        public Criteria andScoreNormExplainLessThan(String value) {
            addCriterion("score_norm_explain <", value, "scoreNormExplain");
            return (Criteria) this;
        }

        public Criteria andScoreNormExplainLessThanOrEqualTo(String value) {
            addCriterion("score_norm_explain <=", value, "scoreNormExplain");
            return (Criteria) this;
        }

        public Criteria andScoreNormExplainLike(String value) {
            addCriterion("score_norm_explain like", value, "scoreNormExplain");
            return (Criteria) this;
        }

        public Criteria andScoreNormExplainNotLike(String value) {
            addCriterion("score_norm_explain not like", value, "scoreNormExplain");
            return (Criteria) this;
        }

        public Criteria andScoreNormExplainIn(List<String> values) {
            addCriterion("score_norm_explain in", values, "scoreNormExplain");
            return (Criteria) this;
        }

        public Criteria andScoreNormExplainNotIn(List<String> values) {
            addCriterion("score_norm_explain not in", values, "scoreNormExplain");
            return (Criteria) this;
        }

        public Criteria andScoreNormExplainBetween(String value1, String value2) {
            addCriterion("score_norm_explain between", value1, value2, "scoreNormExplain");
            return (Criteria) this;
        }

        public Criteria andScoreNormExplainNotBetween(String value1, String value2) {
            addCriterion("score_norm_explain not between", value1, value2, "scoreNormExplain");
            return (Criteria) this;
        }

        public Criteria andSuggestCashIsNull() {
            addCriterion("suggest_cash is null");
            return (Criteria) this;
        }

        public Criteria andSuggestCashIsNotNull() {
            addCriterion("suggest_cash is not null");
            return (Criteria) this;
        }

        public Criteria andSuggestCashEqualTo(Integer value) {
            addCriterion("suggest_cash =", value, "suggestCash");
            return (Criteria) this;
        }

        public Criteria andSuggestCashNotEqualTo(Integer value) {
            addCriterion("suggest_cash <>", value, "suggestCash");
            return (Criteria) this;
        }

        public Criteria andSuggestCashGreaterThan(Integer value) {
            addCriterion("suggest_cash >", value, "suggestCash");
            return (Criteria) this;
        }

        public Criteria andSuggestCashGreaterThanOrEqualTo(Integer value) {
            addCriterion("suggest_cash >=", value, "suggestCash");
            return (Criteria) this;
        }

        public Criteria andSuggestCashLessThan(Integer value) {
            addCriterion("suggest_cash <", value, "suggestCash");
            return (Criteria) this;
        }

        public Criteria andSuggestCashLessThanOrEqualTo(Integer value) {
            addCriterion("suggest_cash <=", value, "suggestCash");
            return (Criteria) this;
        }

        public Criteria andSuggestCashIn(List<Integer> values) {
            addCriterion("suggest_cash in", values, "suggestCash");
            return (Criteria) this;
        }

        public Criteria andSuggestCashNotIn(List<Integer> values) {
            addCriterion("suggest_cash not in", values, "suggestCash");
            return (Criteria) this;
        }

        public Criteria andSuggestCashBetween(Integer value1, Integer value2) {
            addCriterion("suggest_cash between", value1, value2, "suggestCash");
            return (Criteria) this;
        }

        public Criteria andSuggestCashNotBetween(Integer value1, Integer value2) {
            addCriterion("suggest_cash not between", value1, value2, "suggestCash");
            return (Criteria) this;
        }

        public Criteria andStatusIsNull() {
            addCriterion("status is null");
            return (Criteria) this;
        }

        public Criteria andStatusIsNotNull() {
            addCriterion("status is not null");
            return (Criteria) this;
        }

        public Criteria andStatusEqualTo(Integer value) {
            addCriterion("status =", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotEqualTo(Integer value) {
            addCriterion("status <>", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThan(Integer value) {
            addCriterion("status >", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("status >=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThan(Integer value) {
            addCriterion("status <", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThanOrEqualTo(Integer value) {
            addCriterion("status <=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusIn(List<Integer> values) {
            addCriterion("status in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotIn(List<Integer> values) {
            addCriterion("status not in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusBetween(Integer value1, Integer value2) {
            addCriterion("status between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("status not between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNull() {
            addCriterion("create_time is null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNotNull() {
            addCriterion("create_time is not null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeEqualTo(Date value) {
            addCriterion("create_time =", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotEqualTo(Date value) {
            addCriterion("create_time <>", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThan(Date value) {
            addCriterion("create_time >", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("create_time >=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThan(Date value) {
            addCriterion("create_time <", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThanOrEqualTo(Date value) {
            addCriterion("create_time <=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIn(List<Date> values) {
            addCriterion("create_time in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotIn(List<Date> values) {
            addCriterion("create_time not in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeBetween(Date value1, Date value2) {
            addCriterion("create_time between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotBetween(Date value1, Date value2) {
            addCriterion("create_time not between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNull() {
            addCriterion("update_time is null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNotNull() {
            addCriterion("update_time is not null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeEqualTo(Date value) {
            addCriterion("update_time =", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotEqualTo(Date value) {
            addCriterion("update_time <>", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThan(Date value) {
            addCriterion("update_time >", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("update_time >=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThan(Date value) {
            addCriterion("update_time <", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThanOrEqualTo(Date value) {
            addCriterion("update_time <=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIn(List<Date> values) {
            addCriterion("update_time in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotIn(List<Date> values) {
            addCriterion("update_time not in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeBetween(Date value1, Date value2) {
            addCriterion("update_time between", value1, value2, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotBetween(Date value1, Date value2) {
            addCriterion("update_time not between", value1, value2, "updateTime");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {
        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}