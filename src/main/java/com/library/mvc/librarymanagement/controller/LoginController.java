package com.library.mvc.librarymanagement.controller;

import com.library.mvc.librarymanagement.entity.User;
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
            @RequestParam(value = "locked", required = false) String locked,
            Model model) {

        if (locked != null) {
            model.addAttribute(
                    "errorMessage",
                    "Tài khoản đã bị khóa. Vui lòng liên hệ quản trị viên.");
        } else if (error != null) {
            model.addAttribute(
                    "errorMessage",
                    "Tên đăng nhập hoặc mật khẩu không đúng.");
        }

        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(
            @RequestParam("member-id") String memberId,
            @RequestParam String password,
            HttpSession session) {

        Optional<User> user = userService.login(memberId, password);
        if (user.isEmpty()) {
            return "redirect:/login?error=true";
        }

        if (!"active".equalsIgnoreCase(user.get().getStatus())) {
            return "redirect:/login?locked=true";
        }

        session.setAttribute("user", user.get());

        if ("admin".equalsIgnoreCase(user.get().getRole())) {
            return "redirect:/admin/dashboard";
        }

        if ("manager".equalsIgnoreCase(user.get().getRole())) {
            return "redirect:/manager/dashboard";
        }

        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
