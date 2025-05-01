package com.smartvote.controller;

import com.smartvote.model.User;
import com.smartvote.security.JwtTokenProvider;
import com.smartvote.service.ReactiveUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class ReactiveAuthController {

    private final ReactiveUserService userService;
    private final ReactiveAuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @PostMapping("/register")
    public Mono<ResponseEntity<?>> register(@RequestBody User user) {
        return userService.registerUser(user)
            .map(savedUser -> ResponseEntity.ok(savedUser))
            .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().body(e.getMessage())));
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<?>> login(@RequestBody Map<String, String> loginRequest) {
        return authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.get("email"),
                loginRequest.get("password")
            )
        )
        .map(authentication -> {
            String jwt = tokenProvider.generateToken(authentication);
            Map<String, String> response = new HashMap<>();
            response.put("token", jwt);
            return ResponseEntity.ok(response);
        })
        .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().body("Invalid credentials")));
    }

    @GetMapping("/me")
    public Mono<ResponseEntity<?>> getCurrentUser(@RequestHeader("Authorization") String token) {
        String email = tokenProvider.getUsernameFromToken(token.substring(7));
        return userService.findByEmail(email)
            .map(user -> ResponseEntity.ok(user))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }
} 