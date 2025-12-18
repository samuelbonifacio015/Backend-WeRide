package org.example.backendweride.platform.user.domain.services;

import org.example.backendweride.platform.user.domain.model.commands.CreateUserCommand;
import org.example.backendweride.platform.user.domain.model.commands.UpdateUserPorfileCommand;
import org.example.backendweride.platform.user.domain.model.commands.VerifyUserCommand;

import java.util.Optional;

public interface UserCommandService {
    Optional<Long> handle(CreateUserCommand command);
    boolean handle(UpdateUserPorfileCommand command);
    boolean handle(VerifyUserCommand command);
}


