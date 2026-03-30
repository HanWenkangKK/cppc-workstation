package com.nanqiong.cppcaiagent.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "GenerateReportResponse", description = "生成AI报告响应")
public record GenerateReportResponse(
        @Schema(description = "报告ID", example = "5001")
        Long reportId,
        @Schema(description = "评估记录ID", example = "1001")
        Long assessmentId,
        @Schema(description = "患者ID", example = "1")
        Long patientId,
        @Schema(description = "报告类型", example = "assessment_report")
        String reportType,
        @Schema(description = "报告状态", example = "success")
        String status,
        @Schema(description = "是否人工修改", example = "false")
        boolean isModified,
        @ArraySchema(schema = @Schema(implementation = ReportContentBlock.class))
        List<ReportContentBlock> content,
        @Schema(description = "创建时间", example = "2026-03-31 14:20:00")
        String createdAt
) {
}
