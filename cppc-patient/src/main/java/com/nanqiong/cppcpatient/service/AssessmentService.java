package com.nanqiong.cppcpatient.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nanqiong.cppcpatient.dto.AssessmentResponse;
import com.nanqiong.cppcpatient.dto.CreateAssessmentRequest;
import com.nanqiong.cppcpatient.dto.SubmitAssessmentTagsRequest;
import com.nanqiong.cppcpatient.dto.SubmitAssessmentTagsResponse;
import com.nanqiong.cppcpatient.repository.AssessmentRepository;
import com.nanqiong.cppcpatient.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Service
public class AssessmentService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final AssessmentRepository assessmentRepository;
    private final PatientRepository patientRepository;
    private final AssessmentTagRuleService assessmentTagRuleService;
    private final ObjectMapper objectMapper;

    public AssessmentService(
            AssessmentRepository assessmentRepository,
            PatientRepository patientRepository,
            AssessmentTagRuleService assessmentTagRuleService,
            ObjectMapper objectMapper
    ) {
        this.assessmentRepository = assessmentRepository;
        this.patientRepository = patientRepository;
        this.assessmentTagRuleService = assessmentTagRuleService;
        this.objectMapper = objectMapper;
    }

    public AssessmentResponse create(CreateAssessmentRequest request) {
        validateCreateRequest(request);

        if (!patientRepository.existsById(request.patientId())) {
            throw new IllegalArgumentException("patient not found");
        }

        AssessmentRepository.AssessmentEntity saved = assessmentRepository.save(new AssessmentRepository.AssessmentEntity(
                null,
                request.patientId(),
                request.chiefComplaint(),
                request.extraText(),
                toJson(request.imageUrls() == null ? Collections.emptyList() : request.imageUrls()),
                null,
                "draft",
                null
        ));

        return new AssessmentResponse(
                saved.id(),
                saved.patientId(),
                saved.chiefComplaint(),
                saved.extraText(),
                toStringList(saved.imageUrlsJson()),
                saved.status(),
                saved.createdAt().format(DATE_TIME_FORMATTER)
        );
    }

    public SubmitAssessmentTagsResponse submitTags(Long assessmentId, SubmitAssessmentTagsRequest request) {
        if (assessmentId == null || assessmentId <= 0) {
            throw new IllegalArgumentException("assessmentId 必须大于 0");
        }
        if (request == null || request.tagIds() == null) {
            throw new IllegalArgumentException("tagIds 不能为空");
        }

        assessmentTagRuleService.validateSelectedTagIds(request.tagIds());
        List<Long> normalizedTagIds = request.tagIds().stream()
                .filter(java.util.Objects::nonNull)
                .distinct()
                .toList();

        assessmentRepository.updateTagIds(assessmentId, toJson(normalizedTagIds));
        return new SubmitAssessmentTagsResponse(
                assessmentId,
                normalizedTagIds,
                normalizedTagIds.size(),
                "draft"
        );
    }

    private void validateCreateRequest(CreateAssessmentRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("请求体不能为空");
        }
        if (request.patientId() == null || request.patientId() <= 0) {
            throw new IllegalArgumentException("patientId 必须大于 0");
        }
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("JSON 序列化失败", e);
        }
    }

    private List<String> toStringList(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("图片列表解析失败", e);
        }
    }
}
