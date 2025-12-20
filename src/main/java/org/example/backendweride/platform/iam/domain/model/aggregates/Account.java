package org.example.backendweride.platform.iam.domain.model.aggregates;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.backendweride.platform.iam.domain.model.commands.SignUpCommand;
import org.example.backendweride.platform.iam.domain.model.entities.Role;
import org.example.backendweride.platform.iam.domain.model.valueobjects.ProfileId;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "accounts")
public class Account extends AbstractAggregateRoot<Account> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Column(nullable = false, unique = true)
    private String userName;

    @Getter
    @Column(nullable = false)
    private String password;

    @Embedded
    private ProfileId profileId;

    @Getter
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "account_roles",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public Account(String username, String password, List<Role> roles) {
        if (username == null || username.isBlank()) throw new IllegalArgumentException("Username is required");
        if (password == null || password.isBlank()) throw new IllegalArgumentException("Password is required");
        this.userName = username;
        this.password = password;
        this.roles = new HashSet<>(roles);
    }

    public Account(SignUpCommand command, String hashedPassword, Role defaultRole) {
        if (command == null) throw new IllegalArgumentException("SignUpCommand is required");
        if (command.username() == null || command.username().isBlank()) throw new IllegalArgumentException("Username is required");
        if (hashedPassword == null || hashedPassword.isBlank()) throw new IllegalArgumentException("Password is required");
        this.userName = command.username();
        this.password = hashedPassword;
        this.roles = new HashSet<>();
        this.roles.add(defaultRole);
    }

    public Long getId() { return id; }

    public String getUserName() { return userName; }

    public void updateProfileId(ProfileId profileId) { this.profileId = profileId; }
}
