package com.nanqiong.cppcaiagent.service;

import com.nanqiong.cppcaiagent.dto.CozeGenerateReportPayload;
import com.nanqiong.cppcaiagent.dto.ReportContentBlock;
import com.nanqiong.cppcaiagent.dto.ReportTableValue;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MockRemoteAiService implements RemoteAiService {

    @Override
    public List<ReportContentBlock> generateReport(CozeGenerateReportPayload payload) {
        List<Long> tagIds = payload.assessment().tagIds();
        String patientSummary = buildPatientSummary(payload, tagIds);
        String tagSummary = tagIds == null || tagIds.isEmpty()
                ? "本次评估暂未提交标签结果"
                : "已勾选标签ID：" + tagIds.stream().map(String::valueOf).collect(Collectors.joining("、"));

        List<List<String>> rows = new ArrayList<>();
        rows.add(List.of("患者姓名", safeText(payload.patient().name())));
        rows.add(List.of("主诉", safeText(payload.assessment().chiefComplaint())));
        rows.add(List.of("补充说明", safeText(payload.assessment().extraText())));
        rows.add(List.of("标签概况", tagSummary));

        return List.of(
                new ReportContentBlock(
                        "title_1",
                        "title",
                        "CPPC评估报告",
                        "CPPC评估报告"
                ),
                new ReportContentBlock(
                        "text_1",
                        "text",
                        "基本结论",
                        patientSummary
                ),
                new ReportContentBlock(
                        "table_1",
                        "table",
                        "评估摘要",
                        new ReportTableValue(
                                List.of("模块", "发现"),
                                rows
                        )
                )
        );
    }

    private String buildPatientSummary(CozeGenerateReportPayload payload, List<Long> tagIds) {
        StringBuilder builder = new StringBuilder();
        builder.append("患者");
        builder.append(safeText(payload.patient().name()));
        builder.append("，");

        if (payload.patient().age() != null) {
            builder.append(payload.patient().age()).append("岁，");
        }

        if (hasText(payload.patient().diseaseType())) {
            builder.append("目前主要病种为").append(payload.patient().diseaseType()).append("，");
        }

        if (hasText(payload.patient().lesionSide())) {
            builder.append("受损侧别为").append(payload.patient().lesionSide()).append("。");
        }

        if (hasText(payload.assessment().chiefComplaint())) {
            builder.append("主诉为“").append(payload.assessment().chiefComplaint()).append("”。");
        }

        if (tagIds != null && !tagIds.isEmpty()) {
            builder.append("本次已提交").append(tagIds.size()).append("项评估标签，建议结合结构化评估结果继续生成正式报告。");
        } else {
            builder.append("当前尚未提交评估标签，报告内容为基础预览版本。");
        }
        return builder.toString();
    }

    private boolean hasText(String text) {
        return text != null && !text.isBlank();
    }

    private String safeText(String text) {
        return hasText(text) ? text : "无";
    }
}
