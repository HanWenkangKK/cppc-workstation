package com.nanqiong.cppcpatient.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "AssessmentTagRuleViolation", description = "评估标签规则违规明细")
public record AssessmentTagRuleViolation(
        @Schema(description = "违规类型", example = "exclusiveGroup")
        String type,
        @Schema(description = "规则分组", example = "MOTOR_TONE_SIDE")
        String group,
        @ArraySchema(schema = @Schema(description = "关联标签ID", example = "111"))
        List<Long> tagIds,
        @Schema(description = "详细提示信息", example = "“左侧肌张力增高”和“右侧肌张力增高”不能同时选择")
        String message
) {
}
