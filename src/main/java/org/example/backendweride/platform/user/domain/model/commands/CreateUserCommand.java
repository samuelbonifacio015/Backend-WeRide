package org.example.backendweride.platform.user.domain.model.commands;

public record CreateUserCommand(
        String name,
        String email,
        String password,
        String phone,
        String membershipPlanId,
        String profilePicture,
        String dateOfBirth,
        String address,
        String emergencyContact
) {}
