package com.filmmax.service;

import com.filmmax.dao.SceneDao;
import com.filmmax.dao.UserDao;
import com.filmmax.dto.SceneDtos.SceneRequest;
import com.filmmax.dto.SceneDtos.SceneResponse;
import com.filmmax.model.Scene;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class SceneService {

    private final SceneDao sceneDao;
    private final UserDao userDao;

    public SceneService(SceneDao sceneDao, UserDao userDao) {
        this.sceneDao = sceneDao;
        this.userDao = userDao;
    }

    public List<SceneResponse> list() {
        return sceneDao.findAll().stream().map(this::toResponse).toList();
    }

    public SceneResponse get(long id) {
        return sceneDao.findById(id).map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Scene not found"));
    }

    public SceneResponse create(SceneRequest req, String username) {
        Scene s = fromRequest(req);
        s.setCreatedBy(userDao.findByUsername(username).orElseThrow().getId());
        long id = sceneDao.insert(s);
        return get(id);
    }

    public SceneResponse update(long id, SceneRequest req) {
        if (!sceneDao.update(id, fromRequest(req))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Scene not found");
        }
        return get(id);
    }

    public void delete(long id) {
        if (!sceneDao.delete(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Scene not found");
        }
    }

    private Scene fromRequest(SceneRequest req) {
        Scene s = new Scene();
        s.setSceneNumber(req.sceneNumber());
        s.setTitle(req.title());
        s.setDescription(req.description());
        s.setLocationName(req.locationName());
        s.setShootDate(req.shootDate());
        s.setTimeSlot(req.timeSlot());
        return s;
    }

    private SceneResponse toResponse(Scene s) {
        String creator = userDao.findAll().stream()
                .filter(u -> u.getId().equals(s.getCreatedBy()))
                .map(u -> u.getUsername()).findFirst().orElse("unknown");
        return new SceneResponse(s.getId(), s.getSceneNumber(), s.getTitle(), s.getDescription(),
                s.getLocationName(), s.getShootDate(), s.getTimeSlot(), creator);
    }
}