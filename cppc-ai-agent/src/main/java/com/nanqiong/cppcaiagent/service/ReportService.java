package com.nanqiong.cppcaiagent.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nanqiong.cppcaiagent.dto.CozeGenerateReportPayload;
import com.nanqiong.cppcaiagent.dto.GenerateReportRequest;
import com.nanqiong.cppcaiagent.dto.GenerateReportResponse;
import com.nanqiong.cppcaiagent.dto.ReportContentBlock;
import com.nanqiong.cppcaiagent.repository.AssessmentQueryRepository;
import com.nanqiong.cppcaiagent.repository.ReportRecordRepository;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Service
public class ReportService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String DEFAULT_REPORT_TYPE = "assessment_report";

    private final AssessmentQueryRepository assessmentQueryRepository;
    private final ReportRecordRepository reportRecordRepository;
    private final RemoteAiService remoteAiService;
    private final ObjectMapper objectMapper;

    public ReportService(
            AssessmentQueryRepository assessmentQueryRepository,
            ReportRecordRepository reportRecordRepository,
            RemoteAiService remoteAiService,
            ObjectMapper objectMapper
    ) {
        this.assessmentQueryRepository = assessmentQueryRepository;
        this.reportRecordRepository = reportRecordRepository;
        this.remoteAiService = remoteAiService;
        this.objectMapper = objectMapper;
    }

    public GenerateReportResponse generate(GenerateReportRequest request) {
        validateRequest(request);

        AssessmentQueryRepository.AssessmentDetail detail = assessmentQueryRepository
                .findDetailByAssessmentId(request.assessmentId())
                .orElseThrow(() -> new IllegalArgumentException("assessment not found"));

        List<Long> tagIds = readTagIds(detail.tagIdsJson());
        CozeGenerateReportPayload payload = buildCozePayload(detail, tagIds, request.reportType());
        List<ReportContentBlock> content = remoteAiService.generateReport(payload);
        String contentJson = toJson(content);

        ReportRecordRepository.SavedReportRecord savedReportRecord = reportRecordRepository.save(
                new ReportRecordRepository.CreateReportRecordCommand(
                        detail.assessmentId(),
                        detail.patientId(),
                        request.reportType(),
                        "success",
                        contentJson,
                        contentJson,
                        false,
                        "mock-" + payload.assessment().id(),
                        null
                )
        );

        return new GenerateReportResponse(
                savedReportRecord.id(),
                detail.assessmentId(),
                detail.patientId(),
                request.reportType(),
                "success",
                false,
                content,
                savedReportRecord.createdAt().format(DATE_TIME_FORMATTER)
        );
    }

    private void validateRequest(GenerateReportRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request body is required");
        }
        if (request.assessmentId() == null || request.assessmentId() <= 0) {
            throw new IllegalArgumentException("assessmentId must be greater than 0");
        }
        if (request.reportType() == null || request.reportType().isBlank()) {
            throw new IllegalArgumentException("reportType is required");
        }
        if (!DEFAULT_REPORT_TYPE.equals(request.reportType())) {
            throw new IllegalArgumentException("unsupported reportType: " + request.reportType());
        }
    }

    private CozeGenerateReportPayload buildCozePayload(
            AssessmentQueryRepository.AssessmentDetail detail,
            List<Long> tagIds,
            String reportType
    ) {
        return new CozeGenerateReportPayload(
                new CozeGenerateReportPayload.Patient(
                        detail.patientId(),
                        detail.patientName(),
                        detail.age(),
                        detail.gender(),
                        detail.diseaseType(),
                        detail.lesionSide()
                ),
                new CozeGenerateReportPayload.Assessment(
                        detail.assessmentId(),
                        detail.chiefComplaint(),
                        detail.extraText(),
                        tagIds
                ),
                reportType
        );
    }

    private List<Long> readTagIds(String tagIdsJson) {
        if (tagIdsJson == null || tagIdsJson.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(tagIdsJson, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("评估标签解析失败", e);
        }
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("报告内容序列化失败", e);
        }
    }
}
