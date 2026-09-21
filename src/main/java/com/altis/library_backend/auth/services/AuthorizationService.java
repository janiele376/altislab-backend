package com.altis.library_backend.auth.services;

import com.altis.library_backend.users.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService implements UserDetailsService {
    private final UserRepository userRepository;

    public AuthorizationService(UserRepository repository) {
        this.userRepository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

            return userRepository.findByEmail(username)
                    .orElseThrow(() ->
                            new UsernameNotFoundException(
                                    "User not found with email: " + username
                            )
                    );
        }
    }
