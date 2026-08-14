package com.ofss.project.dto.response;

import com.ofss.project.entity.User;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String name,
        String email,
        String mobile,
        String status,
        String role,
        LocalDateTime createdAt
) {

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getMobile(),
                user.getStatus().name(),
                user.getRole().name(),
                user.getCreatedAt()
        );
    }
}
