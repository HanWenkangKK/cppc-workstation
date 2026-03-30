package com.nanqiong.cppcpatient.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "SubmitAssessmentTagsRequest", description = "提交评估标签请求")
public record SubmitAssessmentTagsRequest(
        @ArraySchema(schema = @Schema(description = "标签ID", example = "111"))
        List<Long> tagIds
) {
}
