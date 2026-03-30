package com.nanqiong.cppcpatient.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CreatePatientRequest", description = "创建患者请求")
public record CreatePatientRequest(
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
        String remark
) {
}
