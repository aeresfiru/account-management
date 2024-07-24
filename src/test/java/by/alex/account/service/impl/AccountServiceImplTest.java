package by.alex.account.service.impl;

import by.alex.account.domain.Account;
import by.alex.account.repository.AccountRepository;
import by.alex.account.service.dto.AccountDTO;
import by.alex.account.service.dto.AccountDTOMapper;
import by.alex.account.service.dto.UserDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @InjectMocks
    private AccountServiceImpl accountService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountDTOMapper accountDTOMapper;

    @Test
    void findAll_ReturnsAccountDTOPage() {
        // given
        var account = new Account(1, BigDecimal.valueOf(100.00), false, null);
        var accountDTO = new AccountDTO(1, BigDecimal.valueOf(100.00), false, null);
        var accountPage = new PageImpl<>(List.of(account));
        var accountDTOPage = new PageImpl<>(List.of(accountDTO));

        when(accountRepository.findAll(PageRequest.of(0, 10, Sort.by("id"))))
                .thenReturn(accountPage);
        when(accountDTOMapper.convertToDTO(account)).thenReturn(accountDTO);

        // when
        var actual = accountService.findAll(0, 10);

        // then
        assertEquals(accountDTOPage, actual);
    }

    @Test
    void findByUserId_ReturnsAccountDTO() {
        // given
        var account = new Account(1, BigDecimal.valueOf(100.00), false, null);
        var userDTO = new UserDTO(1, "user", List.of("ROLE_USER"));
        var accountDTO = new AccountDTO(1, BigDecimal.valueOf(100.00), false, userDTO);

        when(accountRepository.findByUserId(1)).thenReturn(Optional.of(account));
        when(accountDTOMapper.convertToDTO(account)).thenReturn(accountDTO);

        // when
        var actual = accountService.findByUserId(1);

        // then
        assertEquals(accountDTO, actual);
    }

    @Test
    void findById_ReturnsAccountDTO() {
        // given
        var account = new Account(1, BigDecimal.valueOf(100.00), false, null);
        var userDTO = new UserDTO(1, "user", List.of("ROLE_USER"));
        var accountDTO = new AccountDTO(1, BigDecimal.valueOf(100.00), false, userDTO);

        when(accountRepository.findById(1)).thenReturn(Optional.of(account));
        when(accountDTOMapper.convertToDTO(account)).thenReturn(accountDTO);

        // when
        var actual = accountService.findById(1);

        // then
        assertEquals(accountDTO, actual);
    }

    @Test
    void deposit_IncreasesAccountBalance() {
        // given
        var accountDTO = new AccountDTO(1, BigDecimal.valueOf(100.00), false, null);
        var account = new Account(1, BigDecimal.valueOf(100.00), false, null);
        when(accountDTOMapper.convertToEntity(accountDTO)).thenReturn(account);
        when(accountRepository.save(account)).thenReturn(account);

        // when
        accountService.deposit(accountDTO, BigDecimal.valueOf(50.00));

        // then
        verify(accountRepository).save(argThat(acc -> acc.getBalance().equals(BigDecimal.valueOf(150.00))));
    }

    @Test
    void withdraw_DecreasesAccountBalance() {
        // given
        var accountDTO = new AccountDTO(1, BigDecimal.valueOf(100.00), false, null);
        var account = new Account(1, BigDecimal.valueOf(100.00), false, null);
        when(accountDTOMapper.convertToEntity(accountDTO)).thenReturn(account);
        when(accountRepository.save(account)).thenReturn(account);

        // when
        accountService.withdraw(accountDTO, BigDecimal.valueOf(50.00));

        // then
        verify(accountRepository).save(argThat(acc -> acc.getBalance().equals(BigDecimal.valueOf(50.00))));
    }

    @Test
    void blockAccount_BlocksAccount() {
        // given
        var accountDTO = new AccountDTO(1, BigDecimal.valueOf(100.00), false, null);
        var account = new Account(1, BigDecimal.valueOf(100.00), false, null);
        when(accountDTOMapper.convertToEntity(accountDTO)).thenReturn(account);
        when(accountRepository.save(account)).thenReturn(account);

        // when
        accountService.blockAccount(accountDTO);

        // then
        verify(accountRepository).save(argThat(Account::getIsBlocked));
    }

    @Test
    void unblockAccount_UnblocksAccount() {
        // given
        var accountDTO = new AccountDTO(1, BigDecimal.valueOf(100.00), true, null);
        var account = new Account(1, BigDecimal.valueOf(100.00), true, null);
        when(accountDTOMapper.convertToEntity(accountDTO)).thenReturn(account);
        when(accountRepository.save(account)).thenReturn(account);

        // when
        accountService.unblockAccount(accountDTO);

        // then
        verify(accountRepository).save(argThat(acc -> !acc.getIsBlocked()));
    }

    @Test
    void findByUserId_ThrowsExceptionIfAccountNotFound() {
        // given
        when(accountRepository.findByUserId(1)).thenReturn(Optional.empty());

        // when & then
        var thrown = assertThrows(RuntimeException.class, () -> accountService.findByUserId(1));
        assertEquals("Account not found", thrown.getMessage());
    }

    @Test
    void findById_ThrowsExceptionIfAccountNotFound() {
        // given
        when(accountRepository.findById(1)).thenReturn(Optional.empty());

        // when & then
        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> accountService.findById(1));
        assertEquals("Account not found", thrown.getMessage());
    }

    @Test
    void deposit_ThrowsExceptionIfAccountBlocked() {
        // given
        var accountDTO = new AccountDTO(1, BigDecimal.valueOf(100.00), true, null);
        var account = new Account(1, BigDecimal.valueOf(100.00), true, null);
        when(accountDTOMapper.convertToEntity(accountDTO)).thenReturn(account);

        // when & then
        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> accountService.deposit(accountDTO, BigDecimal.valueOf(50.00)));
        assertEquals("Account is blocked", thrown.getMessage());
    }

    @Test
    void withdraw_ThrowsExceptionIfAccountBlocked() {
        // given
        var accountDTO = new AccountDTO(1, BigDecimal.valueOf(100.00), true, null);
        var account = new Account(1, BigDecimal.valueOf(100.00), true, null);
        when(accountDTOMapper.convertToEntity(accountDTO)).thenReturn(account);

        // when & then
        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> accountService.withdraw(accountDTO, BigDecimal.valueOf(50.00)));
        assertEquals("Account is blocked", thrown.getMessage());
    }

    @Test
    void withdraw_ThrowsExceptionIfInsufficientFunds() {
        // given
        var accountDTO = new AccountDTO(1, BigDecimal.valueOf(100.00), false, null);
        var account = new Account(1, BigDecimal.valueOf(100.00), false, null);
        when(accountDTOMapper.convertToEntity(accountDTO)).thenReturn(account);

        // when & then
        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> accountService.withdraw(accountDTO, BigDecimal.valueOf(150.00)));
        assertEquals("Insufficient funds", thrown.getMessage());
    }
}
