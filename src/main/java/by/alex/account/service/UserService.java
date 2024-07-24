package by.alex.account.service;

import by.alex.account.service.dto.UserDTO;

public interface UserService {

    UserDTO findByUsername(String username);
}
