package com.library.mvc.librarymanagement.controller;

import com.library.mvc.librarymanagement.entity.User;
import com.library.mvc.librarymanagement.repository.UserRepository;
import com.library.mvc.librarymanagement.service.UserService;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class LoginController {

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage(
            @RequestParam(value = "error", required = false) String error,
            Model model) {

        if (error != null) {
            model.addAttribute("errorMessage",
                    "Mã thành viên hoặc mật khẩu không đúng.");
        }

        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(
            @RequestParam("member-id") String memberId,
            @RequestParam String password,
            HttpSession session) {

        Optional<User> user = userService.login(memberId, password);

        if (user.isPresent()) {

            session.setAttribute("user", user.get());

            if ("manager".equalsIgnoreCase(user.get().getRole())) {
                return "redirect:/manager/dashboard";
            }

            return "redirect:/";
        }

        return "redirect:/login?error=true";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
