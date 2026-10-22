package com.filmmax.controller;

import com.filmmax.dto.UserDtos.RoleRequest;
import com.filmmax.dto.UserDtos.UserResponse;
import com.filmmax.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('DIRECTOR','LINE_PRODUCER')")
    public List<UserResponse> list() {
        return userService.list();
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('DIRECTOR')")
    public UserResponse changeRole(@PathVariable long id, @Valid @RequestBody RoleRequest req) {
        return userService.changeRole(id, req.role());
    }
}