package com.ofss.project.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ofss.project.dto.request.CreateStaffUserRequest;
import com.ofss.project.dto.response.UserResponse;
import com.ofss.project.entity.User;
import com.ofss.project.enums.UserRole;
import com.ofss.project.enums.UserStatus;
import com.ofss.project.exception.EmailAlreadyExistsException;
import com.ofss.project.exception.InvalidStaffRoleException;
import com.ofss.project.exception.MobileAlreadyExistsException;
import com.ofss.project.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse createStaffUser(
            CreateStaffUserRequest request) {

        validateStaffRole(request.role());

        String email = request.email()
                .trim()
                .toLowerCase();

        String mobile = request.mobile()
                .trim();

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
                .passwordHash(
                        passwordEncoder.encode(request.password())
                )
                .status(UserStatus.ACTIVE)
                .role(request.role())
                .build();

        User savedUser = userRepository.save(user);

        return UserResponse.from(savedUser);
    }
    
    @Transactional(readOnly = true)
    public List<UserResponse> getStaffUsers() {

        List<UserRole> staffRoles = List.of(
                UserRole.CREDIT_OFFICER,
                UserRole.MANAGER
        );

        return userRepository
                .findByRoleInOrderByCreatedAtDesc(staffRoles)
                .stream()
                .map(UserResponse::from)
                .toList();
    }

    private void validateStaffRole(UserRole role) {

        if (role != UserRole.CREDIT_OFFICER
                && role != UserRole.MANAGER) {

            throw new InvalidStaffRoleException(
                    "Admin can create only CREDIT_OFFICER or MANAGER accounts"
            );
        }
    }
}