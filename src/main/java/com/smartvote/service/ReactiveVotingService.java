package com.smartvote.service;

import com.smartvote.model.*;
import com.smartvote.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReactiveVotingService {

    private final VoteRepository voteRepository;
    private final EventRepository eventRepository;
    private final CandidateRepository candidateRepository;
    private final FaceRecognitionService faceRecognitionService;

    public Mono<Vote> castVote(String eventId, String voterId, String candidateId, String faceImage) {
        return eventRepository.findById(eventId)
            .flatMap(event -> {
                if (!event.getIsActive()) {
                    return Mono.error(new RuntimeException("Event is not active"));
                }
                if (event.hasVoted(voterId)) {
                    return Mono.error(new RuntimeException("Voter has already voted in this event"));
                }
                return candidateRepository.findById(candidateId)
                    .flatMap(candidate -> {
                        if (!candidate.getEventId().equals(eventId)) {
                            return Mono.error(new RuntimeException("Candidate does not belong to this event"));
                        }
                        return faceRecognitionService.verifyFace(faceImage, voterId)
                            .flatMap(verificationId -> {
                                Vote vote = new Vote();
                                vote.setEventId(eventId);
                                vote.setVoterId(voterId);
                                vote.setCandidateId(candidateId);
                                vote.setVotedAt(LocalDateTime.now());
                                vote.setFaceVerificationId(verificationId);
                                vote.setStatus("PENDING");
                                vote.setCreatedAt(LocalDateTime.now());
                                vote.setUpdatedAt(LocalDateTime.now());

                                return voteRepository.save(vote)
                                    .flatMap(savedVote -> {
                                        candidate.incrementVoteCount();
                                        return candidateRepository.save(candidate)
                                            .thenReturn(savedVote);
                                    });
                            });
                    });
            });
    }

    public Mono<Vote> confirmVote(String voteId) {
        return voteRepository.findById(voteId)
            .flatMap(vote -> {
                vote.setStatus("CONFIRMED");
                vote.setIsValid(true);
                vote.setUpdatedAt(LocalDateTime.now());
                return voteRepository.save(vote);
            });
    }

    public Mono<Vote> rejectVote(String voteId) {
        return voteRepository.findById(voteId)
            .flatMap(vote -> {
                vote.setStatus("REJECTED");
                vote.setIsValid(false);
                vote.setUpdatedAt(LocalDateTime.now());
                return voteRepository.save(vote)
                    .flatMap(savedVote -> 
                        candidateRepository.findById(vote.getCandidateId())
                            .flatMap(candidate -> {
                                candidate.decrementVoteCount();
                                return candidateRepository.save(candidate)
                                    .thenReturn(savedVote);
                            })
                    );
            });
    }

    public Flux<Vote> getVotesByEvent(String eventId) {
        return voteRepository.findByEventId(eventId);
    }

    public Flux<Vote> getVotesByVoter(String voterId) {
        return voteRepository.findByVoterId(voterId);
    }

    public Mono<Vote> getVoteByEventAndVoter(String eventId, String voterId) {
        return voteRepository.findByEventIdAndVoterId(eventId, voterId);
    }

    public Flux<Vote> getPendingVotes() {
        return voteRepository.findByStatus("PENDING");
    }
} 