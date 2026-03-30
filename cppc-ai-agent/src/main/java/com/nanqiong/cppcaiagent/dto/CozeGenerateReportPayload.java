package com.nanqiong.cppcaiagent.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "CozeGenerateReportPayload", description = "后端请求 Coze 生成报告时组装的内部载荷")
public record CozeGenerateReportPayload(
        @Schema(description = "患者信息")
        Patient patient,
        @Schema(description = "评估信息")
        Assessment assessment,
        @Schema(description = "报告类型", example = "assessment_report")
        String reportType
) {

    @Schema(name = "CozeGenerateReportPayloadPatient", description = "Coze 请求中的患者信息")
    public record Patient(
            @Schema(description = "患者ID", example = "1")
            Long id,
            @Schema(description = "姓名", example = "张三")
            String name,
            @Schema(description = "年龄", example = "58")
            Integer age,
            @Schema(description = "性别", example = "male")
            String gender,
            @Schema(description = "病种", example = "脑出血")
            String diseaseType,
            @Schema(description = "病灶侧别", example = "右侧")
            String lesionSide
    ) {
    }

    @Schema(name = "CozeGenerateReportPayloadAssessment", description = "Coze 请求中的评估信息")
    public record Assessment(
            @Schema(description = "评估记录ID", example = "1001")
            Long id,
            @Schema(description = "主诉", example = "右侧肢体活动差")
            String chiefComplaint,
            @Schema(description = "补充说明", example = "伴足内翻，步态不稳")
            String extraText,
            @Schema(description = "勾选标签ID列表")
            List<Long> tagIds
    ) {
    }
}
