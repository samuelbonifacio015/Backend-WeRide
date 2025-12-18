package org.example.backendweride.platform.user.interfaces.rest.resources;

public record UserResource(
        Long id,
        String name,
        String email,
        String phone,
        String membershipPlanId,
        Boolean isActive,
        String profilePicture,
        String dateOfBirth,
        String address,
        String emergencyContact,
        String verificationStatus,
        String registrationDate,
        PreferencesResource preferences,
        StatisticsResource statistics
) {
    public record PreferencesResource(String language, Boolean notifications, String theme) {}
    public record StatisticsResource(Integer totalTrips, Double totalDistance, Double totalSpent, Double averageRating) {}
}
