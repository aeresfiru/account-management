package by.alex.account.service.dto;

import java.io.Serializable;
import java.util.List;

public record UserDTO(Integer id, String username, List<String> roles)
        implements Serializable {
}
