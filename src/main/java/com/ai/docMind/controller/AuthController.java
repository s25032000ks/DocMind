package com.ai.docMind.controller;

import com.ai.docMind.dto.LoginRequestDTO;
import com.ai.docMind.dto.LoginResponseDTO;
import com.ai.docMind.dto.UserDto;
import com.ai.docMind.dto.UserRegistrationDTO;
import com.ai.docMind.entity.Users;
import com.ai.docMind.repository.UserRepository;
import com.ai.docMind.service.JwtService;
import com.ai.docMind.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication Controller",
        description = "Endpoint for authentication")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user",
            description = "This api is used to register a new user")
    public ResponseEntity<UserDto> registerUser(@Valid @RequestBody UserRegistrationDTO userRegistrationDTO){
        UserDto userDto = userService.registerUser(userRegistrationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }

    @PostMapping("/login")
    @Operation(summary = "Login a user",
            description = "This api is used to login a user")
    public ResponseEntity<LoginResponseDTO> loginUser(@Valid @RequestBody LoginRequestDTO loginRequest){
        Authentication authentication = new UsernamePasswordAuthenticationToken(loginRequest.userName(), loginRequest.password());
        Authentication authenticated = authenticationManager.authenticate(authentication);
        Users user = userRepository.findByUserName(loginRequest.userName()).orElseThrow(() -> new RuntimeException("User not found"));

        LoginResponseDTO loginResponse = userService.loginUser(loginRequest);
        return ResponseEntity.ok(loginResponse);
    }

}
