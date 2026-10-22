package com.filmmax.dto;

import jakarta.validation.constraints.NotBlank;

public class UserDtos {

    public record UserResponse(
            Long id,
            String username,
            String email,
            String fullName,
            String role,
            boolean enabled) {}

    public record RoleRequest(@NotBlank String role) {}
}