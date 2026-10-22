package com.filmmax.dao;

import com.filmmax.model.Scene;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class SceneDao {

    private static final String BASE_SELECT = """
            SELECT s.id, s.scene_number, s.title, s.description, s.location_name,
                   s.shoot_date, s.time_slot, s.created_by, s.created_at
            FROM scenes s
            """;

    private final JdbcTemplate jdbc;

    public SceneDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Scene> findAll() {
        return jdbc.query(BASE_SELECT + " ORDER BY s.scene_number", (rs, n) -> map(rs));
    }

    public Optional<Scene> findById(long id) {
        return jdbc.query(BASE_SELECT + " WHERE s.id = ?", (rs, n) -> map(rs), id)
                .stream().findFirst();
    }

    public long insert(Scene s) {
        String sql = """
                INSERT INTO scenes (scene_number, title, description, location_name, shoot_date, time_slot, created_by)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, s.getSceneNumber());
            ps.setString(2, s.getTitle());
            ps.setString(3, s.getDescription());
            ps.setString(4, s.getLocationName());
            ps.setDate(5, s.getShootDate() == null ? null : Date.valueOf(s.getShootDate()));
            ps.setString(6, s.getTimeSlot());
            ps.setLong(7, s.getCreatedBy());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public boolean update(long id, Scene s) {
        int rows = jdbc.update("""
                UPDATE scenes SET scene_number = ?, title = ?, description = ?,
                       location_name = ?, shoot_date = ?, time_slot = ?
                WHERE id = ?
                """,
                s.getSceneNumber(), s.getTitle(), s.getDescription(), s.getLocationName(),
                s.getShootDate() == null ? null : Date.valueOf(s.getShootDate()),
                s.getTimeSlot(), id);
        return rows > 0;
    }

    public boolean delete(long id) {
        return jdbc.update("DELETE FROM scenes WHERE id = ?", id) > 0;
    }

    private Scene map(ResultSet rs) throws SQLException {
        Scene s = new Scene();
        s.setId(rs.getLong("id"));
        s.setSceneNumber(rs.getInt("scene_number"));
        s.setTitle(rs.getString("title"));
        s.setDescription(rs.getString("description"));
        s.setLocationName(rs.getString("location_name"));
        Date d = rs.getDate("shoot_date");
        s.setShootDate(d == null ? null : d.toLocalDate());
        s.setTimeSlot(rs.getString("time_slot"));
        s.setCreatedBy(rs.getLong("created_by"));
        s.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return s;
    }
}