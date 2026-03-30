package com.nanqiong.cppcpatient.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class AssessmentRepository {

    private final JdbcTemplate jdbcTemplate;

    public AssessmentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public AssessmentEntity save(AssessmentEntity entity) {
        String sql = """
                INSERT INTO assessment_record(patient_id, chief_complaint, extra_text, image_urls_json, status)
                VALUES (?, ?, ?, CAST(? AS jsonb), ?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setLong(1, entity.patientId());
            statement.setString(2, entity.chiefComplaint());
            statement.setString(3, entity.extraText());
            statement.setString(4, entity.imageUrlsJson());
            statement.setString(5, entity.status());
            return statement;
        }, keyHolder);

        Number key = keyHolder.getKey();
        Long id = key == null ? null : key.longValue();
        return findById(id).orElseThrow(() -> new IllegalStateException("评估记录创建后查询失败"));
    }

    public Optional<AssessmentEntity> findById(Long id) {
        String sql = """
                SELECT id, patient_id, chief_complaint, extra_text, image_urls_json::text AS image_urls_json,
                       tag_ids_json::text AS tag_ids_json, status, created_at
                FROM assessment_record
                WHERE id = ?
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> new AssessmentEntity(
                rs.getLong("id"),
                rs.getLong("patient_id"),
                rs.getString("chief_complaint"),
                rs.getString("extra_text"),
                rs.getString("image_urls_json"),
                rs.getString("tag_ids_json"),
                rs.getString("status"),
                rs.getObject("created_at", LocalDateTime.class)
        ), id).stream().findFirst();
    }

    public void updateTagIds(Long assessmentId, String tagIdsJson) {
        int updated = jdbcTemplate.update("""
                UPDATE assessment_record
                SET tag_ids_json = CAST(? AS jsonb), status = 'draft'
                WHERE id = ?
                """, tagIdsJson, assessmentId);
        if (updated == 0) {
            throw new IllegalArgumentException("assessment not found");
        }
    }

    public record AssessmentEntity(
            Long id,
            Long patientId,
            String chiefComplaint,
            String extraText,
            String imageUrlsJson,
            String tagIdsJson,
            String status,
            LocalDateTime createdAt
    ) {
    }
}
