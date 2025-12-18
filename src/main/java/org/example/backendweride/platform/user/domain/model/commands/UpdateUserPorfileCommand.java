package org.example.backendweride.platform.user.domain.model.commands;

public record UpdateUserPorfileCommand(
        Long userId,
        String name,
        String phone,
        String address,
        String profilePicture
) {}
