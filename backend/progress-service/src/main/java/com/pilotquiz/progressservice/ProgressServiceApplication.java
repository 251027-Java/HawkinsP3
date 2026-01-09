// Generated with assistance from Gemini AI
// Reviewed and modified by Richard Hawkins
package com.pilotquiz.progressservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Progress Service - Tracks quiz attempts, scores, and user progress.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ProgressServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProgressServiceApplication.class, args);
    }
}
