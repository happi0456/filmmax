package com.filmmax.dao;

import com.filmmax.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDao {

    private static final String BASE_SELECT = """
            SELECT u.id, u.username, u.email, u.password_hash, u.full_name,
                   r.name AS role, u.enabled, u.created_at
            FROM users u
            JOIN roles r ON r.id = u.role_id
            """;

    private final JdbcTemplate jdbc;

    public UserDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<User> findAll() {
        return jdbc.query(BASE_SELECT + " ORDER BY u.id", (rs, n) -> map(rs));
    }

    public Optional<User> findById(long id) {
        return jdbc.query(BASE_SELECT + " WHERE u.id = ?", (rs, n) -> map(rs), id)
                .stream().findFirst();
    }

    public Optional<User> findByUsername(String username) {
        return jdbc.query(BASE_SELECT + " WHERE u.username = ?", (rs, n) -> map(rs), username)
                .stream().findFirst();
    }

    public boolean existsByUsernameOrEmail(String username, String email) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM users WHERE username = ? OR email = ?",
                Integer.class, username, email);
        return count != null && count > 0;
    }

    public long insert(String username, String email, String passwordHash, String fullName, String roleName) {
        String sql = """
                INSERT INTO users (username, email, password_hash, full_name, role_id)
                VALUES (?, ?, ?, ?, (SELECT id FROM roles WHERE name = ?))
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, passwordHash);
            ps.setString(4, fullName);
            ps.setString(5, roleName);
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public boolean updateRole(long id, String roleName) {
        int rows = jdbc.update(
                "UPDATE users SET role_id = (SELECT id FROM roles WHERE name = ?) WHERE id = ?",
                roleName, id);
        return rows > 0;
    }

    private User map(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setFullName(rs.getString("full_name"));
        user.setRole(rs.getString("role"));
        user.setEnabled(rs.getBoolean("enabled"));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return user;
    }
}