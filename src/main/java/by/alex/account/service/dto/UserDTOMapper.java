package by.alex.account.service.dto;

import by.alex.account.domain.Role;
import by.alex.account.domain.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserDTOMapper {

    public UserDTO convertToDTO(User user) {
        var roles = user.getRoles().stream().map(Role::getName).toList();
        return new UserDTO(user.getId(), user.getUsername(), List.copyOf(roles));
    }
}
