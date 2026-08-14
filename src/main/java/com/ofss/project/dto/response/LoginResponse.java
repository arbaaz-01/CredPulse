package com.ofss.project.dto.response;

public record LoginResponse(
        Long userId,
        String name,
        String email,
        String role,
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn
) {
}
