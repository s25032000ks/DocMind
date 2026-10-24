package com.ai.docMind.service;

import com.ai.docMind.dto.LoginRequestDTO;
import com.ai.docMind.dto.LoginResponseDTO;
import com.ai.docMind.dto.UserDto;
import com.ai.docMind.dto.UserRegistrationDTO;
import com.ai.docMind.entity.Role;
import com.ai.docMind.entity.Users;
import com.ai.docMind.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDto registerUser(@Valid UserRegistrationDTO userRegistrationDTO) {
        Users users = Users.builder()
                .userName(userRegistrationDTO.userName())
                .password(passwordEncoder.encode(userRegistrationDTO.password()))
                .role(Role.USER)
                .email(userRegistrationDTO.email())
                .build();
        Users registeredUser = userRepository.save(users);

        return new UserDto(registeredUser.getUserId(),
                registeredUser.getUserName(),
                registeredUser.getRole(),
                registeredUser.getEmail());
    }

}
