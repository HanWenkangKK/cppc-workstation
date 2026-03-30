package com.nanqiong.cppcaiagent.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "GenerateReportRequest", description = "生成AI报告请求")
public record GenerateReportRequest(
        @Schema(description = "评估记录ID", example = "1001")
        Long assessmentId,
        @Schema(description = "报告类型", example = "assessment_report")
        String reportType
) {
}
