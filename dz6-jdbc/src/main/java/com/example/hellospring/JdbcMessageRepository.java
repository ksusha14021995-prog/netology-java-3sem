package com.example.hellospring;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class JdbcMessageRepository {

    private static final String SELECT_WITH_AUTHOR = """
            SELECT m.id, m.content, m.user_id, u.name AS author, m.created_at
            FROM messages m
            JOIN users u ON u.id = m.user_id
            """;
    // id — второй ключ сортировки: у сообщений одного батча created_at совпадает
    private static final String NEWEST_FIRST = " ORDER BY m.created_at DESC, m.id DESC";

    private static final RowMapper<Message> MESSAGE_ROW_MAPPER = (rs, rowNum) -> {
        Message message = new Message();
        message.setId(rs.getLong("id"));
        message.setContent(rs.getString("content"));
        message.setUserId(rs.getLong("user_id"));
        message.setAuthor(rs.getString("author"));
        message.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return message;
    };

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    public JdbcMessageRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedJdbcTemplate = namedJdbcTemplate;
    }

    public Message save(String content, Long userId) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        namedJdbcTemplate.update(
                "INSERT INTO messages (content, user_id) VALUES (:content, :userId)",
                new MapSqlParameterSource().addValue("content", content).addValue("userId", userId),
                keyHolder, new String[]{"id"});
        return findById(keyHolder.getKey().longValue()).orElseThrow();
    }

    /** Батч-вставка одним JDBC batch; возвращает созданные id в порядке входного списка. */
    public List<Long> saveAll(List<Map<String, Object>> rows) {
        SqlParameterSource[] batch = rows.stream()
                .map(MapSqlParameterSource::new)
                .toArray(SqlParameterSource[]::new);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        namedJdbcTemplate.batchUpdate(
                "INSERT INTO messages (content, user_id) VALUES (:content, :userId)",
                batch, keyHolder, new String[]{"id"});
        return keyHolder.getKeyList().stream()
                .map(keys -> ((Number) keys.get(keys.containsKey("id") ? "id" : "ID")).longValue())
                .toList();
    }

    public Optional<Message> findById(Long id) {
        return jdbcTemplate.query(SELECT_WITH_AUTHOR + " WHERE m.id = ?", MESSAGE_ROW_MAPPER, id)
                .stream().findFirst();
    }

    public List<Message> findByIds(List<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        return namedJdbcTemplate.query(SELECT_WITH_AUTHOR + " WHERE m.id IN (:ids) ORDER BY m.id",
                Map.of("ids", ids), MESSAGE_ROW_MAPPER);
    }

    public List<Message> findPage(int page, int size) {
        return jdbcTemplate.query(SELECT_WITH_AUTHOR + NEWEST_FIRST + " LIMIT ? OFFSET ?",
                MESSAGE_ROW_MAPPER, size, (long) page * size);
    }

    public long count() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM messages", Long.class);
    }

    public List<Message> findPageByAuthor(String author, int page, int size) {
        return jdbcTemplate.query(SELECT_WITH_AUTHOR + " WHERE u.name = ?" + NEWEST_FIRST + " LIMIT ? OFFSET ?",
                MESSAGE_ROW_MAPPER, author, size, (long) page * size);
    }

    public long countByAuthor(String author) {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM messages m JOIN users u ON u.id = m.user_id WHERE u.name = ?",
                Long.class, author);
    }

    public int deleteById(Long id) {
        return jdbcTemplate.update("DELETE FROM messages WHERE id = ?", id);
    }

    public void clear() {
        jdbcTemplate.update("DELETE FROM messages");
    }
}
