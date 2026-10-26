package com.example.hellospring;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcUserRepository {

    private static final RowMapper<User> USER_ROW_MAPPER = (rs, rowNum) -> new User(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getTimestamp("created_at").toLocalDateTime());

    private final JdbcTemplate jdbcTemplate;

    public JdbcUserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public User save(String name) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO users (name) VALUES (?)", new String[]{"id"});
            ps.setString(1, name);
            return ps;
        }, keyHolder);
        return findById(keyHolder.getKey().longValue()).orElseThrow();
    }

    public Optional<User> findById(Long id) {
        return jdbcTemplate.query("SELECT id, name, created_at FROM users WHERE id = ?", USER_ROW_MAPPER, id)
                .stream().findFirst();
    }

    public Optional<User> findByName(String name) {
        return jdbcTemplate.query("SELECT id, name, created_at FROM users WHERE name = ?", USER_ROW_MAPPER, name)
                .stream().findFirst();
    }

    public List<User> findAll() {
        return jdbcTemplate.query("SELECT id, name, created_at FROM users ORDER BY id", USER_ROW_MAPPER);
    }

    public void clear() {
        jdbcTemplate.update("DELETE FROM users");
    }
}
