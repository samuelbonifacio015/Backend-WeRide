package org.example.backendweride.platform.user.application.internal.queryservices;

import org.example.backendweride.platform.user.domain.model.aggregates.User;
import org.example.backendweride.platform.user.domain.model.queries.*;
import org.example.backendweride.platform.user.domain.services.UserQueryService;
import org.example.backendweride.platform.user.infrastructure.persistence.jpa.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserQueryServiceImpl implements UserQueryService {

    private final UserRepository userRepository;

    public UserQueryServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> handle(GetUserByIdQuery query) {
        return userRepository.findById(query.userId());
    }

    @Override
    public Optional<User> handle(GetUserByEmailQuery query) {
        return userRepository.findByEmail(query.email());
    }

    @Override
    public List<User> handle(GetAllUsersQuery query) {
        return userRepository.findAll();
    }
}