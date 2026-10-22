package com.filmmax.service;

import com.filmmax.dao.ResourceDao;
import com.filmmax.dao.SceneDao;
import com.filmmax.dao.SceneResourceDao;
import com.filmmax.dto.ResourceDtos.AttachRequest;
import com.filmmax.dto.ResourceDtos.CallSheet;
import com.filmmax.dto.ResourceDtos.ResourceRequest;
import com.filmmax.dto.ResourceDtos.ResourceResponse;
import com.filmmax.dto.SceneDtos.SceneResponse;
import com.filmmax.model.ResourceType;
import com.filmmax.model.Scene;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ResourceService {

    private final ResourceDao resourceDao;
    private final SceneResourceDao sceneResourceDao;
    private final SceneDao sceneDao;
    private final SceneService sceneService;

    public ResourceService(ResourceDao resourceDao, SceneResourceDao sceneResourceDao,
                           SceneDao sceneDao, SceneService sceneService) {
        this.resourceDao = resourceDao;
        this.sceneResourceDao = sceneResourceDao;
        this.sceneDao = sceneDao;
        this.sceneService = sceneService;
    }

    public List<ResourceResponse> list(String path) {
        return resourceDao.findAll(ResourceType.from(path));
    }

    public ResourceResponse create(String path, ResourceRequest req, Authentication auth) {
        ResourceType type = ResourceType.from(path);
        requireManage(auth, type);
        long id = resourceDao.insert(type, req.name(), req.detail());
        return new ResourceResponse(id, req.name(), req.detail());
    }

    public void delete(String path, long id, Authentication auth) {
        ResourceType type = ResourceType.from(path);
        requireManage(auth, type);
        if (!resourceDao.delete(type, id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, type.getLabel() + " not found");
        }
    }

    // Conflict engine: blocks booking a resource already used by another
    // scene on the same shoot date and time slot.
    public void attach(long sceneId, AttachRequest req, Authentication auth) {
        ResourceType type = ResourceType.from(req.type());
        requireManage(auth, type);

        Scene scene = sceneDao.findById(sceneId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Scene not found"));
        if (scene.getShootDate() == null || scene.getTimeSlot() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Set a shoot date and time slot on the scene before booking resources");
        }
        if (!resourceDao.exists(type, req.resourceId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, type.getLabel() + " not found");
        }

        var clash = sceneResourceDao.findConflictingScene(sceneId, type, req.resourceId());
        if (clash.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    type.getLabel() + " is already booked for scene " + clash.get()
                            + " on the same date and time slot");
        }

        try {
            sceneResourceDao.attach(sceneId, type, req.resourceId());
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Already attached to this scene");
        }
    }

    public void detach(long sceneId, String path, long resourceId, Authentication auth) {
        ResourceType type = ResourceType.from(path);
        requireManage(auth, type);
        if (!sceneResourceDao.detach(sceneId, type, resourceId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not attached to this scene");
        }
    }

    public CallSheet callSheet(long sceneId) {
        SceneResponse scene = sceneService.get(sceneId);
        return new CallSheet(
                scene,
                sceneResourceDao.findAttached(sceneId, ResourceType.ACTOR),
                sceneResourceDao.findAttached(sceneId, ResourceType.EQUIPMENT),
                sceneResourceDao.findAttached(sceneId, ResourceType.LOCATION),
                sceneResourceDao.findAttached(sceneId, ResourceType.COSTUME),
                sceneResourceDao.findAttached(sceneId, ResourceType.SHOT));
    }

    // Directors and line producers manage everything.
    // Other roles manage only their own resource type.
    private void requireManage(Authentication auth, ResourceType type) {
        boolean allowed = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_DIRECTOR")
                        || a.equals("ROLE_LINE_PRODUCER")
                        || a.equals("ROLE_" + type.getManagerRole()));
        if (!allowed) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Your role cannot manage " + type.getLabel().toLowerCase() + "s");
        }
    }
}