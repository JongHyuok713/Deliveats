package com.example.deliveats.service.user;

import com.example.deliveats.domain.user.User;
import com.example.deliveats.exception.CommonErrorCode;
import com.example.deliveats.exception.CustomException;
import com.example.deliveats.exception.user.UserNotFoundException;
import com.example.deliveats.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void updateAddress(User user, String address) {
        User managed = userRepository.findById(user.getId())
                .orElseThrow(UserNotFoundException::new);
        managed.updateAddress(address);
    }

    @Transactional
    public void updatePhone(User user, String phone) {
        User managed = userRepository.findById(user.getId())
                        .orElseThrow(UserNotFoundException::new);
        userRepository.save(user);
    }

    public void updatePassword(User user, String currentPassword, String newPassword) {

        User managed = userRepository.findById(user.getId())
                .orElseThrow(UserNotFoundException::new);

        if (!passwordEncoder.matches(currentPassword, managed.getPassword())) {
            throw new CustomException(CommonErrorCode.UNAUTHORIZED, "current password mismatch");
        }
        managed.updatePassword(passwordEncoder.encode(newPassword));
    }
}
