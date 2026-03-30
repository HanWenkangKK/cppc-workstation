package com.nanqiong.cppcaiagent.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AssessmentQueryRepository {

    private final JdbcTemplate jdbcTemplate;

    public AssessmentQueryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<AssessmentDetail> findDetailByAssessmentId(Long assessmentId) {
        String sql = """
                SELECT ar.id AS assessment_id,
                       ar.patient_id,
                       ar.chief_complaint,
                       ar.extra_text,
                       ar.tag_ids_json::text AS tag_ids_json,
                       pi.name,
                       pi.age,
                       pi.gender,
                       pi.disease_type,
                       pi.lesion_side
                FROM assessment_record ar
                JOIN patient_info pi ON pi.id = ar.patient_id
                WHERE ar.id = ?
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> new AssessmentDetail(
                rs.getLong("assessment_id"),
                rs.getLong("patient_id"),
                rs.getString("chief_complaint"),
                rs.getString("extra_text"),
                rs.getString("tag_ids_json"),
                rs.getString("name"),
                (Integer) rs.getObject("age"),
                rs.getString("gender"),
                rs.getString("disease_type"),
                rs.getString("lesion_side")
        ), assessmentId).stream().findFirst();
    }

    public record AssessmentDetail(
            Long assessmentId,
            Long patientId,
            String chiefComplaint,
            String extraText,
            String tagIdsJson,
            String patientName,
            Integer age,
            String gender,
            String diseaseType,
            String lesionSide
    ) {
    }
}
