package com.smartvote.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "votes")
public class Vote {
    @Id
    private String id;
    
    private String eventId;
    private String voterId;
    private String candidateId;
    private LocalDateTime votedAt;
    private String faceVerificationId; // Reference to face verification record
    private boolean isValid;
    private String status; // PENDING, CONFIRMED, REJECTED
} 