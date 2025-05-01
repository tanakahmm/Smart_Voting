package com.smartvote.repository;

import com.smartvote.model.Event;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface EventRepository extends R2dbcRepository<Event, String> {
    Flux<Event> findByOrganizerId(String organizerId);
    Flux<Event> findByStatus(String status);
    Flux<Event> findByIsActive(boolean isActive);
    Mono<Boolean> existsByIdAndOrganizerId(String id, String organizerId);
} 