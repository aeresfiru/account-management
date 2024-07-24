package by.alex.account.controller;

import by.alex.account.service.dto.AccountDTO;
import by.alex.account.service.dto.UserDTO;
import by.alex.account.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@WithMockUser(username = "j.daniels", roles = "ADMIN")
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountServiceImpl accountService;

    @Test
    void listAccounts_ShouldReturnAccountsPage() throws Exception {
        // given
        var user1 = new UserDTO(1, "username1", List.of("USER"));
        var user2 = new UserDTO(2, "username2", List.of("USER"));
        var account1 = new AccountDTO(1, BigDecimal.valueOf(100.00), false, user1);
        var account2 = new AccountDTO(2, BigDecimal.valueOf(200.00), false, user2);
        var accountPage = new PageImpl<>(List.of(account1, account2), PageRequest.of(0, 10), 2);

        when(accountService.findAll(0, 10)).thenReturn(accountPage);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/accounts")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/accounts"))
                .andExpect(model().attribute("accounts", List.of(account1, account2)))
                .andExpect(model().attribute("currentPage", 0))
                .andExpect(model().attribute("totalPages", 1))
                .andExpect(model().attribute("pageSize", 10));

        verify(accountService).findAll(0, 10);
    }

    @Test
    void blockAccount_ShouldRedirectToAccountsPage() throws Exception {
        // given
        var accountDTO = new AccountDTO(1, BigDecimal.valueOf(100.00), false, null);
        when(accountService.findById(1)).thenReturn(accountDTO);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.post("/admin/block")
                        .param("accountId", "1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/accounts"));

        verify(accountService).blockAccount(accountDTO);
    }

    @Test
    void unblockAccount_ShouldRedirectToAccountsPage() throws Exception {
        // given
        var accountDTO = new AccountDTO(1, BigDecimal.valueOf(100.00), true, null);
        when(accountService.findById(1)).thenReturn(accountDTO);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.post("/admin/unblock")
                        .param("accountId", "1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/accounts"));

        verify(accountService).unblockAccount(accountDTO);
    }
}
