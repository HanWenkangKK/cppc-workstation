package com.nanqiong.cppcaiagent.controller;

import com.nanqiong.common.result.GlobalExceptionHandler;
import com.nanqiong.common.result.GlobalResponseAdvice;
import com.nanqiong.cppcaiagent.dto.GenerateReportRequest;
import com.nanqiong.cppcaiagent.dto.GenerateReportResponse;
import com.nanqiong.cppcaiagent.dto.ReportContentBlock;
import com.nanqiong.cppcaiagent.service.ReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportController.class)
@Import({GlobalResponseAdvice.class, GlobalExceptionHandler.class})
class ReportControllerResponseTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportService reportService;

    @Test
    void shouldWrapSuccessResponse() throws Exception {
        when(reportService.generate(any(GenerateReportRequest.class))).thenReturn(new GenerateReportResponse(
                5001L,
                1001L,
                1L,
                "assessment_report",
                "success",
                false,
                List.of(new ReportContentBlock("title_1", "title", "CPPC评估报告", "CPPC评估报告")),
                "2026-03-31 14:20:00"
        ));

        mockMvc.perform(post("/api/v1/reports/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "assessmentId": 1001,
                                  "reportType": "assessment_report"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").value("success"))
                .andExpect(jsonPath("$.data.reportId").value(5001))
                .andExpect(jsonPath("$.data.reportType").value("assessment_report"));
    }

    @Test
    void shouldWrapIllegalArgumentException() throws Exception {
        when(reportService.generate(any(GenerateReportRequest.class)))
                .thenThrow(new IllegalArgumentException("assessment not found"));

        mockMvc.perform(post("/api/v1/reports/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "assessmentId": 1001,
                                  "reportType": "assessment_report"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(4001))
                .andExpect(jsonPath("$.msg").value("assessment not found"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}
