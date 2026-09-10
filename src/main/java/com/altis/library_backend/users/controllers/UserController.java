package com.altis.library_backend.users.controllers;

import com.altis.library_backend.users.models.dtos.UserRequestDTO;
import com.altis.library_backend.users.models.dtos.UserResponseDTO;
import com.altis.library_backend.users.models.dtos.UpdateRequestDTO;
import com.altis.library_backend.users.models.dtos.UpdateResponseDTO;
import com.altis.library_backend.users.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> create(@RequestBody @Valid UserRequestDTO request){
        UserResponseDTO response = userService.createUser(request);

        URI location = URI.create("/users" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getById(@PathVariable Long id){
        UserResponseDTO response = userService.findById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdateResponseDTO> update(@PathVariable Long id, @RequestBody @Valid UpdateRequestDTO request) {
        UpdateResponseDTO response = userService.updateUser(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserResponseDTO> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
