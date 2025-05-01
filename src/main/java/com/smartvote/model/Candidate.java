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
@Table("candidates")
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

    public void incrementVoteCount() {
        this.voteCount++;
    }

    public void decrementVoteCount() {
        if (this.voteCount > 0) {
            this.voteCount--;
        }
    }
} 