package com.filmmax.dto;

import com.filmmax.dto.SceneDtos.SceneResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class ResourceDtos {

    public record ResourceRequest(
            @NotBlank String name,
            String detail) {}

    public record ResourceResponse(
            Long id,
            String name,
            String detail) {}

    // type is the URL path: "actors", "equipment", "locations", "costumes" or "shots"
    public record AttachRequest(
            @NotBlank String type,
            @NotNull Long resourceId) {}

    public record CallSheet(
            SceneResponse scene,
            List<ResourceResponse> actors,
            List<ResourceResponse> equipment,
            List<ResourceResponse> locations,
            List<ResourceResponse> costumes,
            List<ResourceResponse> shots) {}
}