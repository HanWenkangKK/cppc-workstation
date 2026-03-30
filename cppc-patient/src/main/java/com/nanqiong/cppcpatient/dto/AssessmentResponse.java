package com.nanqiong.cppcpatient.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "AssessmentResponse", description = "评估记录响应")
public record AssessmentResponse(
        @Schema(description = "评估记录ID", example = "1001")
        Long id,
        @Schema(description = "患者ID", example = "1")
        Long patientId,
        @Schema(description = "主诉", example = "右侧肢体活动差")
        String chiefComplaint,
        @Schema(description = "补充文本", example = "伴足内翻，步态不稳，治疗师怀疑存在肌张力异常")
        String extraText,
        @ArraySchema(schema = @Schema(description = "图片地址", example = "https://mock.example.com/images/1.jpg"))
        List<String> imageUrls,
        @Schema(description = "状态", example = "draft")
        String status,
        @Schema(description = "创建时间", example = "2026-03-31 14:05:00")
        String createdAt
) {
}
