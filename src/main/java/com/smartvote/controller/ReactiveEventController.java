package com.smartvote.controller;

import com.smartvote.model.Event;
import com.smartvote.service.ReactiveEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class ReactiveEventController {

    private final ReactiveEventService eventService;

    @PostMapping
    public Mono<ResponseEntity<Event>> createEvent(
            @RequestBody Event event,
            @AuthenticationPrincipal UserDetails userDetails) {
        event.setOrganizerId(userDetails.getUsername());
        return eventService.createEvent(event)
            .map(ResponseEntity::ok)
            .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Event>> updateEvent(
            @PathVariable String id,
            @RequestBody Event event,
            @AuthenticationPrincipal UserDetails userDetails) {
        return eventService.updateEvent(id, event, userDetails.getUsername())
            .map(ResponseEntity::ok)
            .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteEvent(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return eventService.deleteEvent(id, userDetails.getUsername())
            .then(Mono.just(ResponseEntity.ok().<Void>build()))
            .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Event>> getEvent(@PathVariable String id) {
        return eventService.getEventById(id)
            .map(ResponseEntity::ok)
            .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build()));
    }

    @GetMapping("/organizer")
    public Flux<Event> getOrganizerEvents(@AuthenticationPrincipal UserDetails userDetails) {
        return eventService.getEventsByOrganizer(userDetails.getUsername());
    }

    @GetMapping("/active")
    public Flux<Event> getActiveEvents() {
        return eventService.getActiveEvents();
    }

    @PutMapping("/{id}/status")
    public Mono<ResponseEntity<Event>> updateEventStatus(
            @PathVariable String id,
            @RequestParam String status,
            @AuthenticationPrincipal UserDetails userDetails) {
        return eventService.updateEventStatus(id, status, userDetails.getUsername())
            .map(ResponseEntity::ok)
            .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @GetMapping("/status/{status}")
    public Flux<Event> getEventsByStatus(@PathVariable String status) {
        return eventService.getEventsByStatus(status);
    }
} 