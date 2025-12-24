// Java
package org.example.backendweride.platform.iam.application.internal.commandservices;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.example.backendweride.platform.iam.application.internal.outboundservices.hashing.HashingService;
import org.example.backendweride.platform.iam.application.internal.outboundservices.tokens.TokenService;
import org.example.backendweride.platform.iam.domain.model.aggregates.Account;
import org.example.backendweride.platform.iam.domain.model.commands.SignInCommand;
import org.example.backendweride.platform.iam.domain.model.commands.SignUpCommand;
import org.example.backendweride.platform.iam.domain.model.entities.Role;
import org.example.backendweride.platform.iam.domain.model.valueobjects.ProfileId;
import org.example.backendweride.platform.iam.domain.model.valueobjects.Roles;
import org.example.backendweride.platform.iam.domain.services.AccountCommandService;
import org.example.backendweride.platform.iam.infrastructure.persistence.jpa.repositories.AccountRepository;
import org.example.backendweride.platform.iam.infrastructure.persistence.jpa.repositories.RoleRepository;
import org.example.backendweride.platform.profile.interfaces.acl.ProfileContextFacade;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountCommandServiceImpl implements AccountCommandService {
    private final RoleRepository roleRepository;
    private final AccountRepository accountRepository;
    private final HashingService hashingService;
    private final TokenService tokenService;
    private final ProfileContextFacade profileContextFacade;

    public AccountCommandServiceImpl(AccountRepository accountRepository, HashingService hashingService,
                                     TokenService tokenService, ProfileContextFacade profileContextFacade,
                                     RoleRepository roleRepository) {
        this.accountRepository = accountRepository;
        this.hashingService = hashingService;
        this.tokenService = tokenService;
        this.profileContextFacade = profileContextFacade;
        this.roleRepository = roleRepository;
    }

    @Override
    public Optional<Account> handle(SignUpCommand command) {
        if (accountRepository.existsByUserName(command.username()))
            throw new RuntimeException("User already exists");

        Role role = roleRepository.findByName(Roles.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Role not found. Initialize database first."));

        var account = new Account(command, hashingService.encode(command.password()), role);

        try {
            var accountCreated = accountRepository.save(account);

            Long profileIdValue = profileContextFacade.createProfileForAccount(accountCreated.getId());
            accountCreated.updateProfileId(new ProfileId(profileIdValue));

            var updatedAccount = accountRepository.save(accountCreated);
            return Optional.of(updatedAccount);
        } catch (Exception e) {
            throw new RuntimeException("Error saving user: %s".formatted(e.getMessage()));
        }
    }

    @Override
    public Optional<ImmutablePair<Account, String>> handle(SignInCommand command) {
        var account = accountRepository.findByUserName(command.username());

        if (account.isEmpty() || !hashingService.matches(command.password(), account.get().getPassword())) {
            return Optional.empty();
        }

        if (!hashingService.matches(command.password(), account.get().getPassword()))
            throw new RuntimeException("Invalid password");
        var token = tokenService.generateToken(account.get().getUserName());
        return Optional.of(ImmutablePair.of(account.get(), token));
    }
}
