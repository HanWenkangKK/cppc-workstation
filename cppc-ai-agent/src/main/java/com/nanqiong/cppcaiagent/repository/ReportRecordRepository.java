package com.nanqiong.cppcaiagent.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;

@Repository
public class ReportRecordRepository {

    private final JdbcTemplate jdbcTemplate;

    public ReportRecordRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public SavedReportRecord save(CreateReportRecordCommand command) {
        String sql = """
                INSERT INTO report_record(
                    assessment_id, patient_id, report_type, status,
                    content, original_content, is_modified, ai_trace_id, error_msg
                )
                VALUES (?, ?, ?, ?, CAST(? AS jsonb), CAST(? AS jsonb), ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setLong(1, command.assessmentId());
            statement.setLong(2, command.patientId());
            statement.setString(3, command.reportType());
            statement.setString(4, command.status());
            statement.setString(5, command.contentJson());
            statement.setString(6, command.originalContentJson());
            statement.setBoolean(7, command.isModified());
            statement.setString(8, command.aiTraceId());
            statement.setString(9, command.errorMsg());
            return statement;
        }, keyHolder);

        Number key = keyHolder.getKey();
        Long id = key == null ? null : key.longValue();
        LocalDateTime createdAt = jdbcTemplate.queryForObject(
                "SELECT created_at FROM report_record WHERE id = ?",
                LocalDateTime.class,
                id
        );
        return new SavedReportRecord(id, createdAt);
    }

    public record CreateReportRecordCommand(
            Long assessmentId,
            Long patientId,
            String reportType,
            String status,
            String contentJson,
            String originalContentJson,
            boolean isModified,
            String aiTraceId,
            String errorMsg
    ) {
    }

    public record SavedReportRecord(
            Long id,
            LocalDateTime createdAt
    ) {
    }
}
