package com.nanqiong.cppcaiagent.controller;

import com.nanqiong.cppcaiagent.dto.GenerateReportRequest;
import com.nanqiong.cppcaiagent.dto.GenerateReportResponse;
import com.nanqiong.cppcaiagent.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "AI 报告")
@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @Operation(summary = "生成 AI 报告", description = "根据评估记录生成结构化 AI 报告并写入 report_record")
    @PostMapping("/generate")
    public GenerateReportResponse generate(@RequestBody GenerateReportRequest request) {
        return reportService.generate(request);
    }
}
