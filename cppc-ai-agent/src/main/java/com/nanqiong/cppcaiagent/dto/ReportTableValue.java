package com.nanqiong.cppcaiagent.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "ReportTableValue", description = "表格类型报告内容")
public record ReportTableValue(
        @ArraySchema(schema = @Schema(description = "列名", example = "模块"))
        List<String> columns,
        @ArraySchema(schema = @Schema(description = "单元格内容", example = "运动功能"))
        List<List<String>> rows
) {
}
