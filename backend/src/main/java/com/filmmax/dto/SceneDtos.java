package com.filmmax.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public class SceneDtos {

    public record SceneRequest(
            @NotNull @Positive Integer sceneNumber,
            @NotBlank String title,
            String description,
            String locationName,
            LocalDate shootDate,
            String timeSlot) {}

    public record SceneResponse(
            Long id,
            int sceneNumber,
            String title,
            String description,
            String locationName,
            LocalDate shootDate,
            String timeSlot,
            String createdBy) {}
}