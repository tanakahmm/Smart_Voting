package com.smartvote.service;

import com.smartvote.model.Event;
import com.smartvote.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReactiveEventService {

    private final EventRepository eventRepository;

    public Mono<Event> createEvent(Event event) {
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());
        event.setStatus("UPCOMING");
        event.setIsActive(false);
        return eventRepository.save(event);
    }

    public Mono<Event> updateEvent(String id, Event event, String organizerId) {
        return eventRepository.existsByIdAndOrganizerId(id, organizerId)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new RuntimeException("Event not found or unauthorized"));
                }
                return eventRepository.findById(id)
                    .flatMap(existingEvent -> {
                        existingEvent.setTitle(event.getTitle());
                        existingEvent.setDescription(event.getDescription());
                        existingEvent.setStartDate(event.getStartDate());
                        existingEvent.setEndDate(event.getEndDate());
                        existingEvent.setUpdatedAt(LocalDateTime.now());
                        return eventRepository.save(existingEvent);
                    });
            });
    }

    public Mono<Void> deleteEvent(String id, String organizerId) {
        return eventRepository.existsByIdAndOrganizerId(id, organizerId)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new RuntimeException("Event not found or unauthorized"));
                }
                return eventRepository.deleteById(id);
            });
    }

    public Mono<Event> getEventById(String id) {
        return eventRepository.findById(id)
            .switchIfEmpty(Mono.error(new RuntimeException("Event not found")));
    }

    public Flux<Event> getEventsByOrganizer(String organizerId) {
        return eventRepository.findByOrganizerId(organizerId);
    }

    public Flux<Event> getActiveEvents() {
        return eventRepository.findByIsActive(true);
    }

    public Mono<Event> updateEventStatus(String id, String status, String organizerId) {
        return eventRepository.existsByIdAndOrganizerId(id, organizerId)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new RuntimeException("Event not found or unauthorized"));
                }
                return eventRepository.findById(id)
                    .flatMap(event -> {
                        event.setStatus(status);
                        event.setIsActive(status.equals("ACTIVE"));
                        event.setUpdatedAt(LocalDateTime.now());
                        return eventRepository.save(event);
                    });
            });
    }

    public Flux<Event> getEventsByStatus(String status) {
        return eventRepository.findByStatus(status);
    }
} 