package com.ofss.project.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ofss.project.dto.request.LoginRequest;
import com.ofss.project.dto.request.RegisterRequest;
import com.ofss.project.dto.response.LoginResponse;
import com.ofss.project.dto.response.UserResponse;
import com.ofss.project.entity.RefreshToken;
import com.ofss.project.entity.User;
import com.ofss.project.enums.UserRole;
import com.ofss.project.enums.UserStatus;
import com.ofss.project.exception.EmailAlreadyExistsException;
import com.ofss.project.exception.InvalidCredentialsException;
import com.ofss.project.exception.MobileAlreadyExistsException;
import com.ofss.project.repository.UserRepository;
import com.ofss.project.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final RefreshTokenService refreshTokenService;

    @Transactional
    public UserResponse register(RegisterRequest request) {

        String email = request.email().trim().toLowerCase();
        String mobile = request.mobile().trim();

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(
                    "An account with this email already exists"
            );
        }

        if (userRepository.existsByMobile(mobile)) {
            throw new MobileAlreadyExistsException(
                    "An account with this mobile number already exists"
            );
        }

        User user = User.builder()
                .name(request.name().trim())
                .email(email)
                .mobile(mobile)
                .passwordHash(passwordEncoder.encode(request.password()))
                .status(UserStatus.ACTIVE)
                .role(UserRole.USER)
                .build();

        User savedUser = userRepository.save(user);

        return UserResponse.from(savedUser);
    }
    
    public LoginResponse login(LoginRequest request) {

        String email = request.email()
                .trim()
                .toLowerCase();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "Invalid email or password"
                        )
                );

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new InvalidCredentialsException(
                    "Invalid email or password"
            );
        }

        boolean passwordMatches = passwordEncoder.matches(
                request.password(),
                user.getPasswordHash()
        );

        if (!passwordMatches) {
            throw new InvalidCredentialsException(
                    "Invalid email or password"
            );
        }

        String accessToken = jwtService.generateAccessToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );

        String refreshToken =
                refreshTokenService.createRefreshToken(user);

        return new LoginResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                accessToken,
                refreshToken,
                "Bearer",
                900
        );

    }
    
    @Transactional
    public LoginResponse refreshAccessToken(
            String rawRefreshToken) {

        RefreshToken oldRefreshToken =
                refreshTokenService.validateRefreshToken(
                        rawRefreshToken
                );

        User user = oldRefreshToken.getUser();

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new InvalidCredentialsException(
                    "User account is not active"
            );
        }

        String newRefreshToken =
                refreshTokenService.rotateRefreshToken(
                        oldRefreshToken
                );

        String newAccessToken =
                jwtService.generateAccessToken(
                        user.getId(),
                        user.getEmail(),
                        user.getRole().name()
                );

        return new LoginResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                newAccessToken,
                newRefreshToken,
                "Bearer",
                900
        );
    }


    @Transactional
    public void logout(String refreshToken) {
        refreshTokenService.revokeToken(refreshToken);
    }


}
