package com.nanqiong.cppcpatient.service;

import com.nanqiong.cppcpatient.dto.CreatePatientRequest;
import com.nanqiong.cppcpatient.dto.PatientResponse;
import com.nanqiong.cppcpatient.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class PatientService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public PatientResponse create(CreatePatientRequest request) {
        validateRequest(request);

        PatientRepository.PatientEntity saved = patientRepository.save(new PatientRepository.PatientEntity(
                null,
                request.name().trim(),
                request.gender(),
                request.age(),
                request.diseaseType(),
                request.lesionSide(),
                request.remark(),
                null
        ));

        return new PatientResponse(
                saved.id(),
                saved.name(),
                saved.gender(),
                saved.age(),
                saved.diseaseType(),
                saved.lesionSide(),
                saved.remark(),
                saved.createdAt().format(DATE_TIME_FORMATTER)
        );
    }

    private void validateRequest(CreatePatientRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("请求体不能为空");
        }
        if (request.name() == null || request.name().isBlank()) {
            throw new IllegalArgumentException("name 不能为空");
        }
        if (request.age() != null && request.age() < 0) {
            throw new IllegalArgumentException("age 不能小于 0");
        }
    }
}
