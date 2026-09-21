package com.altis.library_backend.users.controllers;

import com.altis.library_backend.auth.models.dtos.ForgotPasswordDTO;
import com.altis.library_backend.users.models.dtos.*;
import com.altis.library_backend.users.models.entities.UserEntity;
import com.altis.library_backend.users.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAll() {
        List<UserResponseDTO> responses = userService.findAll();

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getById(@PathVariable Long id) {
        UserResponseDTO response = userService.findById(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getMe(
            @AuthenticationPrincipal UserEntity loggedUser
    ) {
        UserResponseDTO response = userService.findById(loggedUser.getId());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/me")
    public ResponseEntity<UpdateResponseDTO> updateMe(
            @AuthenticationPrincipal UserEntity loggedUser,
            @RequestBody @Valid UpdateRequestDTO request
    ) {
        UpdateResponseDTO response =
                userService.updateUser(loggedUser.getId(), request);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> adminUpdate(
            @PathVariable Long id,
            @RequestBody @Valid UserRequestDTO request
    ) {
        UserResponseDTO response =
                userService.adminUpdateUser(id, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }
}