package com.nanqiong.cppcpatient.controller;

import com.nanqiong.common.result.BusinessException;
import com.nanqiong.common.result.GlobalExceptionHandler;
import com.nanqiong.common.result.GlobalResponseAdvice;
import com.nanqiong.cppcpatient.dto.AssessmentTagRuleViolation;
import com.nanqiong.cppcpatient.dto.AssessmentTagRuleViolationData;
import com.nanqiong.cppcpatient.dto.SubmitAssessmentTagsResponse;
import com.nanqiong.cppcpatient.service.AssessmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AssessmentController.class)
@Import({GlobalResponseAdvice.class, GlobalExceptionHandler.class})
class AssessmentControllerResponseTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AssessmentService assessmentService;

    @Test
    void shouldWrapSuccessResponse() throws Exception {
        when(assessmentService.submitTags(eq(1001L), any())).thenReturn(
                new SubmitAssessmentTagsResponse(1001L, List.of(111L), 1, "draft")
        );

        mockMvc.perform(post("/api/v1/assessments/1001/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "tagIds": [111]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.assessmentId").value(1001))
                .andExpect(jsonPath("$.data.tagCount").value(1));
    }

    @Test
    void shouldReturnViolationDataWhenRuleFails() throws Exception {
        when(assessmentService.submitTags(eq(1001L), any())).thenThrow(new BusinessException(
                4104,
                "exclusive rule violated",
                new AssessmentTagRuleViolationData(List.of(
                        new AssessmentTagRuleViolation(
                                "exclusiveGroup",
                                "MOTOR_TONE_SIDE",
                                List.of(111L, 112L),
                                "“左侧肌张力增高”和“右侧肌张力增高”不能同时选择"
                        )
                ))
        ));

        mockMvc.perform(post("/api/v1/assessments/1001/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "tagIds": [111, 112]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(4104))
                .andExpect(jsonPath("$.msg").value("exclusive rule violated"))
                .andExpect(jsonPath("$.data.violations[0].type").value("exclusiveGroup"))
                .andExpect(jsonPath("$.data.violations[0].group").value("MOTOR_TONE_SIDE"));
    }
}
