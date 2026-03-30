package com.nanqiong.cppcpatient.controller;

import com.nanqiong.cppcpatient.dto.CreatePatientRequest;
import com.nanqiong.cppcpatient.dto.PatientResponse;
import com.nanqiong.cppcpatient.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "患者管理")
@RestController
@RequestMapping("/api/v1/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @Operation(summary = "创建患者", description = "创建患者基础信息")
    @PostMapping
    public PatientResponse create(@RequestBody CreatePatientRequest request) {
        return patientService.create(request);
    }
}
