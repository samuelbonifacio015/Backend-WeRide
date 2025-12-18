package org.example.backendweride.platform.user.interfaces.rest.transform;

import org.example.backendweride.platform.user.domain.model.aggregates.User;
import org.example.backendweride.platform.user.interfaces.rest.resources.UserResource;

public class UserResourceFromEntityAssembler {
    public static UserResource toResourceFromEntity(User entity) {
        var preferences = new UserResource.PreferencesResource(
                entity.getPreferences().getLanguage(),
                entity.getPreferences().getNotifications(),
                entity.getPreferences().getTheme()
        );

        var statistics = new UserResource.StatisticsResource(
                entity.getStatistics().getTotalTrips(),
                entity.getStatistics().getTotalDistance(),
                entity.getStatistics().getTotalSpent(),
                entity.getStatistics().getAverageRating()
        );

        return new UserResource(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getPhone(),
                entity.getMembershipPlanId(),
                entity.getIsActive(),
                entity.getProfilePicture(),
                entity.getDateOfBirth(),
                entity.getAddress(),
                entity.getEmergencyContact(),
                entity.getVerificationStatus().getStatus(),
                entity.getRegistrationDate(),
                preferences,
                statistics
        );
    }
}
