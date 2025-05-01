package com.smartvote.service;

import com.smartvote.model.User;
import com.smartvote.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class ReactiveUserService implements ReactiveUserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByEmail(username)
            .cast(UserDetails.class);
    }

    public Mono<User> registerUser(User user) {
        return userRepository.existsByEmail(user.getEmail())
            .flatMap(exists -> {
                if (exists) {
                    return Mono.error(new RuntimeException("Email already exists"));
                }
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                return userRepository.save(user);
            });
    }

    public Mono<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Flux<User> findAllUsers() {
        return userRepository.findAll();
    }

    public Mono<User> updateUser(String id, User user) {
        return userRepository.findById(id)
            .flatMap(existingUser -> {
                existingUser.setFirstName(user.getFirstName());
                existingUser.setLastName(user.getLastName());
                if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                    existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
                }
                return userRepository.save(existingUser);
            });
    }

    public Mono<Void> deleteUser(String id) {
        return userRepository.deleteById(id);
    }
} 