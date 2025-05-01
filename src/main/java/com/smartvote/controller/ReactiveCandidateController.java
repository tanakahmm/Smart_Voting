package com.smartvote.controller;

import com.smartvote.model.Candidate;
import com.smartvote.service.ReactiveCandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/candidates")
@RequiredArgsConstructor
public class ReactiveCandidateController {

    private final ReactiveCandidateService candidateService;

    @PostMapping("/event/{eventId}")
    public Mono<ResponseEntity<Candidate>> createCandidate(
            @PathVariable String eventId,
            @RequestBody Candidate candidate,
            @AuthenticationPrincipal UserDetails userDetails) {
        return candidateService.createCandidate(candidate, eventId)
            .map(ResponseEntity::ok)
            .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @PutMapping("/{id}/event/{eventId}")
    public Mono<ResponseEntity<Candidate>> updateCandidate(
            @PathVariable String id,
            @PathVariable String eventId,
            @RequestBody Candidate candidate,
            @AuthenticationPrincipal UserDetails userDetails) {
        return candidateService.updateCandidate(id, candidate, eventId)
            .map(ResponseEntity::ok)
            .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @DeleteMapping("/{id}/event/{eventId}")
    public Mono<ResponseEntity<Void>> deleteCandidate(
            @PathVariable String id,
            @PathVariable String eventId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return candidateService.deleteCandidate(id, eventId)
            .then(Mono.just(ResponseEntity.ok().<Void>build()))
            .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Candidate>> getCandidate(@PathVariable String id) {
        return candidateService.getCandidateById(id)
            .map(ResponseEntity::ok)
            .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build()));
    }

    @GetMapping("/event/{eventId}")
    public Flux<Candidate> getCandidatesByEvent(@PathVariable String eventId) {
        return candidateService.getCandidatesByEvent(eventId);
    }

    @GetMapping("/event/{eventId}/results")
    public Flux<Candidate> getCandidatesByEventOrderByVotes(@PathVariable String eventId) {
        return candidateService.getCandidatesByEventOrderByVotes(eventId);
    }
} 