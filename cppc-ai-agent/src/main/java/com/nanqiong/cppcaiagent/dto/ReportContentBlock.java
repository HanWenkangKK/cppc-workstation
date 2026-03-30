package com.nanqiong.cppcaiagent.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ReportContentBlock", description = "报告内容块")
public record ReportContentBlock(
        @Schema(description = "内容块ID", example = "title_1")
        String id,
        @Schema(description = "内容块类型", example = "title")
        String type,
        @Schema(description = "标题", example = "CPPC评估报告")
        String title,
        @Schema(description = "值，可能是文本或表格对象")
        Object value
) {
}
