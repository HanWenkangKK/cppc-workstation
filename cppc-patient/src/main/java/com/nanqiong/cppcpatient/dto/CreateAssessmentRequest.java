package com.nanqiong.cppcpatient.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "CreateAssessmentRequest", description = "创建评估记录请求")
public record CreateAssessmentRequest(
        @Schema(description = "患者ID", example = "1")
        Long patientId,
        @Schema(description = "主诉", example = "右侧肢体活动差")
        String chiefComplaint,
        @Schema(description = "补充文本", example = "伴足内翻，步态不稳，治疗师怀疑存在肌张力异常")
        String extraText,
        @ArraySchema(schema = @Schema(description = "图片地址", example = "https://mock.example.com/images/1.jpg"))
        List<String> imageUrls
) {
}
