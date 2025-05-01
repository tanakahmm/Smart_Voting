package com.smartvote.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("votes")
public class Vote {
    @Id
    private String id;
    
    private String eventId;
    private String candidateId;
    private String voterId;
    private String status; // PENDING, CONFIRMED, REJECTED
    private boolean isValid;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void setIsValid(boolean isValid) {
        this.isValid = isValid;
    }

    public boolean getIsValid() {
        return this.isValid;
    }
} 