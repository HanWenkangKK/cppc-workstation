package com.nanqiong.cppcpatient.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Repository
public class PatientRepository {

    private final JdbcTemplate jdbcTemplate;

    public PatientRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public PatientEntity save(PatientEntity entity) {
        String sql = """
                INSERT INTO patient_info(name, gender, age, disease_type, lesion_side, remark)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, entity.name());
            statement.setString(2, entity.gender());
            if (entity.age() == null) {
                statement.setObject(3, null);
            } else {
                statement.setInt(3, entity.age());
            }
            statement.setString(4, entity.diseaseType());
            statement.setString(5, entity.lesionSide());
            statement.setString(6, entity.remark());
            return statement;
        }, keyHolder);

        Long id = extractId(keyHolder);
        return findById(id).orElseThrow(() -> new IllegalStateException("患者创建后查询失败"));
    }

    public Optional<PatientEntity> findById(Long id) {
        String sql = """
                SELECT id, name, gender, age, disease_type, lesion_side, remark, created_at
                FROM patient_info
                WHERE id = ?
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> new PatientEntity(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("gender"),
                (Integer) rs.getObject("age"),
                rs.getString("disease_type"),
                rs.getString("lesion_side"),
                rs.getString("remark"),
                rs.getObject("created_at", LocalDateTime.class)
        ), id).stream().findFirst();
    }

    public boolean existsById(Long id) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM patient_info WHERE id = ?",
                Integer.class,
                id
        );
        return count != null && count > 0;
    }

    private Long extractId(KeyHolder keyHolder) {
        Map<String, Object> keys = keyHolder.getKeys();
        Object id = keys == null ? null : keys.get("id");
        if (id instanceof Number number) {
            return number.longValue();
        }
        throw new IllegalStateException("患者创建后未返回主键 id");
    }

    public record PatientEntity(
            Long id,
            String name,
            String gender,
            Integer age,
            String diseaseType,
            String lesionSide,
            String remark,
            LocalDateTime createdAt
    ) {
    }
}
