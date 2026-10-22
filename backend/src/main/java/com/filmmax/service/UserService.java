package com.filmmax.service;

import com.filmmax.dao.UserDao;
import com.filmmax.dto.UserDtos.UserResponse;
import com.filmmax.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private static final Set<String> ALLOWED_ROLES = Set.of(
            "DIRECTOR", "LINE_PRODUCER", "SCRIPTWRITER", "CASTING_MANAGER",
            "STYLIST", "EQUIPMENT_MANAGER", "LOCATION_MANAGER");

    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public List<UserResponse> list() {
        return userDao.findAll().stream().map(this::toResponse).toList();
    }

    public UserResponse changeRole(long id, String role) {
        if (!ALLOWED_ROLES.contains(role)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown role");
        }
        if (!userDao.updateRole(id, role)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return userDao.findById(id).map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private UserResponse toResponse(User u) {
        return new UserResponse(u.getId(), u.getUsername(), u.getEmail(),
                u.getFullName(), u.getRole(), u.isEnabled());
    }
}