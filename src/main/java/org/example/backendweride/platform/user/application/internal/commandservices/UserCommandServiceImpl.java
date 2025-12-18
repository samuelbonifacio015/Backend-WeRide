package org.example.backendweride.platform.user.application.internal.commandservices;

import org.example.backendweride.platform.user.domain.model.aggregates.User;
import org.example.backendweride.platform.user.domain.model.commands.CreateUserCommand;
import org.example.backendweride.platform.user.domain.model.commands.UpdateUserPorfileCommand;
import org.example.backendweride.platform.user.domain.model.commands.VerifyUserCommand;
import org.example.backendweride.platform.user.domain.services.UserCommandService;
import org.example.backendweride.platform.user.infrastructure.persistence.jpa.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;

    public UserCommandServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<Long> handle(CreateUserCommand command) {
        if (userRepository.existsByEmail(command.email())) {
            throw new IllegalArgumentException("User with email " + command.email() + " already exists");
        }
        var user = new User(command);
        userRepository.save(user);
        return Optional.of(user.getId());
    }

    @Override
    public boolean handle(UpdateUserPorfileCommand command) {
        userRepository.findById(command.userId())
                .map(user -> {
                    user.updateProfile(command.name(), command.phone(),
                            command.address(), command.profilePicture());
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + command.userId()));
        return true;
    }

    @Override
    public boolean handle(VerifyUserCommand command) {
        userRepository.findById(command.userId())
                .map(user -> {
                    user.verifyUser();
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + command.userId()));
        return true;
    }
}
