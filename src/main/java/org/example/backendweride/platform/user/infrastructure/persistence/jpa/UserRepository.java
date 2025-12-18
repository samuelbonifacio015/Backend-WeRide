package org.example.backendweride.platform.user.infrastructure.persistence.jpa;

import org.example.backendweride.platform.user.domain.model.aggregates.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
