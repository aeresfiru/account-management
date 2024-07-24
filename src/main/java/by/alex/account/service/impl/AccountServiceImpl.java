package by.alex.account.service.impl;

import by.alex.account.repository.AccountRepository;
import by.alex.account.service.AccountBlockedException;
import by.alex.account.service.AccountNotFoundException;
import by.alex.account.service.AccountService;
import by.alex.account.service.InsufficientFundsException;
import by.alex.account.service.dto.AccountDTO;
import by.alex.account.service.dto.AccountDTOMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountDTOMapper accountDTOMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<AccountDTO> findAll(int page, int size) {
        log.debug("Finding all accounts with page={} and size={}", page, size);
        return accountRepository.findAll(PageRequest.of(page, size, Sort.by("id")))
                .map(accountDTOMapper::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountDTO findByUserId(Integer userId) {
        log.debug("Finding account by userID={}", userId);
        return accountRepository.findByUserId(userId)
                .map(accountDTOMapper::convertToDTO)
                .orElseThrow(() -> {
                    log.error("Account not found for userID={}", userId);
                    return new AccountNotFoundException("Account not found");
                });
    }

    @Override
    @Transactional(readOnly = true)
    public AccountDTO findById(Integer accountId) {
        log.debug("Finding account by accountID={}", accountId);
        return accountRepository.findById(accountId)
                .map(accountDTOMapper::convertToDTO)
                .orElseThrow(() -> {
                    log.error("Account not found for accountID={}", accountId);
                    return new AccountNotFoundException("Account not found");
                });
    }

    @Override
    public void deposit(AccountDTO accountDTO, BigDecimal amount) {
        log.debug("Depositing amount={} into accountID={}", amount, accountDTO.id());
        var account = accountDTOMapper.convertToEntity(accountDTO);
        if (account.getIsBlocked()) {
            log.error("Attempt to deposit into blocked accountID={}", accountDTO.id());
            throw new AccountBlockedException("Account is blocked");
        }
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
        log.info("Successfully deposited amount={} into accountID={}", amount, accountDTO.id());
    }

    @Override
    public void withdraw(AccountDTO accountDTO, BigDecimal amount) {
        log.debug("Withdrawing amount={} from accountID={}", amount, accountDTO.id());
        var account = accountDTOMapper.convertToEntity(accountDTO);
        if (account.getIsBlocked()) {
            log.error("Attempt to withdraw from blocked accountID={}", accountDTO.id());
            throw new AccountBlockedException("Account is blocked");
        }
        if (account.getBalance().compareTo(amount) < 0) {
            log.error("Insufficient funds for withdrawal amount={} from accountID={}", amount, accountDTO.id());
            throw new InsufficientFundsException("Insufficient funds");
        }
        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);
        log.info("Successfully withdrew amount={} from accountID={}", amount, accountDTO.id());
    }

    @Override
    public void blockAccount(AccountDTO accountDTO) {
        log.debug("Blocking accountID={}", accountDTO.id());
        var account = accountDTOMapper.convertToEntity(accountDTO);
        account.setIsBlocked(true);
        accountRepository.save(account);
        log.info("Successfully blocked accountDTO={}", accountDTO.id());
    }

    @Override
    public void unblockAccount(AccountDTO accountDTO) {
        log.debug("Unblocking accountID={}", accountDTO.id());
        var account = accountDTOMapper.convertToEntity(accountDTO);
        account.setIsBlocked(false);
        accountRepository.save(account);
        log.info("Successfully unblocked accountID={}", accountDTO.id());
    }
}
