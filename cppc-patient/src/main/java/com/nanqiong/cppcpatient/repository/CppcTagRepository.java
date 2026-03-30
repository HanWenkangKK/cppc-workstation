package com.nanqiong.cppcpatient.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CppcTagRepository {

    private final JdbcTemplate jdbcTemplate;

    public CppcTagRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<CppcTagEntity> findAll() {
        return jdbcTemplate.query("""
                SELECT id, parent_id, biz_code, label, level, is_leaf
                FROM cppc_tags
                ORDER BY sort_no ASC, id ASC
                """, (rs, rowNum) -> new CppcTagEntity(
                rs.getLong("id"),
                rs.getLong("parent_id"),
                rs.getString("biz_code"),
                rs.getString("label"),
                rs.getInt("level"),
                rs.getBoolean("is_leaf")
        ));
    }

    public record CppcTagEntity(
            Long id,
            Long parentId,
            String bizCode,
            String label,
            Integer level,
            Boolean isLeaf
    ) {
    }
}
