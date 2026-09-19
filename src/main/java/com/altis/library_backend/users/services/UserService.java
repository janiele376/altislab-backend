package com.altis.library_backend.users.services;

import com.altis.library_backend.users.models.dtos.UpdateRequestDTO;
import com.altis.library_backend.users.models.dtos.UserRequestDTO;
import com.altis.library_backend.users.models.dtos.UserResponseDTO;
import com.altis.library_backend.users.models.dtos.UpdateResponseDTO;
import com.altis.library_backend.users.models.entities.UserEntity;
import com.altis.library_backend.users.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findById(Long id){

        UserEntity findUser = userRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("User not found with ID: "+id));

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
    public List<UserResponseDTO> findAll() {
        List<UserEntity> users = userRepository.findAll();

        List<UserResponseDTO> responses = new ArrayList<>();

        for (UserEntity user : users) {
            UserResponseDTO response = new UserResponseDTO(
                    user.getId(),
                    user.getNameCompleted(),
                    user.getEmail(),
                    user.getPhone(),
                    user.getCpf(),
                    user.getDateBirth(),
                    user.getAddress()
            );
            responses.add(response);
        }
        return responses;
    }

    @Transactional
    public UserResponseDTO createUser(UserRequestDTO request){
        if (userRepository.existsByCpf(request.cpf())){
            throw new IllegalArgumentException("CPF already in use");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already in use");
        }

        UserEntity newUser = new UserEntity();
        newUser.setNameCompleted(request.nameCompleted());
        newUser.setEmail(request.email());
        newUser.setPhone(request.phone());
        newUser.setCpf(request.cpf());
        newUser.setDateBirth(request.dateBirth());
        newUser.setAddress(request.address());
        newUser.setPassword(request.password());

        newUser.setIsAdmin(false);
        newUser.setIsDisabled(false);
        UserEntity savedUser = userRepository.save(newUser);

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
    public UpdateResponseDTO updateUser(Long id, UpdateRequestDTO request){
        UserEntity existingUser = userRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("User not found with ID: " + id));

        if (!existingUser.getPassword().equals(request.currentPassword())) {
            throw new IllegalArgumentException("Invalid current password!");
        }

        if (request.email() != null && !request.email().isBlank()
                && !existingUser.getEmail().equals(request.email())
                && userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already in use by another account");
        }

        if (request.nameCompleted() != null && !request.nameCompleted().isBlank()) {
            existingUser.setNameCompleted(request.nameCompleted());
        }
        if (request.email() != null && !request.email().isBlank()) {
            existingUser.setEmail(request.email());
        }
        if (request.address() != null && !request.address().isBlank()) {
            existingUser.setAddress(request.address());
        }
        if (request.phone() != null && !request.phone().isBlank()) {
            existingUser.setPhone(request.phone());
        }

        if (request.password() != null && !request.password().isBlank()) {
            existingUser.setPassword(request.password());
        }

        UserEntity savedUser = userRepository.save(existingUser);

        return new UpdateResponseDTO(
                savedUser.getId(),
                savedUser.getNameCompleted(),
                savedUser.getEmail(),
                savedUser.getPhone(),
                savedUser.getAddress()
        );
    }

    @Transactional
    public void deleteUser(Long id){
        if(!userRepository.existsById(id)){
            throw new IllegalArgumentException("User not found with ID: "+id);
        }

        userRepository.deleteById(id);
    }
}
