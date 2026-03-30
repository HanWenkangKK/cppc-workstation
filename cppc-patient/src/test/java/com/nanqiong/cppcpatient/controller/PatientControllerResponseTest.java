package com.nanqiong.cppcpatient.controller;

import com.nanqiong.common.result.GlobalExceptionHandler;
import com.nanqiong.common.result.GlobalResponseAdvice;
import com.nanqiong.cppcpatient.dto.CreatePatientRequest;
import com.nanqiong.cppcpatient.dto.PatientResponse;
import com.nanqiong.cppcpatient.service.PatientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PatientController.class)
@Import({GlobalResponseAdvice.class, GlobalExceptionHandler.class})
class PatientControllerResponseTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PatientService patientService;

    @Test
    void shouldWrapSuccessResponse() throws Exception {
        when(patientService.create(any(CreatePatientRequest.class))).thenReturn(new PatientResponse(
                1L,
                "张三",
                "male",
                58,
                "脑出血",
                "右侧",
                "发病3个月，首次来诊",
                "2026-03-31 14:00:00"
        ));

        mockMvc.perform(post("/api/v1/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "张三",
                                  "gender": "male",
                                  "age": 58,
                                  "diseaseType": "脑出血",
                                  "lesionSide": "右侧",
                                  "remark": "发病3个月，首次来诊"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").value("success"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("张三"));
    }

    @Test
    void shouldWrapIllegalArgumentException() throws Exception {
        when(patientService.create(any(CreatePatientRequest.class)))
                .thenThrow(new IllegalArgumentException("patient name is required"));

        mockMvc.perform(post("/api/v1/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": ""
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(4001))
                .andExpect(jsonPath("$.msg").value("patient name is required"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}
