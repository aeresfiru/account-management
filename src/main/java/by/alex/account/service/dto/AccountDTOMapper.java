package by.alex.account.service.dto;

import by.alex.account.domain.Account;
import by.alex.account.domain.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AccountDTOMapper {

    private final UserDTOMapper userDTOMapper;

    public AccountDTO convertToDTO(Account account) {
        var userDTO = userDTOMapper.convertToDTO(account.getUser());

        return new AccountDTO(account.getId(), account.getBalance(),
                account.getIsBlocked(), userDTO);
    }

    public Account convertToEntity(AccountDTO dto) {
        return Account.builder()
                .id(dto.id())
                .balance(dto.balance())
                .isBlocked(dto.isBlocked())
                .user(User.builder().id(dto.user().id()).build())
                .build();
    }
}
