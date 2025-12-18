package org.example.backendweride.platform.user.domain.model.aggregates;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import org.example.backendweride.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import org.example.backendweride.platform.user.domain.model.commands.CreateUserCommand;
import org.example.backendweride.platform.user.domain.model.valueobjects.UserPreferences;
import org.example.backendweride.platform.user.domain.model.valueobjects.UserStatistics;
import org.example.backendweride.platform.user.domain.model.valueobjects.VerificationStatus;

/**
 * User aggregate root representing a user in the system.
 * Extends AuditableAbstractAggregateRoot to include auditing fields.
 */
@Getter
@Entity
@Table(name = "users")
public class User extends AuditableAbstractAggregateRoot<User> {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 20)
    private String phone;

    @Column(name = "membership_plan_id")
    private String membershipPlanId;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "profile_picture")
    private String profilePicture;

    @Column(name = "date_of_birth")
    private String dateOfBirth;

    @Column(length = 255)
    private String address;

    @Column(name = "emergency_contact", length = 20)
    private String emergencyContact;

    @Embedded
    private VerificationStatus verificationStatus;

    @Column(name = "registration_date")
    private String registrationDate;

    @Embedded
    private UserPreferences preferences;

    @Embedded
    private UserStatistics statistics;

    protected User() {}

    public User(CreateUserCommand command) {
        this.name = command.name();
        this.email = command.email();
        this.password = command.password();
        this.phone = command.phone();
        this.membershipPlanId = command.membershipPlanId();
        this.isActive = true;
        this.profilePicture = command.profilePicture();
        this.dateOfBirth = command.dateOfBirth();
        this.address = command.address();
        this.emergencyContact = command.emergencyContact();
        this.verificationStatus = new VerificationStatus("pending");
        this.registrationDate = java.time.Instant.now().toString();
        this.preferences = new UserPreferences("es", true, "light");
        this.statistics = new UserStatistics();
    }

    public void updateProfile(String name, String phone, String address, String profilePicture) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.profilePicture = profilePicture;
    }

    public void verifyUser() {
        this.verificationStatus = new VerificationStatus("verified");
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void activate() {
        this.isActive = true;
    }

    public void updateStatistics(int totalTrips, double totalDistance, double totalSpent, double averageRating) {
        this.statistics = new UserStatistics(totalTrips, totalDistance, totalSpent, averageRating);
    }
}

