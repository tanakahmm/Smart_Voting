package com.smartvote;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class SmartVoteApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmartVoteApplication.class, args);
    }
} 