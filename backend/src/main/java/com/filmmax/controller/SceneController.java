package com.filmmax.controller;

import com.filmmax.dto.SceneDtos.SceneRequest;
import com.filmmax.dto.SceneDtos.SceneResponse;
import com.filmmax.service.SceneService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scenes")
public class SceneController {

    private final SceneService sceneService;

    public SceneController(SceneService sceneService) {
        this.sceneService = sceneService;
    }

    @GetMapping
    public List<SceneResponse> list() {
        return sceneService.list();
    }

    @GetMapping("/{id}")
    public SceneResponse get(@PathVariable long id) {
        return sceneService.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('DIRECTOR','LINE_PRODUCER','SCRIPTWRITER')")
    public SceneResponse create(@Valid @RequestBody SceneRequest req, Authentication auth) {
        return sceneService.create(req, auth.getName());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('DIRECTOR','LINE_PRODUCER','SCRIPTWRITER')")
    public SceneResponse update(@PathVariable long id, @Valid @RequestBody SceneRequest req) {
        return sceneService.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('DIRECTOR','LINE_PRODUCER','SCRIPTWRITER')")
    public void delete(@PathVariable long id) {
        sceneService.delete(id);
    }
}