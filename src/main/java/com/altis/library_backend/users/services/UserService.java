package com.altis.library_backend.users.services;

import com.altis.library_backend.users.models.dtos.UpdateRequestDTO;
import com.altis.library_backend.users.models.dtos.UserRequestDTO;
import com.altis.library_backend.users.models.dtos.UserResponseDTO;
import com.altis.library_backend.users.models.dtos.UpdateResponseDTO;
import com.altis.library_backend.users.models.entities.Users;
import com.altis.library_backend.users.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findById(Long id){

        Users findUser = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found with ID: "+id));

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

    @Transactional
    public UserResponseDTO createUser(UserRequestDTO request){
        if (userRepository.existsByCpf(request.cpf())){
            throw new IllegalArgumentException("CPF already in use");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already in use");
        }

        Users newUser = new Users();
        newUser.setNameCompleted(request.nameCompleted());
        newUser.setEmail(request.email());
        newUser.setPhone(request.phone());
        newUser.setCpf(request.cpf());
        newUser.setDateBirth(request.dateBirth());
        newUser.setAddress(request.address());
        newUser.setPassword(request.password());

        newUser.setIsAdmin(false);
        newUser.setIsDisabled(false);
        Users savedUser = userRepository.save(newUser);

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
        Users existingUser = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));

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

        Users savedUser = userRepository.save(existingUser);

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
