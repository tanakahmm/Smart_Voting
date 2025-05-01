package com.smartvote.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "candidates")
public class Candidate {
    @Id
    private String id;
    
    private String eventId;
    private String name;
    private String description;
    private String imageUrl;
    private int voteCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 