package by.alex.account.controller;

import by.alex.account.service.impl.AccountServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
@AllArgsConstructor
@Slf4j
public class AdminController {

    private final AccountServiceImpl accountService;

    @GetMapping("/accounts")
    public String listAccounts(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               Model model) {
        var accountPage = accountService.findAll(page, size);
        model.addAttribute("accounts", accountPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", accountPage.getTotalPages());
        model.addAttribute("pageSize", size);
        log.info("Admin retrieved account list: page={}, size={}", page, size);
        return "admin/accounts";
    }

    @PostMapping("/block")
    public String blockAccount(@RequestParam("accountId") Integer accountId) {
        var accountDTO = accountService.findById(accountId);
        accountService.blockAccount(accountDTO);
        log.info("Admin blocked account with ID {}", accountId);
        return "redirect:/admin/accounts";
    }

    @PostMapping("/unblock")
    public String unblockAccount(@RequestParam("accountId") Integer accountId) {
        var accountDTO = accountService.findById(accountId);
        accountService.unblockAccount(accountDTO);
        log.info("Admin unblocked account with ID {}", accountId);
        return "redirect:/admin/accounts";
    }
}
