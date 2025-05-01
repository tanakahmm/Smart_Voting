package com.smartvote.controller;

import com.smartvote.model.Vote;
import com.smartvote.service.ReactiveVotingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
public class ReactiveVotingController {

    private final ReactiveVotingService votingService;

    @PostMapping("/cast")
    public Mono<ResponseEntity<Vote>> castVote(
            @RequestBody Map<String, String> voteRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        return votingService.castVote(
                voteRequest.get("eventId"),
                userDetails.getUsername(),
                voteRequest.get("candidateId"),
                voteRequest.get("faceImage")
            )
            .map(ResponseEntity::ok)
            .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @PutMapping("/{id}/confirm")
    public Mono<ResponseEntity<Vote>> confirmVote(@PathVariable String id) {
        return votingService.confirmVote(id)
            .map(ResponseEntity::ok)
            .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @PutMapping("/{id}/reject")
    public Mono<ResponseEntity<Vote>> rejectVote(@PathVariable String id) {
        return votingService.rejectVote(id)
            .map(ResponseEntity::ok)
            .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @GetMapping("/event/{eventId}")
    public Flux<Vote> getVotesByEvent(@PathVariable String eventId) {
        return votingService.getVotesByEvent(eventId);
    }

    @GetMapping("/voter")
    public Flux<Vote> getVotesByVoter(@AuthenticationPrincipal UserDetails userDetails) {
        return votingService.getVotesByVoter(userDetails.getUsername());
    }

    @GetMapping("/event/{eventId}/voter")
    public Mono<ResponseEntity<Vote>> getVoteByEventAndVoter(
            @PathVariable String eventId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return votingService.getVoteByEventAndVoter(eventId, userDetails.getUsername())
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/pending")
    public Flux<Vote> getPendingVotes() {
        return votingService.getPendingVotes();
    }
} 