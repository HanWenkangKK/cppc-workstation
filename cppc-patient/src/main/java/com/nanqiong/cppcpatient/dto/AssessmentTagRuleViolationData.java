package com.nanqiong.cppcpatient.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "AssessmentTagRuleViolationData", description = "评估标签规则错误响应数据")
public record AssessmentTagRuleViolationData(
        @ArraySchema(schema = @Schema(implementation = AssessmentTagRuleViolation.class))
        List<AssessmentTagRuleViolation> violations
) {
}
