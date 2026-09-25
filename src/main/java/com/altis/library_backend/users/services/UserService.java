package com.altis.library_backend.users.services;

import com.altis.library_backend.auth.models.dtos.ForgotPasswordRequestDTO;
import com.altis.library_backend.users.models.dtos.UpdateRequestDTO;
import com.altis.library_backend.users.models.dtos.UpdateResponseDTO;
import com.altis.library_backend.users.models.dtos.UserRequestDTO;
import com.altis.library_backend.users.models.dtos.UserResponseDTO;
import com.altis.library_backend.users.models.entities.UserEntity;
import com.altis.library_backend.users.repositories.UserRepository;
import com.altis.library_backend.users.specifications.UserSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findById(Long id) {

        UserEntity findUser = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));

            if (findUser.getEmail().equals("admin@admin.com")) {
                throw new IllegalArgumentException("User not found with ID: " + id);
            }

            return new UserResponseDTO(
                    findUser.getId(),
                    findUser.getNameCompleted(),
                    findUser.getEmail(),
                    findUser.getPhone(),
                    findUser.getCpf(),
                    findUser.getDateBirth(),
                    findUser.getAddress()
            );
    }

    @Transactional(readOnly = true)
    public Page<UserResponseDTO> findAll(String nameCompleted,String email, String cpf, Pageable pageable) {

        Specification<UserEntity> spec = UserSpecification.isNotAdmin()
                .and(UserSpecification.hasName(nameCompleted))
                .and(UserSpecification.hasEmail(email))
                .and(UserSpecification.hasCpf(cpf));

        Page<UserEntity> users = userRepository.findAll(spec, pageable);

        return users.map(user -> new UserResponseDTO(
                user.getId(),
                user.getNameCompleted(),
                user.getEmail(),
                user.getPhone(),
                user.getCpf(),
                user.getDateBirth(),
                user.getAddress()
        ));
    }

    @Transactional
    public UpdateResponseDTO updateUser(Long id, UpdateRequestDTO request) {

        UserEntity existingUser = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));

        if (!passwordEncoder.matches(
                request.currentPassword(),
                existingUser.getPassword()
        )) {
            throw new IllegalArgumentException(
                    "Invalid current password!"
            );
        }

        if (request.email() != null
                && !request.email().isBlank()
                && !existingUser.getEmail().equals(request.email())
                && userRepository.existsByEmail(request.email())) {

            throw new IllegalArgumentException(
                    "Email already in use by another account"
            );
        }

        if (request.nameCompleted() != null && !request.nameCompleted().isBlank()) {
            existingUser.setNameCompleted(request.nameCompleted()
            );
        }

        if (request.email() != null && !request.email().isBlank()) {
            existingUser.setEmail(request.email()
            );
        }

        if (request.address() != null && !request.address().isBlank()) {
            existingUser.setAddress(request.address()
            );
        }

        if (request.phone() != null && !request.phone().isBlank()) {
            existingUser.setPhone(request.phone()
            );
        }

        if (request.password() != null && !request.password().isBlank()) {
            existingUser.setPassword(
                    passwordEncoder.encode(
                            request.password()
                    )
            );
        }

        UserEntity savedUser =
                userRepository.save(existingUser);

        return new UpdateResponseDTO(
                savedUser.getId(),
                savedUser.getNameCompleted(),
                savedUser.getEmail(),
                savedUser.getPhone(),
                savedUser.getAddress()
        );
    }

    @Transactional
    public UserResponseDTO adminUpdateUser(Long id, UserRequestDTO request) {

        UserEntity existingUser = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));

        if (request.email() != null
                && !request.email().isBlank()
                && !existingUser.getEmail().equals(request.email())
                && userRepository.existsByEmail(request.email())) {

            throw new IllegalArgumentException(
                    "Email already in use by another account"
            );
        }

        if (request.nameCompleted() != null && !request.nameCompleted().isBlank()) {
            existingUser.setNameCompleted(request.nameCompleted()
            );
        }

        if (request.email() != null && !request.email().isBlank()) {
            existingUser.setEmail(request.email()
            );
        }

        if (request.phone() != null && !request.phone().isBlank()) {
            existingUser.setPhone(request.phone()
            );
        }

        if (request.address() != null && !request.address().isBlank()) {
            existingUser.setAddress(request.address()
            );
        }

        if (request.cpf() != null && !request.cpf().isBlank()) {
            existingUser.setCpf(request.cpf()
            );
        }

        if (request.dateBirth() != null) {
            existingUser.setDateBirth(request.dateBirth()
            );
        }

        UserEntity savedUser = userRepository.save(existingUser);

        return new UserResponseDTO(
                savedUser.getId(),
                savedUser.getNameCompleted(),
                savedUser.getEmail(),
                savedUser.getPhone(),
                savedUser.getCpf(),
                savedUser.getDateBirth(),
                savedUser.getAddress()
        );
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequestDTO request) {

        UserEntity existingUser = userRepository.findByEmail(request.email()).orElseThrow(() -> new IllegalArgumentException("Invalid email or CPF"));

        if (!existingUser.getCpf().equals(request.cpf())) {
            throw new IllegalArgumentException(
                    "Invalid email or CPF"
            );
        }

        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new IllegalArgumentException(
                    "Passwords do not match"
            );
        }

        existingUser.setPassword(passwordEncoder.encode(request.newPassword()));

        userRepository.save(existingUser);
    }

    @Transactional
    public void deleteUser(Long id) {

        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found with ID: " + id);
        }

        userRepository.deleteById(id);
    }
}