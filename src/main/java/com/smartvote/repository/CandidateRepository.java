package com.smartvote.repository;

import com.smartvote.model.Candidate;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CandidateRepository extends R2dbcRepository<Candidate, String> {
    Flux<Candidate> findByEventId(String eventId);
    Flux<Candidate> findByEventIdOrderByVoteCountDesc(String eventId);
    Mono<Boolean> existsByIdAndEventId(String id, String eventId);
} 