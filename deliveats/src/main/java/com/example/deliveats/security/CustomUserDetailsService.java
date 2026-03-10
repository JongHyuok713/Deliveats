package com.example.deliveats.security;

import com.example.deliveats.domain.user.User;
import com.example.deliveats.exception.CustomException;
import com.example.deliveats.exception.UserErrorCode;
import com.example.deliveats.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        return new CustomUserDetails(user);
    }
}
