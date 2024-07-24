package by.alex.account.service.impl;

import by.alex.account.domain.Role;
import by.alex.account.domain.User;
import by.alex.account.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private UserRepository userRepository;

    @Test
    void loadUserByUsername_ReturnsUserDetails() {
        // given
        var user = User.builder()
                .username("testuser")
                .password("password")
                .roles(Set.of(new Role(1, "USER")))
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // when
        var actual = userDetailsService.loadUserByUsername("testuser");

        // then
        assertNotNull(actual);
        assertEquals("testuser", actual.getUsername());
        assertEquals("password", actual.getPassword());
        assertTrue(actual.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void loadUserByUsername_ThrowsExceptionIfUserNotFound() {
        // given
        when(userRepository.findByUsername("nonexistentuser")).thenReturn(Optional.empty());

        // when & then
        var thrown = assertThrows(UsernameNotFoundException.class, () ->
                userDetailsService.loadUserByUsername("nonexistentuser")
        );
        assertEquals("User not found", thrown.getMessage());
    }
}
