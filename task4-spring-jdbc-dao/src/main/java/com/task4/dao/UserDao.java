package com.task4.dao;

import com.task4.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDao {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<User> userRowMapper = (rs, rowNum) -> new User(
            rs.getLong("id"),
            rs.getString("username")
    );

    @Autowired
    public UserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Создание таблицы users (если не существует)
     */
    public void createTableIfNotExists() {
        String sql = """
                CREATE TABLE IF NOT EXISTS users (
                    id BIGSERIAL PRIMARY KEY,
                    username VARCHAR(255) UNIQUE NOT NULL
                )
                """;
        jdbcTemplate.execute(sql);
        System.out.println("Таблица 'users' проверена/создана");
    }

    /**
     * Создание нового пользователя (INSERT)
     * @return созданный пользователь с сгенерированным ID
     */
    public User create(User user) {
        String sql = "INSERT INTO users (username) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getUsername());
            return ps;
        }, keyHolder);

        Long generatedId = keyHolder.getKey().longValue();
        user.setId(generatedId);
        return user;
    }

    /**
     * Обновление пользователя (UPDATE)
     * @return true если обновление выполнено, false если пользователь не найден
     */
    public boolean update(User user) {
        String sql = "UPDATE users SET username = ? WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, user.getUsername(), user.getId());
        return rowsAffected > 0;
    }

    /**
     * Удаление пользователя по ID (DELETE)
     * @return true если удаление выполнено, false если пользователь не найден
     */
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        return rowsAffected > 0;
    }

    /**
     * Удаление пользователя по username (DELETE)
     * @return true если удаление выполнено, false если пользователь не найден
     */
    public boolean deleteByUsername(String username) {
        String sql = "DELETE FROM users WHERE username = ?";
        int rowsAffected = jdbcTemplate.update(sql, username);
        return rowsAffected > 0;
    }

    /**
     * Поиск пользователя по ID (SELECT)
     * @return Optional с пользователем или пустой Optional
     */
    public Optional<User> findById(Long id) {
        String sql = "SELECT id, username FROM users WHERE id = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, userRowMapper, id);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * Поиск пользователя по username (SELECT)
     * @return Optional с пользователем или пустой Optional
     */
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT id, username FROM users WHERE username = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, userRowMapper, username);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * Получение всех пользователей (SELECT)
     * @return список всех пользователей
     */
    public List<User> findAll() {
        String sql = "SELECT id, username FROM users ORDER BY id";
        return jdbcTemplate.query(sql, userRowMapper);
    }

    /**
     * Проверка существования пользователя по username
     */
    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username);
        return count != null && count > 0;
    }

    /**
     * Получение количества всех пользователей
     */
    public long count() {
        String sql = "SELECT COUNT(*) FROM users";
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count != null ? count : 0;
    }

    /**
     * Удаление всех пользователей (очистка таблицы)
     */
    public void deleteAll() {
        String sql = "DELETE FROM users";
        jdbcTemplate.update(sql);
    }
}