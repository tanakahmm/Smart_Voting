package com.smartvote.repository;

import com.smartvote.model.Vote;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface VoteRepository extends R2dbcRepository<Vote, String> {
    Flux<Vote> findByEventId(String eventId);
    Flux<Vote> findByVoterId(String voterId);
    Mono<Vote> findByEventIdAndVoterId(String eventId, String voterId);
    Mono<Boolean> existsByEventIdAndVoterId(String eventId, String voterId);
    Flux<Vote> findByCandidateId(String candidateId);
    Flux<Vote> findByStatus(String status);
} 