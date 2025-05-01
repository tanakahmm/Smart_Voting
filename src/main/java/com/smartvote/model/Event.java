package com.smartvote.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("events")
public class Event {
    @Id
    private String id;
    
    private String title;
    private String description;
    private String organizerId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean isActive;
    private List<String> candidateIds = new ArrayList<>();
    private List<String> voterIds = new ArrayList<>();
    private String status; // UPCOMING, ACTIVE, COMPLETED, CANCELLED
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void setIsActive(boolean active) {
        this.isActive = active;
    }

    public boolean getIsActive() {
        return this.isActive;
    }

    public void addCandidate(String candidateId) {
        if (!candidateIds.contains(candidateId)) {
            candidateIds.add(candidateId);
        }
    }

    public void addVoter(String voterId) {
        if (!voterIds.contains(voterId)) {
            voterIds.add(voterId);
        }
    }

    public boolean hasVoted(String voterId) {
        return voterIds.contains(voterId);
    }
} 