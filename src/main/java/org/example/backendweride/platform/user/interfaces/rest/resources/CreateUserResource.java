package org.example.backendweride.platform.user.interfaces.rest.resources;

public record CreateUserResource(
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
