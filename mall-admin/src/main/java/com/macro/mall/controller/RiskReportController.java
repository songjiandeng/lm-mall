package com.macro.mall.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.PmsSkuStock;
import com.macro.mall.model.TRiskReport;
import com.macro.mall.service.RiskReportService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author songjd
 * @description: 风险报告控制器
 * @date 2023/9/8
 */
@RestController
@ApiOperation("风险报告相关")
@RequestMapping("riskReport")
@RequiredArgsConstructor
public class RiskReportController {

    private final RiskReportService riskReportService;

    @ApiOperation("查询风险报告")
    @GetMapping("detail")
    public CommonResult getRiskReport(@RequestParam(value = "name") String name, @RequestParam(value = "idNumber") String idNumber,
                                      @RequestParam(value = "phone") String phone) {

        TRiskReport riskReport = riskReportService.getRiskReport(name, idNumber, phone);
        return CommonResult.success(riskReport);
    }

    @ApiOperation("实时查询风险报告")
    @GetMapping("realTimeDetail")
    public CommonResult getRealTimeRiskReport(@RequestParam(value = "name") String name, @RequestParam(value = "idNumber") String idNumber,
                                              @RequestParam(value = "phone") String phone) {

        TRiskReport riskReport = riskReportService.getRealTimeRiskReport(name, idNumber, phone);
        return CommonResult.success(riskReport);
    }
}
