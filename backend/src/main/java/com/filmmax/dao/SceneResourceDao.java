package com.filmmax.dao;

import com.filmmax.dto.ResourceDtos.ResourceResponse;
import com.filmmax.model.ResourceType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SceneResourceDao {

    private final JdbcTemplate jdbc;

    public SceneResourceDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // Returns the scene number of another scene that already uses this resource
    // on the same shoot date and time slot, if there is one.
    public Optional<Integer> findConflictingScene(long sceneId, ResourceType t, long resourceId) {
        return jdbc.query("""
                SELECT other.scene_number
                FROM scene_resources sr
                JOIN scenes other ON other.id = sr.scene_id
                JOIN scenes me ON me.id = ?
                WHERE sr.resource_type = ?
                  AND sr.resource_id = ?
                  AND other.id <> me.id
                  AND other.shoot_date = me.shoot_date
                  AND other.time_slot = me.time_slot
                LIMIT 1
                """,
                (rs, n) -> rs.getInt("scene_number"),
                sceneId, t.name(), resourceId).stream().findFirst();
    }

    public void attach(long sceneId, ResourceType t, long resourceId) {
        jdbc.update("INSERT INTO scene_resources (scene_id, resource_type, resource_id) VALUES (?, ?, ?)",
                sceneId, t.name(), resourceId);
    }

    public boolean detach(long sceneId, ResourceType t, long resourceId) {
        return jdbc.update(
                "DELETE FROM scene_resources WHERE scene_id = ? AND resource_type = ? AND resource_id = ?",
                sceneId, t.name(), resourceId) > 0;
    }

    public List<ResourceResponse> findAttached(long sceneId, ResourceType t) {
        return jdbc.query("""
                SELECT r.id, r.name, r.detail
                FROM scene_resources sr
                JOIN %s r ON r.id = sr.resource_id
                WHERE sr.scene_id = ? AND sr.resource_type = ?
                ORDER BY r.name
                """.formatted(t.getPath()),
                (rs, n) -> new ResourceResponse(rs.getLong("id"), rs.getString("name"), rs.getString("detail")),
                sceneId, t.name());
    }
}