package org.example.backendweride.platform.user.domain.services;

import org.example.backendweride.platform.user.domain.model.aggregates.User;
import org.example.backendweride.platform.user.domain.model.queries.*;
import java.util.List;
import java.util.Optional;

public interface UserQueryService {
    Optional<User> handle(GetUserByIdQuery query);
    Optional<User> handle(GetUserByEmailQuery query);
    List<User> handle(GetAllUsersQuery query);
}