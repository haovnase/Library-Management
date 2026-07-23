package com.library.mvc.librarymanagement.controller;

import com.library.mvc.librarymanagement.entity.User;
import com.library.mvc.librarymanagement.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController
{
    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {

        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null) {
            return "redirect:/login";
        }

        if (!"admin".equalsIgnoreCase(currentUser.getRole())) {
            return "redirect:/";
        }

        model.addAttribute("user", currentUser);
        model.addAttribute("activeTab", "dashboard");

        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String userManagement(HttpSession session, Model model) {

        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null) {
            return "redirect:/login";
        }

        if (!"admin".equalsIgnoreCase(currentUser.getRole())) {
            return "redirect:/";
        }

        List<User> users = userService.findAll();

        model.addAttribute("user", currentUser);
        model.addAttribute("users", users);
        model.addAttribute("activeTab", "users");

        return "admin/dashboard";
    }

    @GetMapping("/users/search")
    public String searchUser(@RequestParam String keyword,
                             HttpSession session,
                             Model model) {

        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null) {
            return "redirect:/login";
        }

        if (!"admin".equalsIgnoreCase(currentUser.getRole())) {
            return "redirect:/";
        }

        List<User> users = userService.searchByUsername(keyword);

        model.addAttribute("user", currentUser);
        model.addAttribute("users", users);
        model.addAttribute("keyword", keyword);
        model.addAttribute("activeTab", "users");

        return "admin/dashboard";
    }

    @PostMapping("/users/lock")
    public String lockUser(@RequestParam String id) {

        User user = userService.findById(id);

        if (user != null) {
            user.setStatus("locked");
            userService.save(user);
        }

        return "redirect:/admin/users";
    }

    @PostMapping("/users/unlock")
    public String unlockUser(@RequestParam String id) {

        User user = userService.findById(id);

        if (user != null) {
            user.setStatus("active");
            userService.save(user);
        }

        return "redirect:/admin/users";
    }

    @PostMapping("/users/change-role")
    public String changeRole(@RequestParam String id,
                             @RequestParam String role) {

        User user = userService.findById(id);

        if (user != null) {
            user.setRole(role);
            userService.save(user);
        }

        return "redirect:/admin/users";
    }
    @PostMapping("/users/delete")
    public String deleteUser(@RequestParam String id,
                             HttpSession session) {

        User currentUser = (User) session.getAttribute("user");

        if (currentUser.getId().equals(id)) {
            return "redirect:/admin/users";
        }

        userService.lockUser(id);

        return "redirect:/admin/users";
    }


}
