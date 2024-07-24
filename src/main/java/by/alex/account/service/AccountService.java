package by.alex.account.service;

import by.alex.account.service.dto.AccountDTO;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;

public interface AccountService {

    Page<AccountDTO> findAll(int page, int size);

    AccountDTO findByUserId(Integer userId);

    AccountDTO findById(Integer accountId);

    void deposit(AccountDTO accountDTO, BigDecimal amount);

    void withdraw(AccountDTO accountDTO, BigDecimal amount);

    void blockAccount(AccountDTO accountDTO);

    void unblockAccount(AccountDTO accountDTO);
}
