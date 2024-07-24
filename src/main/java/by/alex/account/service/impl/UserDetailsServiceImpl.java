package by.alex.account.service.impl;

import by.alex.account.domain.Role;
import by.alex.account.domain.User;
import by.alex.account.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Attempting to load user by username: {}", username);
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found with username: {}", username);
                    return new UsernameNotFoundException("User not found");
                });

        log.info("User found: {}", user.getUsername());
        return buildUserDetails(user);
    }

    private static UserDetails buildUserDetails(User user) {
        var roles = mapRolesToNames(user.getRoles());
        log.debug("Building UserDetails for user: {}, with roles: {}", user.getUsername(), roles);

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(roles)
                .build();
    }

    private static String[] mapRolesToNames(Set<Role> roles) {
        log.debug("Mapping roles to names: {}", roles);
        return roles.stream()
                .map(Role::getName)
                .toArray(String[]::new);
    }
}
