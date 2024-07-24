package by.alex.account.controller;

import by.alex.account.service.AccountBlockedException;
import by.alex.account.service.InsufficientFundsException;
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
        var user = userService.findByUsername(userDetails.getUsername());
        var account = accountService.findByUserId(user.id());
        model.addAttribute("account", account);
        log.info("User {} is viewing account with ID {}", userDetails.getUsername(), account.id());
        return "user/account";
    }

    @PostMapping("/deposit")
    public String deposit(@AuthenticationPrincipal UserDetails userDetails,
                          @RequestParam BigDecimal amount,
                          Model model) {
        try {
            var user = userService.findByUsername(userDetails.getUsername());
            var account = accountService.findByUserId(user.id());
            accountService.deposit(account, amount);
            log.info("User {} deposited {} to account with ID {}", userDetails.getUsername(), amount, account.id());
        } catch (AccountBlockedException | InsufficientFundsException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            var user = userService.findByUsername(userDetails.getUsername());
            var account = accountService.findByUserId(user.id());
            model.addAttribute("account", account);
            log.error("Error during deposit operation for user {}: {}", userDetails.getUsername(), ex.getMessage());
            return "user/account";
        }
        return "redirect:/user/account";
    }

    @PostMapping("/withdraw")
    public String withdraw(@AuthenticationPrincipal UserDetails userDetails,
                           @RequestParam BigDecimal amount,
                           Model model) {
        try {
            var user = userService.findByUsername(userDetails.getUsername());
            var account = accountService.findByUserId(user.id());
            accountService.withdraw(account, amount);
            log.info("User {} withdrew {} from account with ID {}", userDetails.getUsername(), amount, account.id());
        } catch (AccountBlockedException | InsufficientFundsException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            var user = userService.findByUsername(userDetails.getUsername());
            var account = accountService.findByUserId(user.id());
            model.addAttribute("account", account);
            log.error("Error during withdraw operation for user {}: {}", userDetails.getUsername(), ex.getMessage());
            return "user/account";
        }
        return "redirect:/user/account";
    }
}
