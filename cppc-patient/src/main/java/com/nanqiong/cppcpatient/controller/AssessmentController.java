package com.nanqiong.cppcpatient.controller;

import com.nanqiong.cppcpatient.dto.AssessmentResponse;
import com.nanqiong.cppcpatient.dto.CreateAssessmentRequest;
import com.nanqiong.cppcpatient.dto.SubmitAssessmentTagsRequest;
import com.nanqiong.cppcpatient.dto.SubmitAssessmentTagsResponse;
import com.nanqiong.cppcpatient.service.AssessmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "评估记录")
@RestController
@RequestMapping("/api/v1/assessments")
public class AssessmentController {

    private final AssessmentService assessmentService;

    public AssessmentController(AssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }

    @Operation(summary = "创建评估记录", description = "创建患者评估记录")
    @PostMapping
    public AssessmentResponse create(@RequestBody CreateAssessmentRequest request) {
        return assessmentService.create(request);
    }

    @Operation(summary = "提交评估勾选结果", description = "提交指定评估记录的标签勾选结果")
    @PostMapping("/{assessmentId}/tags")
    public SubmitAssessmentTagsResponse submitTags(
            @PathVariable Long assessmentId,
            @RequestBody SubmitAssessmentTagsRequest request
    ) {
        return assessmentService.submitTags(assessmentId, request);
    }
}
