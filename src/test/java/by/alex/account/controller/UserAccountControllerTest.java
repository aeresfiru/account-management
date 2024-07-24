package by.alex.account.controller;

import by.alex.account.service.dto.AccountDTO;
import by.alex.account.service.dto.UserDTO;
import by.alex.account.service.impl.AccountServiceImpl;
import by.alex.account.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
@AutoConfigureMockMvc
@WithMockUser(username = "j.daniels", roles = "USER")
class UserAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServiceImpl userService;

    @MockBean
    private AccountServiceImpl accountService;

    @Test
    void viewAccount_ShouldReturnAccountPage() throws Exception {
        // given
        var user = new UserDTO(1, "j.daniels", List.of("USER"));
        var account = new AccountDTO(1, BigDecimal.valueOf(150.00), false, user);

        when(userService.findByUsername("j.daniels")).thenReturn(user);
        when(accountService.findByUserId(1)).thenReturn(account);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get("/user/account"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/account"))
                .andExpect(model().attribute("account", account));

        verify(userService).findByUsername("j.daniels");
        verify(accountService).findByUserId(1);
    }

    @Test
    void deposit_ShouldRedirectToAccountPage() throws Exception {
        // given
        var user = new UserDTO(1, "j.daniels", List.of("USER"));
        var account = new AccountDTO(1, BigDecimal.valueOf(150.00), false, user);

        when(userService.findByUsername("j.daniels")).thenReturn(user);
        when(accountService.findByUserId(1)).thenReturn(account);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.post("/user/account/deposit")
                        .param("amount", "50.00")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/account"));

        verify(userService).findByUsername("j.daniels");
        verify(accountService).findByUserId(1);
    }

    @Test
    void withdraw_ShouldRedirectToAccountPage() throws Exception {
        // given
        var user = new UserDTO(1, "j.daniels", List.of("USER"));
        var account = new AccountDTO(1, BigDecimal.valueOf(150.00), false, user);

        when(userService.findByUsername("j.daniels")).thenReturn(user);
        when(accountService.findByUserId(1)).thenReturn(account);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.post("/user/account/withdraw")
                        .param("amount", "30.00")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/account"));

        verify(userService).findByUsername("j.daniels");
        verify(accountService).findByUserId(1);
    }
}
