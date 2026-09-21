package com.altis.library_backend.auth.controllers;

import com.altis.library_backend.auth.models.dtos.AuthenticationDTO;
import com.altis.library_backend.auth.models.dtos.ForgotPasswordDTO;
import com.altis.library_backend.auth.models.dtos.LoginResponseDTO;
import com.altis.library_backend.auth.models.dtos.RegisterDTO;
import com.altis.library_backend.auth.services.TokenService;
import com.altis.library_backend.users.models.entities.UserEntity;
import com.altis.library_backend.users.repositories.UserRepository;
import com.altis.library_backend.users.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserService userService;

    public AuthenticationController(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            TokenService tokenService,
            UserService userService
    ) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AuthenticationDTO data) {

        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());

        var auth = authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((UserEntity) auth.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterDTO data) {

        if (userRepository.findByEmail(data.email()).isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body("Email already registered");
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());

        UserEntity newUser = new UserEntity(data, encryptedPassword);

        userRepository.save(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(
            @RequestBody @Valid ForgotPasswordDTO request
    ) {
        userService.forgotPassword(request);

        return ResponseEntity.noContent().build();
    }
}