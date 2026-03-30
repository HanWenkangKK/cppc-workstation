package com.nanqiong.cppcpatient.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "SubmitAssessmentTagsResponse", description = "提交评估标签响应")
public record SubmitAssessmentTagsResponse(
        @Schema(description = "评估记录ID", example = "1001")
        Long assessmentId,
        @ArraySchema(schema = @Schema(description = "标签ID", example = "111"))
        List<Long> tagIds,
        @Schema(description = "标签数量", example = "3")
        Integer tagCount,
        @Schema(description = "状态", example = "draft")
        String status
) {
}
