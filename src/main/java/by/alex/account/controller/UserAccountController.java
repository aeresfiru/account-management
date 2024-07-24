package by.alex.account.controller;

import by.alex.account.service.AccountBlockedException;
import by.alex.account.service.InsufficientFundsException;
import by.alex.account.service.dto.AccountDTO;
import by.alex.account.service.impl.AccountServiceImpl;
import by.alex.account.service.impl.UserServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@Controller
@RequestMapping("/user/account")
@AllArgsConstructor
@Slf4j
public class UserAccountController {

    private final UserServiceImpl userService;
    private final AccountServiceImpl accountService;

    @RequestMapping
    public String viewAccount(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        var account = loadUserAccount(userDetails);
        model.addAttribute("account", account);
        return "user/account";
    }

    @PostMapping("/deposit")
    public String deposit(@AuthenticationPrincipal UserDetails userDetails,
                          @RequestParam BigDecimal amount,
                          Model model) {
        try {
            var account = loadUserAccount(userDetails);
            accountService.deposit(account, amount);
        } catch (AccountBlockedException | InsufficientFundsException | IllegalArgumentException ex) {
            var account = loadUserAccount(userDetails);
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("account", account);
            return "user/account";
        }
        return "redirect:/user/account";
    }

    @PostMapping("/withdraw")
    public String withdraw(@AuthenticationPrincipal UserDetails userDetails,
                           @RequestParam BigDecimal amount,
                           Model model) {
        try {
            var account = loadUserAccount(userDetails);
            accountService.withdraw(account, amount);
        } catch (AccountBlockedException | InsufficientFundsException | IllegalArgumentException ex) {
            var account = loadUserAccount(userDetails);
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("account", account);
            return "user/account";
        }
        return "redirect:/user/account";
    }

    private AccountDTO loadUserAccount(UserDetails userDetails) {
        var user = userService.findByUsername(userDetails.getUsername());
        return accountService.findByUserId(user.id());
    }
}
