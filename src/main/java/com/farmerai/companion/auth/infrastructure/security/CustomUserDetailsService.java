package com.farmerai.companion.auth.infrastructure.security;

import com.farmerai.companion.auth.infrastructure.persistence.entity.UserJpaEntity;
import com.farmerai.companion.auth.infrastructure.persistence.repository.UserJpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Custom {@link UserDetailsService} that loads user data from PostgreSQL
 * via the JPA repository.
 *
 * <p>Used by Spring Security during authentication (login flow).
 * In the stateless JWT flow, this is primarily invoked during login to
 * load the user for credential validation.</p>
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserJpaRepository userJpaRepository;

    public CustomUserDetailsService(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserJpaEntity user = userJpaRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with email: " + email
                ));

        return new CustomUserDetails(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getRole().getName(),
                "ACTIVE".equals(user.getStatus())
        );
    }
}
