package org.example.backendweride.platform.user.domain.model.valueobjects;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class UserStatistics {
    private Integer totalTrips;
    private Double totalDistance;
    private Double totalSpent;
    private Double averageRating;

    public UserStatistics() {
        this.totalTrips = 0;
        this.totalDistance = 0.0;
        this.totalSpent = 0.0;
        this.averageRating = 0.0;
    }

    public UserStatistics(Integer totalTrips, Double totalDistance, Double totalSpent, Double averageRating) {
        this.totalTrips = totalTrips;
        this.totalDistance = totalDistance;
        this.totalSpent = totalSpent;
        this.averageRating = averageRating;
    }
}