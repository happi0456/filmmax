package com.filmmax.controller;

import com.filmmax.dto.ResourceDtos.AttachRequest;
import com.filmmax.dto.ResourceDtos.CallSheet;
import com.filmmax.dto.ResourceDtos.ResourceRequest;
import com.filmmax.dto.ResourceDtos.ResourceResponse;
import com.filmmax.service.ResourceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductionController {

    private final ResourceService resourceService;

    public ProductionController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping("/resources/{type}")
    public List<ResourceResponse> list(@PathVariable String type) {
        return resourceService.list(type);
    }

    @PostMapping("/resources/{type}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResourceResponse create(@PathVariable String type,
                                   @Valid @RequestBody ResourceRequest req,
                                   Authentication auth) {
        return resourceService.create(type, req, auth);
    }

    @DeleteMapping("/resources/{type}/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String type, @PathVariable long id, Authentication auth) {
        resourceService.delete(type, id, auth);
    }

    @GetMapping("/scenes/{sceneId}/callsheet")
    public CallSheet callSheet(@PathVariable long sceneId) {
        return resourceService.callSheet(sceneId);
    }

    @PostMapping("/scenes/{sceneId}/resources")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void attach(@PathVariable long sceneId,
                       @Valid @RequestBody AttachRequest req,
                       Authentication auth) {
        resourceService.attach(sceneId, req, auth);
    }

    @DeleteMapping("/scenes/{sceneId}/resources/{type}/{resourceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void detach(@PathVariable long sceneId,
                       @PathVariable String type,
                       @PathVariable long resourceId,
                       Authentication auth) {
        resourceService.detach(sceneId, type, resourceId, auth);
    }
}