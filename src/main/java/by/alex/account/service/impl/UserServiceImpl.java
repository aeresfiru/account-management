package by.alex.account.service.impl;

import by.alex.account.repository.UserRepository;
import by.alex.account.service.UserService;
import by.alex.account.service.dto.UserDTO;
import by.alex.account.service.dto.UserDTOMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserDTOMapper userDTOMapper;

    @Override
    @Transactional(readOnly = true)
    public UserDTO findByUsername(String username) {
        log.debug("Attempting to find user by username: {}", username);
        return userRepository.findByUsername(username)
                .map(userDTOMapper::convertToDTO)
                .orElseThrow(() -> {
                    log.error("User not found with username: {}", username);
                    return new EntityNotFoundException("User not found");
                });
    }
}
