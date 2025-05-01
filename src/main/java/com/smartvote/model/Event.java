package com.smartvote.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "events")
public class Event {
    @Id
    private String id;
    
    private String title;
    private String description;
    private String organizerId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean isActive;
    private List<String> candidateIds;
    private List<String> voterIds; // List of voters who have already voted
    private String status; // UPCOMING, ACTIVE, COMPLETED, CANCELLED
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 