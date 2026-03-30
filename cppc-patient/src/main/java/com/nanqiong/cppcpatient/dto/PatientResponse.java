package com.nanqiong.cppcpatient.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "PatientResponse", description = "患者响应")
public record PatientResponse(
        @Schema(description = "患者ID", example = "1")
        Long id,
        @Schema(description = "患者姓名", example = "张三")
        String name,
        @Schema(description = "性别", example = "male")
        String gender,
        @Schema(description = "年龄", example = "58")
        Integer age,
        @Schema(description = "病种", example = "脑出血")
        String diseaseType,
        @Schema(description = "受损侧别", example = "右侧")
        String lesionSide,
        @Schema(description = "备注", example = "发病3个月，首次来诊")
        String remark,
        @Schema(description = "创建时间", example = "2026-03-31 14:00:00")
        String createdAt
) {
}
