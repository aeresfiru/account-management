package by.alex.account.service.impl;

import by.alex.account.domain.User;
import by.alex.account.repository.UserRepository;
import by.alex.account.service.dto.UserDTO;
import by.alex.account.service.dto.UserDTOMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserDTOMapper userDTOMapper;

    @Test
    void findByUsername_ReturnsUser() {
        // given
        var user = new User();
        user.setUsername("testuser");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userDTOMapper.convertToDTO(user))
                .thenReturn(new UserDTO(1, "testuser", List.of()));

        // when
        var result = userService.findByUsername("testuser");

        // then
        assertNotNull(result);
        assertEquals("testuser", result.username());
    }

    @Test
    void findByUsername_ThrowsExceptionIfUserNotFound() {
        // given
        when(userRepository.findByUsername("nonexistentuser")).thenReturn(Optional.empty());

        // when & then
        var thrown = assertThrows(RuntimeException.class, () ->
                userService.findByUsername("nonexistentuser")
        );
        assertEquals("User not found", thrown.getMessage());
    }
}
