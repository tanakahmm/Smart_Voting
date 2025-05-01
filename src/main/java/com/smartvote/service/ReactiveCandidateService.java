package com.smartvote.service;

import com.smartvote.model.Candidate;
import com.smartvote.model.Event;
import com.smartvote.repository.CandidateRepository;
import com.smartvote.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReactiveCandidateService {

    private final CandidateRepository candidateRepository;
    private final EventRepository eventRepository;

    public Mono<Candidate> createCandidate(Candidate candidate, String eventId) {
        return eventRepository.findById(eventId)
            .flatMap(event -> {
                candidate.setEventId(eventId);
                candidate.setVoteCount(0);
                candidate.setCreatedAt(LocalDateTime.now());
                candidate.setUpdatedAt(LocalDateTime.now());
                return candidateRepository.save(candidate)
                    .flatMap(savedCandidate -> {
                        event.addCandidate(savedCandidate.getId());
                        return eventRepository.save(event)
                            .thenReturn(savedCandidate);
                    });
            });
    }

    public Mono<Candidate> updateCandidate(String id, Candidate candidate, String eventId) {
        return candidateRepository.findById(id)
            .flatMap(existingCandidate -> {
                if (!existingCandidate.getEventId().equals(eventId)) {
                    return Mono.error(new RuntimeException("Candidate does not belong to this event"));
                }
                existingCandidate.setName(candidate.getName());
                existingCandidate.setDescription(candidate.getDescription());
                existingCandidate.setImageUrl(candidate.getImageUrl());
                existingCandidate.setUpdatedAt(LocalDateTime.now());
                return candidateRepository.save(existingCandidate);
            });
    }

    public Mono<Void> deleteCandidate(String id, String eventId) {
        return candidateRepository.findById(id)
            .flatMap(candidate -> {
                if (!candidate.getEventId().equals(eventId)) {
                    return Mono.error(new RuntimeException("Candidate does not belong to this event"));
                }
                return eventRepository.findById(eventId)
                    .flatMap(event -> {
                        event.getCandidateIds().remove(id);
                        return eventRepository.save(event)
                            .then(candidateRepository.deleteById(id));
                    });
            });
    }

    public Mono<Candidate> getCandidateById(String id) {
        return candidateRepository.findById(id)
            .switchIfEmpty(Mono.error(new RuntimeException("Candidate not found")));
    }

    public Flux<Candidate> getCandidatesByEvent(String eventId) {
        return candidateRepository.findByEventId(eventId);
    }

    public Flux<Candidate> getCandidatesByEventOrderByVotes(String eventId) {
        return candidateRepository.findByEventIdOrderByVoteCountDesc(eventId);
    }
} 