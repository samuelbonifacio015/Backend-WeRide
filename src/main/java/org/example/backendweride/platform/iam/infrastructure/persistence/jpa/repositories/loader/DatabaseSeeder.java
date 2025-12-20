package org.example.backendweride.platform.iam.infrastructure.persistence.jpa.repositories.loader;

import org.example.backendweride.platform.iam.domain.model.entities.Role;
import org.example.backendweride.platform.iam.domain.model.valueobjects.Roles;
import org.example.backendweride.platform.iam.infrastructure.persistence.jpa.repositories.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public DatabaseSeeder(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        Arrays.stream(Roles.values()).forEach(roleName -> {
            if (!roleRepository.existsByName(roleName)) {
                roleRepository.save(new Role(roleName));
                System.out.println("Role created: " + roleName);
            }
        });
        System.out.println("Database seeding completed.");
    }
}