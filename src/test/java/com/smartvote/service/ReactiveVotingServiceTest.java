package com.smartvote.service;

import com.smartvote.model.*;
import com.smartvote.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ReactiveVotingServiceTest {

    @Mock
    private VoteRepository voteRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private CandidateRepository candidateRepository;
    @Mock
    private FaceRecognitionService faceRecognitionService;

    private ReactiveVotingService votingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        votingService = new ReactiveVotingService(voteRepository, eventRepository, candidateRepository, faceRecognitionService);
    }

    @Test
    void castVote_Success() {
        // Arrange
        String eventId = "event1";
        String voterId = "voter1";
        String candidateId = "candidate1";
        String faceImage = "base64Image";

        Event event = new Event();
        event.setId(eventId);
        event.setIsActive(true);

        Candidate candidate = new Candidate();
        candidate.setId(candidateId);
        candidate.setEventId(eventId);

        Vote vote = new Vote();
        vote.setId("vote1");
        vote.setEventId(eventId);
        vote.setVoterId(voterId);
        vote.setCandidateId(candidateId);

        when(eventRepository.findById(eventId)).thenReturn(Mono.just(event));
        when(candidateRepository.findById(candidateId)).thenReturn(Mono.just(candidate));
        when(faceRecognitionService.verifyFace(faceImage, voterId)).thenReturn(Mono.just("verificationId"));
        when(voteRepository.save(any(Vote.class))).thenReturn(Mono.just(vote));
        when(candidateRepository.save(any(Candidate.class))).thenReturn(Mono.just(candidate));

        // Act & Assert
        StepVerifier.create(votingService.castVote(eventId, voterId, candidateId, faceImage))
            .expectNext(vote)
            .verifyComplete();
    }

    @Test
    void castVote_EventNotActive() {
        // Arrange
        String eventId = "event1";
        String voterId = "voter1";
        String candidateId = "candidate1";
        String faceImage = "base64Image";

        Event event = new Event();
        event.setId(eventId);
        event.setIsActive(false);

        when(eventRepository.findById(eventId)).thenReturn(Mono.just(event));

        // Act & Assert
        StepVerifier.create(votingService.castVote(eventId, voterId, candidateId, faceImage))
            .expectError(RuntimeException.class)
            .verify();
    }

    @Test
    void confirmVote_Success() {
        // Arrange
        String voteId = "vote1";
        Vote vote = new Vote();
        vote.setId(voteId);
        vote.setStatus("PENDING");

        when(voteRepository.findById(voteId)).thenReturn(Mono.just(vote));
        when(voteRepository.save(any(Vote.class))).thenReturn(Mono.just(vote));

        // Act & Assert
        StepVerifier.create(votingService.confirmVote(voteId))
            .expectNextMatches(v -> v.getStatus().equals("CONFIRMED") && v.getIsValid())
            .verifyComplete();
    }
} 