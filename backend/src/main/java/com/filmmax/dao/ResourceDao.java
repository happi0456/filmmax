package com.filmmax.dao;

import com.filmmax.dto.ResourceDtos.ResourceResponse;
import com.filmmax.model.ResourceType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

// Table names come only from the ResourceType enum, never from user input,
// so concatenating them into SQL is safe here.
@Repository
public class ResourceDao {

    private final JdbcTemplate jdbc;

    public ResourceDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<ResourceResponse> findAll(ResourceType t) {
        return jdbc.query("SELECT id, name, detail FROM " + t.getPath() + " ORDER BY name",
                (rs, n) -> new ResourceResponse(rs.getLong("id"), rs.getString("name"), rs.getString("detail")));
    }

    public boolean exists(ResourceType t, long id) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM " + t.getPath() + " WHERE id = ?", Integer.class, id);
        return count != null && count > 0;
    }

    public long insert(ResourceType t, String name, String detail) {
        String sql = "INSERT INTO " + t.getPath() + " (name, detail) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setString(2, detail);
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public boolean delete(ResourceType t, long id) {
        return jdbc.update("DELETE FROM " + t.getPath() + " WHERE id = ?", id) > 0;
    }
}