package com.ofss.project.service;

import com.ofss.project.dto.response.UserResponse;
import com.ofss.project.entity.User;
import com.ofss.project.exception.UserNotFoundException;
import com.ofss.project.repository.UserRepository;
import com.ofss.project.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CurrentUser currentUser;

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(
            Authentication authentication) {

        Long userId = currentUser.getUserId(authentication);

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found"
                        )
                );

        return UserResponse.from(user);
    }
}
