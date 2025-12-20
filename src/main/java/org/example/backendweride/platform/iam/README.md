# IAM Bounded Context - WeRide Platform

## Descripción General

El contexto acotado de **IAM (Identity and Access Management)** es responsable de gestionar la autenticación, autorización y administración de cuentas de usuario en la plataforma WeRide. Este módulo implementa patrones de Domain-Driven Design y sigue la arquitectura por capas con separación clara entre dominio, aplicación, infraestructura e interfaces.

---

## Tabla de Contenidos

1. [Domain Layer](#domain-layer)
2. [Application Layer](#application-layer)
3. [Infrastructure Layer](#infrastructure-layer)
4. [Interfaces Layer](#interfaces-layer)
5. [Arquitectura y Flujos](#arquitectura-y-flujos)

---

## Domain Layer

### Agregados (Aggregates)

#### Account.java
```java
package org.example.backendweride.platform.iam.domain.model.aggregates;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.backendweride.platform.iam.domain.model.commands.SignUpCommand;
import org.example.backendweride.platform.iam.domain.model.valueobjects.ProfileId;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Account Aggregate Root
 *
 * @summary Represents an account in WeRide Platform.
 */
@Entity
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "accounts")
public class Account extends AbstractAggregateRoot<Account> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @NotBlank
    @Getter
    @Size(max=20)
    @Column(unique = true)
    private String userName;

    @NotBlank
    @Getter
    private String password;

    @Embedded
    @Getter
    private ProfileId profileId;

    public Account(String username, String password) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        this.userName = username;
        this.password = password;
    }

    public Account(String username, String password, ProfileId profileId) {
        this(username, password);
        this.profileId = profileId;
    }

    public Account(SignUpCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("SignUpCommand cannot be null");
        }
        if (command.username() == null || command.username().isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (command.password() == null || command.password().isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        this.userName = command.username();
        this.password = command.password();
    }

    public Account(SignUpCommand command, String hashedPassword) {
        if (command == null) {
            throw new IllegalArgumentException("SignUpCommand cannot be null");
        }
        if (command.username() == null || command.username().isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (hashedPassword == null || hashedPassword.isBlank()) {
            throw new IllegalArgumentException("Hashed password cannot be null or empty");
        }
        this.userName = command.username();
        this.password = hashedPassword;
    }

    public Account(SignUpCommand command, String hashedPassword, ProfileId profileId) {
        this(command, hashedPassword);
        this.profileId = profileId;
    }

    public Account updatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        this.password = password;
        return this;
    }

    public Account updateUserName(String userName) {
        if (userName == null || userName.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        this.userName = userName;
        return this;
    }

    public Account updateProfileId(ProfileId profileId) {
        if (profileId == null) {
            throw new IllegalArgumentException("ProfileId cannot be null");
        }
        this.profileId = profileId;
        return this;
    }
}
```

### Comandos (Commands)

#### SignUpCommand.java
```java
package org.example.backendweride.platform.iam.domain.model.commands;

/**
 * SignUpCommand
 *
 * @summary Command to sign up a new user in WeRide Platform.
 */
public record SignUpCommand(String username, String password) {}
```

#### SignInCommand.java
```java
package org.example.backendweride.platform.iam.domain.model.commands;

/**
 * SignInCommand
 *
 * @summary Command to sign in an existing user in WeRide Platform.
 */
public record SignInCommand(String username, String password) {}
```

### Queries (Consultas)

#### GetAccountByIdQuery.java
```java
package org.example.backendweride.platform.iam.domain.model.queries;

/**
 * GetAccountByIdQuery
 *
 * @summary Query to get an account by its ID in WeRide Platform.
 */
public record GetAccountByIdQuery(Long id) {}
```

### Value Objects

#### ProfileId.java
```java
package org.example.backendweride.platform.iam.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

/**
 * ProfileId Value Object
 *
 * @summary Represents a unique identifier for a user profile.
 */
@Embeddable
public record ProfileId(Long profileId) {
    public ProfileId {
        if (profileId == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
    }
}
```

### Domain Services

#### AccountCommandService.java
```java
package org.example.backendweride.platform.iam.domain.services;

import org.example.backendweride.platform.iam.domain.model.aggregates.Account;
import org.example.backendweride.platform.iam.domain.model.commands.SignInCommand;
import org.example.backendweride.platform.iam.domain.model.commands.SignUpCommand;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Account Command Service Interface
 *
 * @summary Defines operations for handling account-related commands.
 */
@Service
public interface AccountCommandService {
    Optional<Account> handle (SignUpCommand command);
    Optional<ImmutablePair<Account, String>> handle(SignInCommand command);
}
```

#### AccountQueryService.java
```java
package org.example.backendweride.platform.iam.domain.services;

import org.example.backendweride.platform.iam.domain.model.aggregates.Account;
import org.example.backendweride.platform.iam.domain.model.queries.GetAccountByIdQuery;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Account Query Service Interface
 *
 * @summary Defines operations for handling account-related queries.
 */
@Service
public interface AccountQueryService {
    Optional<Account> handle (GetAccountByIdQuery query);
}
```

---

## Application Layer

### Command Services

#### AccountCommandServiceImpl.java
```java
package org.example.backendweride.platform.iam.application.internal.commandservices;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.example.backendweride.platform.iam.application.internal.outboundservices.hashing.HashingService;
import org.example.backendweride.platform.iam.application.internal.outboundservices.tokens.TokenService;
import org.example.backendweride.platform.iam.domain.model.aggregates.Account;
import org.example.backendweride.platform.iam.domain.model.commands.SignInCommand;
import org.example.backendweride.platform.iam.domain.model.commands.SignUpCommand;
import org.example.backendweride.platform.iam.domain.model.valueobjects.ProfileId;
import org.example.backendweride.platform.iam.domain.services.AccountCommandService;
import org.example.backendweride.platform.iam.infrastructure.persistence.jpa.repositories.AccountRepository;
import org.example.backendweride.platform.profile.interfaces.acl.ProfileContextFacade;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountCommandServiceImpl implements AccountCommandService {
    private final AccountRepository accountRepository;
    private final HashingService hashingService;
    private final TokenService tokenService;
    private final ProfileContextFacade profileContextFacade;

    public AccountCommandServiceImpl(AccountRepository accountRepository, HashingService hashingService, TokenService tokenService, ProfileContextFacade profileContextFacade) {
        this.accountRepository = accountRepository;
        this.hashingService = hashingService;
        this.tokenService = tokenService;
        this.profileContextFacade = profileContextFacade;
    }

    @Override
    public Optional<Account> handle(SignUpCommand command) {
        if(accountRepository.existsByUserName(command.username()))
            throw new RuntimeException("User already exists");

        var account = new Account(command, hashingService.encode(command.password()));

        try{
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
        var accountExists = accountRepository.existsByUserName(command.username());
        if(accountExists) {
            var account = accountRepository.findByUserName(command.username());
            var token = tokenService.generateToken(account.get().getUserName());
            return Optional.of(ImmutablePair.of(account.get(), token));
        }
        throw new RuntimeException("User not found");
    }
}
```

### Query Services

#### AccountQueryServiceImpl.java
```java
package org.example.backendweride.platform.iam.application.internal.queryservices;

import org.example.backendweride.platform.iam.domain.model.aggregates.Account;
import org.example.backendweride.platform.iam.domain.model.queries.GetAccountByIdQuery;
import org.example.backendweride.platform.iam.domain.services.AccountQueryService;
import org.example.backendweride.platform.iam.infrastructure.persistence.jpa.repositories.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of AccountQueryService to handle account-related queries.
 *
 * @summary This service retrieves account information based on provided queries.
 */
@Service
public class AccountQueryServiceImpl implements AccountQueryService {
    private final AccountRepository accountRepository;

    public AccountQueryServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Optional<Account> handle(GetAccountByIdQuery query) {
        var accountFound = accountRepository.findById(query.id());
        if(accountFound.isEmpty()) throw new IllegalArgumentException("Account not found");
        return accountFound;
    }
}
```

### Outbound Services (Interfaces Abstractas)

#### HashingService.java
```java
package org.example.backendweride.platform.iam.application.internal.outboundservices.hashing;

/**
 * Hashing Service Interface
 *
 * @summary Defines operations for hashing and verifying passwords.
 */
public interface HashingService {
    String encode(CharSequence rawPassword);
    boolean matches(CharSequence rawPassword, String encodedPassword);
}
```

#### TokenService.java
```java
package org.example.backendweride.platform.iam.application.internal.outboundservices.tokens;

/**
 * Token Service Interface
 *
 * @summary Defines operations for generating and validating tokens.
 */
public interface TokenService {

    String generateToken(String username);

    String getUsernameFromToken(String token);

    boolean validateToken(String token);
}
```

---

## Infrastructure Layer

### Persistence

#### AccountRepository.java
```java
package org.example.backendweride.platform.iam.infrastructure.persistence.jpa.repositories;

import org.example.backendweride.platform.iam.domain.model.aggregates.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * AccountRepository interface for managing Account entities in the database.
 *
 * @repository annotation indicates that this interface is a Spring Data repository.
 * @summary This interface extends JpaRepository to provide CRUD operations for Account entities.
 */
@Repository
public interface AccountRepository extends JpaRepository<Account,Long> {
    boolean existsByUserName(String userName);
    Optional<Account> findByUserName(String userName);
}
```

### Hashing Implementation

#### BCryptHashingService.java
```java
package org.example.backendweride.platform.iam.infrastructure.hashing.bcrypt;

import org.example.backendweride.platform.iam.application.internal.outboundservices.hashing.HashingService;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * BCryptHashingService interface that extends both HashingService and PasswordEncoder.
 * This interface provides methods for hashing and verifying passwords using the BCrypt algorithm.
 */
public interface BCryptHashingService extends HashingService, PasswordEncoder {}
```

#### HashingServiceImpl.java
```java
package org.example.backendweride.platform.iam.infrastructure.hashing.bcrypt.services;

import org.example.backendweride.platform.iam.infrastructure.hashing.bcrypt.BCryptHashingService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Implementation of the BCrypt hashing service.
 *
 * @summary This service provides methods to encode and verify passwords using the BCrypt algorithm.
 */
@Service
public class HashingServiceImpl implements BCryptHashingService {
    private final BCryptPasswordEncoder passwordEncoder;

    public HashingServiceImpl() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public String encode(CharSequence rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
```

### JWT Token Implementation

#### BearerTokenService.java
```java
package org.example.backendweride.platform.iam.infrastructure.tokens.jwt;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.example.backendweride.platform.iam.application.internal.outboundservices.tokens.TokenService;

/**
 * BearerTokenService interface that extends TokenService.
 *
 * @summary This interface provides methods for extracting bearer tokens from HTTP requests
 */
public interface BearerTokenService extends TokenService {
    String getBearerTokenFrom(HttpServletRequest token);
    String generateToken(Authentication authentication);
}
```

#### TokenServiceImpl.java
```java
package org.example.backendweride.platform.iam.infrastructure.tokens.jwt.services;

import org.example.backendweride.platform.iam.infrastructure.tokens.jwt.BearerTokenService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

/**
 * Implementation of BearerTokenService for JWT token management.
 *
 * @summary This class provides methods to generate, extract, and validate JWT bearer tokens.
 */
@Service
public class TokenServiceImpl implements BearerTokenService {

    private final Logger LOGGER = LoggerFactory.getLogger(TokenServiceImpl.class);
    private static final String AUTHORIZATION_PARAMETER_NAME = "Authorization";
    private static final String BEARER_TOKEN_PREFIX = "Bearer ";
    private static final int TOKEN_START_INDEX = 7;

    @Value("${authorization.jwt.secret}")
    private String secret;

    @Value("${authorization.jwt.expiration.days}")
    private int expirationDays;

    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
    }

    private<T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    private boolean isTokenPresentIn(String authorizationParameter) {
        return authorizationParameter != null && authorizationParameter.toLowerCase().startsWith(BEARER_TOKEN_PREFIX.toLowerCase());
    }

    private String extractTokenFrom(String authorizationParameter) {
        return authorizationParameter.substring(TOKEN_START_INDEX);
    }

    private String getAuthorizationParameterFrom(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION_PARAMETER_NAME);
    }

    private String buildTokenWithDefaultParameters(String username) {
        var issuedAt = new Date();
        var expiration = DateUtils.addDays(issuedAt, expirationDays);
        var key = getSigningKey();
        return Jwts.builder()
                .subject(username)
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }

    private boolean isBearerTokenIn(String authorizationParameter) {
        return authorizationParameter != null && authorizationParameter.toLowerCase().startsWith(BEARER_TOKEN_PREFIX.toLowerCase());
    }

    @Override
    public String getBearerTokenFrom(HttpServletRequest token) {
        String parameter = getAuthorizationParameterFrom(token);
        if(isTokenPresentIn(parameter) && isBearerTokenIn(parameter)) return extractTokenFrom(parameter);
        return null;
    }

    @Override
    public String generateToken(Authentication authentication) {
        return buildTokenWithDefaultParameters(authentication.getName());
    }

    @Override
    public String generateToken(String username) {
        return buildTokenWithDefaultParameters(username);
    }

    @Override
    public String getUsernameFromToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
            LOGGER.info("Token is valida");
            return true;
        } catch (SignatureException e) {
            LOGGER.error("Invalid token signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            LOGGER.error("Invalid token format: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            LOGGER.error("Token has expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            LOGGER.error("Unsupported token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            LOGGER.error("Token is empty: {}", e.getMessage());
        }

        return false;
    }
}
```

### Authentication Configuration

#### WebSecurityConfig.java
```java
package org.example.backendweride.platform.iam.infrastructure.auth.configuration;

import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.example.backendweride.platform.iam.infrastructure.auth.pipeline.BearerAuthRequestFilter;
import org.example.backendweride.platform.iam.infrastructure.hashing.bcrypt.BCryptHashingService;
import org.example.backendweride.platform.iam.infrastructure.tokens.jwt.BearerTokenService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

/**
 * Configuration class for web security using Spring Security.
 *
 * @summary This class sets up security filters, authentication providers, and CORS configurations.
 */
@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {
    private final UserDetailsService userDetailsService;
    private final BearerTokenService tokenService;
    private final BCryptHashingService hashingService;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    public WebSecurityConfig(
            @Qualifier("defaultUserDetailsService") UserDetailsService userDetailsService,
            BearerTokenService tokenService,
            BCryptHashingService hashingService,
            AuthenticationEntryPoint authenticationEntryPoint) {
        this.userDetailsService = userDetailsService;
        this.tokenService = tokenService;
        this.hashingService = hashingService;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Bean
    public BearerAuthRequestFilter authRequestFilter() {
        return new BearerAuthRequestFilter(tokenService, userDetailsService);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        var provider = new DaoAuthenticationProvider(hashingService);
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() { return hashingService;}

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(configurer ->
                configurer.configurationSource(request -> {
            var cors = new CorsConfiguration();
            cors.setAllowedOrigins(List.of("*"));
            cors.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            cors.setAllowedHeaders(List.of("*"));
            return cors;
        }));
        http.csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint(authenticationEntryPoint))
                .sessionManagement(customizer ->
                        customizer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers(
                                "/api/v1/authentication/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/api/v1/professional-profiles"
                        ).permitAll()
                        .anyRequest().authenticated());
        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authRequestFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
```

### Auth Pipeline

#### BearerAuthRequestFilter.java
```java
package org.example.backendweride.platform.iam.infrastructure.auth.pipeline;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.example.backendweride.platform.iam.infrastructure.auth.model.UserDetailsImpl;
import org.example.backendweride.platform.iam.infrastructure.auth.model.UsernamePasswordAuthTokenBuilder;
import org.example.backendweride.platform.iam.infrastructure.tokens.jwt.BearerTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

/**
 * Filter that process incoming HTTP requets to authenticate users based on Bearer tokens.
 *
 * @summary This filter intercepts HTTP requests, extracts Bearer tokens from the Authorization header,
 * validates them, and sets the authentication context if the token is valid.
 */
public class BearerAuthRequestFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(BearerAuthRequestFilter.class);
    private final BearerTokenService tokenService;
    @Qualifier("defaultUserDetailsService")
    private final UserDetailsService userDetailsService;

    public BearerAuthRequestFilter(BearerTokenService tokenService, UserDetailsService userDetailsService) {
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            var token = tokenService.getBearerTokenFrom(request);
            LOGGER.info("Token: {}", token);
            if(Objects.nonNull(token) && tokenService.validateToken(token)) {
                var username = tokenService.getUsernameFromToken(token);
                var userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(username);
                SecurityContextHolder.getContext()
                        .setAuthentication(UsernamePasswordAuthTokenBuilder
                                .build(userDetails, request));
            } else {
                LOGGER.warn("Token is not valid");
            }
        } catch(Exception e) {
            LOGGER.error("Cannot set user authentication: {}", e.getMessage());
        }
        filterChain.doFilter(request, response);
    }
}
```

#### UnauthorizedRequestHandleEntryPoint.java
```java
package org.example.backendweride.platform.iam.infrastructure.auth.pipeline;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Handles unauthorized requests by logging the authentication exception.
 *
 * @summary This class implements the AuthenticationEntryPoint interface to handle unauthorized requests.
 */
@Component
public class UnauthorizedRequestHandleEntryPoint implements AuthenticationEntryPoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(UnauthorizedRequestHandleEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authenticationException) throws IOException, ServletException {
        LOGGER.error("Unauthorized request: {}", authenticationException.getMessage());
    }
}
```

### Auth Models

#### UserDetailsImpl.java
```java
package org.example.backendweride.platform.iam.infrastructure.auth.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.example.backendweride.platform.iam.domain.model.aggregates.Account;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Implementation of UserDetails to represent authenticated user information.
 *
 * @summary UserDetails implementation for authenticated users
 */
@Getter
@EqualsAndHashCode
public class UserDetailsImpl implements UserDetails {
    private final String username;
    @JsonIgnore
    private final String password;
    private final boolean accountNonExpired;
    private final boolean accountNonLocked;
    private final boolean credentialsNonExpired;
    private final boolean enabled;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.accountNonExpired = true;
        this.accountNonLocked = true;
        this.credentialsNonExpired = true;
        this.enabled = true;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(Account account) {
        // For now, WeRide only handles the CLIENT role
        // All authenticated users have this single role
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_CLIENT"));

        return new UserDetailsImpl(
                account.getUserName(),
                account.getPassword(),
                authorities
        );
    }
}
```

#### UserDetailsServiceImpl.java
```java
package org.example.backendweride.platform.iam.infrastructure.auth.services;

import org.example.backendweride.platform.iam.infrastructure.persistence.jpa.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 * Implementation of UserDetailsService to load user details from the database.
 *
 * @summary This class implements the UserDetailsService interface to provide user authentication details.
 */
@Service
@Qualifier("defaultUserDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AccountRepository accountRepository;

    public UserDetailsServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        var user = accountRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User Not Found with username: " + username));
        return org.example.backendweride.platform.iam.infrastructure.auth.model.UserDetailsImpl.build(user);
    }
}
```

#### UsernamePasswordAuthTokenBuilder.java
```java
package org.example.backendweride.platform.iam.infrastructure.auth.model;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Builder for creating UsernamePasswordAuthenticationToken instances.
 *
 * @summary Utility class for building authentication tokens
 */
public class UsernamePasswordAuthTokenBuilder {
    public static UsernamePasswordAuthenticationToken build(UserDetails principal, HttpServletRequest request){
        var usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        usernamePasswordAuthenticationToken.setDetails(new org.springframework.security.web.authentication.WebAuthenticationDetailsSource().buildDetails(request));
        return usernamePasswordAuthenticationToken;
    }
}
```

---

## Interfaces Layer

### Controllers

#### IamAuthController.java
```java
package org.example.backendweride.platform.iam.interfaces.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.backendweride.platform.iam.domain.services.AccountCommandService;
import org.example.backendweride.platform.iam.interfaces.rest.resources.AccountResource;
import org.example.backendweride.platform.iam.interfaces.rest.resources.AuthenticatedAccountResource;
import org.example.backendweride.platform.iam.interfaces.rest.resources.SignInResource;
import org.example.backendweride.platform.iam.interfaces.rest.resources.SignUpResource;
import org.example.backendweride.platform.iam.interfaces.rest.transform.AccountResourceFromEntityAssembler;
import org.example.backendweride.platform.iam.interfaces.rest.transform.AuthenticatedAccountResourceFromEntityAssembler;
import org.example.backendweride.platform.iam.interfaces.rest.transform.SignInCommandFromResourceAssembler;
import org.example.backendweride.platform.iam.interfaces.rest.transform.SignUpCommandFromResourceAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Auth Controller
 *
 * @summary Handles authentication-related endpoints such as sign-in and sign-up.
 */
@RestController
@RequestMapping(value = "api/v1/authentication", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Authentication", description = "Manage Auth in the System")
public class IamAuthController {
    private final AccountCommandService accountCommandService;

    public IamAuthController(AccountCommandService accountCommandService) {
        this.accountCommandService = accountCommandService;
    }

    /**
     * Sign in to the system.
     * @param signInResource The sign-in details.
     * @return ResponseEntity containing the authenticated account resource or an error status.
     */
    @PostMapping("/sign-in")
    @Operation(summary = "Sign in to the system", description = "Authenticate a user and return an authenticated account resource.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated"),
            @ApiResponse(responseCode = "404", description = "Authentication failed, account not found")
    })
    public ResponseEntity<AuthenticatedAccountResource> signIn(@RequestBody SignInResource signInResource) {
        var signInCommand = SignInCommandFromResourceAssembler.toCommandFromResource(signInResource);
        var authenticatedAccountResult = accountCommandService.handle(signInCommand);

        if(authenticatedAccountResult.isEmpty()) return ResponseEntity.notFound().build();
        var authenticatedAccount = authenticatedAccountResult.get();
        var authenticatedAccountResource =
                AuthenticatedAccountResourceFromEntityAssembler.toResourceFromEntity(authenticatedAccount.left, authenticatedAccount.right);
        return ResponseEntity.ok(authenticatedAccountResource);
    }

    /**
     * Sign up for a new account.
     * @param signUpResource The sign-up details.
     * @return ResponseEntity containing the created account resource or an error status.
     */
    @PostMapping("/sign-up")
    @Operation(summary = "Sign up for a new account", description = "Create a new user account in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Account created successfully"),
            @ApiResponse(responseCode = "400", description = "Account creation failed due to bad request")
    })
    public ResponseEntity<AccountResource> signUp(@RequestBody SignUpResource signUpResource) {
        var signUpCommand = SignUpCommandFromResourceAssembler.toCommandFromResource(signUpResource);
        var account = accountCommandService.handle(signUpCommand);

        if (account.isEmpty()) return ResponseEntity.badRequest().build();
        var accountEntity = account.get();
        var accountResource = AccountResourceFromEntityAssembler.toResourceFromEntity(accountEntity);
        return new ResponseEntity<>(accountResource, HttpStatus.CREATED);
    }
}
```

#### AccountsController.java
```java
package org.example.backendweride.platform.iam.interfaces.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.backendweride.platform.iam.domain.model.queries.GetAccountByIdQuery;
import org.example.backendweride.platform.iam.domain.services.AccountQueryService;
import org.example.backendweride.platform.iam.interfaces.rest.resources.AccountResource;
import org.example.backendweride.platform.iam.interfaces.rest.transform.AccountResourceFromEntityAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Accounts Controller
 *
 * @summary Handles HTTP requests related to account retrieval.
 */
@RestController
@RequestMapping(value = "api/v1/accounts", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Accounts", description = "Manage User Accounts")
public class AccountsController {
    private final AccountQueryService accountQueryService;
    public AccountsController(AccountQueryService accountQueryService) {
        this.accountQueryService = accountQueryService;
    }

    /**
     * Get account by ID.
     * @param accountId The ID of the account to retrieve.
     * @return ResponseEntity containing the account resource or a not found status.
     */
    @GetMapping("/{accountId}")
    @Operation (summary = "Get account by ID", description = "Retrieve account details using the account ID.")
    @ApiResponses (value = {
            @ApiResponse(responseCode = "200", description = "Account found"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    public ResponseEntity<AccountResource> getAccountById(@PathVariable Long accountId) {
        var getAccountByIdQuery = new GetAccountByIdQuery(accountId);
        var account = accountQueryService.handle(getAccountByIdQuery);
        if(account.isEmpty()) return ResponseEntity.notFound().build();

        var userResource = AccountResourceFromEntityAssembler.toResourceFromEntity(account.get());
        return ResponseEntity.ok(userResource);
    }
}
```

### REST Resources

#### SignUpResource.java
```java
package org.example.backendweride.platform.iam.interfaces.rest.resources;

/**
 * Sign Up Resource
 *
 * @summary Represents the data required for signing up, including username and password.
 */
public record SignUpResource(String username, String password) {}
```

#### SignInResource.java
```java
package org.example.backendweride.platform.iam.interfaces.rest.resources;

/**
 * Sign In Resource
 *
 * @summary Represents the data required for signing in, including username and password.
 */
public record SignInResource(String username, String password) {}
```

#### AuthenticatedAccountResource.java
```java
package org.example.backendweride.platform.iam.interfaces.rest.resources;

/**
 * Authenticated Account Resource
 *
 * @summary Represents an authenticated account resource with its ID and token.
 */
public record AuthenticatedAccountResource(Long id, String token) {}
```

#### AccountResource.java
```java
package org.example.backendweride.platform.iam.interfaces.rest.resources;

/**
 * Account Resource
 *
 * @summary Represents an account resource with its ID and username.
 */
public record AccountResource(Long id, String username) {}
```

### Transformers (Assemblers)

#### SignUpCommandFromResourceAssembler.java
```java
package org.example.backendweride.platform.iam.interfaces.rest.transform;

import org.example.backendweride.platform.iam.domain.model.commands.SignUpCommand;
import org.example.backendweride.platform.iam.interfaces.rest.resources.SignUpResource;

/**
 * Assembler class to convert SignUpResource to SignUpCommand.
 *
 * @summary This class provides a method to transform a SignUpResource
 *          into a SignUpCommand for processing sign-up requests.
 */
public class SignUpCommandFromResourceAssembler {
    public static SignUpCommand toCommandFromResource(SignUpResource resource) {
        return new SignUpCommand(resource.username(), resource.password());
    }
}
```

#### SignInCommandFromResourceAssembler.java
```java
package org.example.backendweride.platform.iam.interfaces.rest.transform;

import org.example.backendweride.platform.iam.domain.model.commands.SignInCommand;
import org.example.backendweride.platform.iam.interfaces.rest.resources.SignInResource;

/**
 * Assembler class to convert SignInResource to SignInCommand.
 *
 * @summary This class provides a method to transform a SignInResource
 *          into a SignInCommand for processing sign-in requests.
 */
public class SignInCommandFromResourceAssembler {
    public static SignInCommand toCommandFromResource(SignInResource resource) {
        return new SignInCommand(resource.username(), resource.password());
    }
}
```

#### AuthenticatedAccountResourceFromEntityAssembler.java
```java
package org.example.backendweride.platform.iam.interfaces.rest.transform;

import org.example.backendweride.platform.iam.domain.model.aggregates.Account;
import org.example.backendweride.platform.iam.interfaces.rest.resources.AuthenticatedAccountResource;

/**
 * Assembler class to convert Account entity to AuthenticatedAccountResource.
 *
 * @summary This class provides a method to transform an Account entity
 *          into an AuthenticatedAccountResource for REST API responses.
 */
public class AuthenticatedAccountResourceFromEntityAssembler {
    public static AuthenticatedAccountResource toResourceFromEntity(Account entity, String token) {
        return new AuthenticatedAccountResource(entity.getId(), token);
    }
}
```

#### AccountResourceFromEntityAssembler.java
```java
package org.example.backendweride.platform.iam.interfaces.rest.transform;

import org.example.backendweride.platform.iam.domain.model.aggregates.Account;
import org.example.backendweride.platform.iam.interfaces.rest.resources.AccountResource;

/**
 * Assembler class to convert Account entity to AccountResource.
 *
 * @summary This class provides a method to transform an Account entity
 *         into an AccountResource for REST API responses.
 */
public class AccountResourceFromEntityAssembler {
    public static AccountResource toResourceFromEntity(Account entity){
        return new AccountResource(entity.getId(), entity.getUserName());
    }
}
```

### Anti-Corruption Layer (ACL)

#### IamContextFacade.java
```java
package org.example.backendweride.platform.iam.interfaces.acl;

import org.example.backendweride.platform.iam.domain.model.commands.SignUpCommand;
import org.example.backendweride.platform.iam.domain.model.queries.GetAccountByIdQuery;
import org.example.backendweride.platform.iam.domain.services.AccountCommandService;
import org.example.backendweride.platform.iam.domain.services.AccountQueryService;
import org.springframework.stereotype.Service;

/*
 * IamContextFacade
 * This class serves as a facade for IAM context operations, providing simplified methods for account creation and retrieval.
 *
 * @summary Facade for IAM context operations.
*/

@Service
public class IamContextFacade {
    private final AccountCommandService accountCommandService;
    private final AccountQueryService accountQueryService;

    public IamContextFacade(AccountCommandService accountCommandService,
                            AccountQueryService accountQueryService)
    {
        this.accountCommandService = accountCommandService;
        this.accountQueryService = accountQueryService;
    }

    public Long createAccount(String username, String password) {
        var signUpCommand = new SignUpCommand(username, password);
        var result = accountCommandService.handle(signUpCommand);
        if (result.isEmpty())
            throw new RuntimeException("Error creating account");
        return result.get().getId();
    }

    public String fetchUsernameByAccountId(Long accountId) {
        var getAccountByIdQuery = new GetAccountByIdQuery(accountId);
        var result = accountQueryService.handle(getAccountByIdQuery);
        if (result.isEmpty())
            throw new RuntimeException("Account not found");
        return result.get().getUserName();
    }
}
```

---

## Arquitectura y Flujos

### Flujo de Sign Up (Registro de Usuario)

```
1. Cliente envía: POST /api/v1/authentication/sign-up
   Body: { "username": "user@example.com", "password": "password123" }

2. IamAuthController.signUp() recibe SignUpResource
   ↓
3. SignUpCommandFromResourceAssembler convierte a SignUpCommand
   ↓
4. AccountCommandServiceImpl.handle(SignUpCommand)
   - Verifica que el usuario no exista
   - Crea nueva instancia de Account con contraseña hasheada (BCrypt)
   - Persiste la cuenta en la BD
   - Crea un Profile relacionado (via ProfileContextFacade)
   - Actualiza la cuenta con el profileId
   ↓
5. AccountResourceFromEntityAssembler convierte Account a AccountResource
   ↓
6. Retorna HTTP 201 Created con AccountResource
```

### Flujo de Sign In (Inicio de Sesión)

```
1. Cliente envía: POST /api/v1/authentication/sign-in
   Body: { "username": "user@example.com", "password": "password123" }

2. IamAuthController.signIn() recibe SignInResource
   ↓
3. SignInCommandFromResourceAssembler convierte a SignInCommand
   ↓
4. AccountCommandServiceImpl.handle(SignInCommand)
   - Busca la cuenta por username
   - TokenService.generateToken(username) crea un JWT
   - Retorna ImmutablePair<Account, String(token)>
   ↓
5. AuthenticatedAccountResourceFromEntityAssembler convierte a AuthenticatedAccountResource
   ↓
6. Retorna HTTP 200 OK con { "id": 1, "token": "eyJhbGc..." }
```

### Flujo de Autenticación en Requests Posteriores

```
1. Cliente envía: GET /api/v1/accounts/1
   Header: Authorization: Bearer eyJhbGc...

2. BearerAuthRequestFilter.doFilterInternal()
   - Extrae el token del header Authorization
   - TokenService.validateToken(token) valida el JWT
   - TokenService.getUsernameFromToken(token) extrae el username
   - UserDetailsServiceImpl carga los UserDetails
   - UsernamePasswordAuthTokenBuilder construye el Authentication token
   - SecurityContextHolder.getContext().setAuthentication(token)
   ↓
3. Request procede con autenticación establecida
   ↓
4. AccountsController.getAccountById() procesa la solicitud autenticada
```

### Flujo de Get Account

```
1. Cliente envía: GET /api/v1/accounts/{accountId}

2. AccountsController.getAccountById() recibe el ID
   ↓
3. GetAccountByIdQuery se crea con el ID
   ↓
4. AccountQueryServiceImpl.handle(GetAccountByIdQuery)
   - Busca la cuenta en AccountRepository por ID
   - Lanza excepción si no existe
   ↓
5. AccountResourceFromEntityAssembler convierte Account a AccountResource
   ↓
6. Retorna HTTP 200 OK con AccountResource
```

---

## Configuración Requerida

En `application.properties` se deben establecer:

```properties
# JWT Configuration
authorization.jwt.secret=your-very-secret-key-here-min-256-bits
authorization.jwt.expiration.days=7

# Database Configuration (para la persistencia de cuentas)
spring.datasource.url=jdbc:mysql://localhost:3306/weride
spring.datasource.username=root
spring.datasource.password=password
```

---

## Dependencias Principales

- **Spring Security**: Autenticación y autorización
- **JWT (jjwt)**: Generación y validación de tokens JWT
- **BCrypt**: Hashing de contraseñas
- **Spring Data JPA**: Persistencia de datos
- **Lombok**: Reducción de boilerplate
- **Swagger/OpenAPI**: Documentación de API

---

## Patrones de Diseño Utilizados

1. **Aggregate Root**: Account es la raíz del agregado
2. **Command Pattern**: SignUpCommand, SignInCommand
3. **Query Pattern**: GetAccountByIdQuery
4. **Repository Pattern**: AccountRepository abstrae la persistencia
5. **Service Layer**: AccountCommandService y AccountQueryService
6. **Assembler/DTO Pattern**: Transformadores para convertir entre capas
7. **Anti-Corruption Layer**: IamContextFacade para comunicación con otros bounded contexts
8. **Strategy Pattern**: HashingService y TokenService son intercambiables
9. **Filter Chain Pattern**: BearerAuthRequestFilter en el pipeline de seguridad

---

## Responsabilidades por Capa

### Domain Layer
- Define entidades, value objects, agregados
- Especifica interfaces de servicios de dominio
- No tiene dependencias de infraestructura

### Application Layer
- Implementa los servicios de dominio
- Orquesta llamadas a repositorios y servicios externos
- Maneja lógica transaccional

### Infrastructure Layer
- Implementa persistencia (repositories)
- Proporciona servicios técnicos (hashing, tokens)
- Configura seguridad y autenticación

### Interfaces Layer
- Expone endpoints REST
- Valida entrada del cliente
- Convierte entre DTOs y comandos/queries


