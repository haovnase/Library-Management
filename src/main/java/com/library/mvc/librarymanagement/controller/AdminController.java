package com.library.mvc.librarymanagement.controller;

import com.library.mvc.librarymanagement.dto.UserDTO;
import com.library.mvc.librarymanagement.entity.User;
import com.library.mvc.librarymanagement.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    // =========================
    // Dashboard
    // =========================

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session,
                            Model model) {

        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null) {
            return "redirect:/login";
        }

        if (!currentUser.getRole().equalsIgnoreCase("admin")) {
            return "redirect:/";
        }

        model.addAttribute("user", currentUser);
        model.addAttribute("users", userService.findAll());
        model.addAttribute("activeTab", "users");

        return "admin";
    }

    // =========================
    // User List
    // =========================

    @GetMapping("/users")
    public String userManagement(HttpSession session,
                                 Model model) {

        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null) {
            return "redirect:/login";
        }

        if (!currentUser.getRole().equalsIgnoreCase("admin")) {
            return "redirect:/";
        }

        List<User> users = userService.findAll();

        model.addAttribute("user", currentUser);
        model.addAttribute("users", users);
        model.addAttribute("activeTab", "users");

        return "admin";
    }

    // =========================
    // Search User
    // =========================

    @GetMapping("/users/search")
    public String searchUser(@RequestParam String keyword,
                             HttpSession session,
                             Model model) {

        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null) {
            return "redirect:/login";
        }

        if (!currentUser.getRole().equalsIgnoreCase("admin")) {
            return "redirect:/";
        }

        List<User> users = userService.searchByUsername(keyword);

        model.addAttribute("user", currentUser);
        model.addAttribute("users", users);
        model.addAttribute("keyword", keyword);
        model.addAttribute("activeTab", "users");

        return "admin";
    }

    // =========================
    // Show Add User Form
    // =========================

    @GetMapping("/users/add")
    public String showAddUser(HttpSession session,
                              Model model) {

        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null) {
            return "redirect:/login";
        }

        if (!currentUser.getRole().equalsIgnoreCase("admin")) {
            return "redirect:/";
        }

        model.addAttribute("user", currentUser);
        model.addAttribute("userDTO", new UserDTO());

        return "add-user";
    }

    // =========================
    // Add User
    // =========================

    @PostMapping("/users/add")
    public String addUser(
            @Valid
            @ModelAttribute("userDTO") UserDTO userDTO,

            BindingResult result,

            HttpSession session,

            Model model) {

        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null) {
            return "redirect:/login";
        }

        if (!currentUser.getRole().equalsIgnoreCase("admin")) {
            return "redirect:/";
        }

        if (userService.existsByUsername(userDTO.getUsername())) {

            result.rejectValue(
                    "username",
                    "error.username",
                    "Username already exists."
            );

        }

        if (result.hasErrors()) {

            model.addAttribute("user", currentUser);

            return "add-user";
        }

        User newUser = new User();

        newUser.setId(userService.generateNextUserId());
        newUser.setUsername(userDTO.getUsername());
        newUser.setPassword(userDTO.getPassword());
        newUser.setFullName(userDTO.getFullName());
        newUser.setRole(userDTO.getRole());
        newUser.setStatus(userDTO.getStatus());

        userService.createUser(newUser);

        return "redirect:/admin/users";
    }

    // =========================
    // Show Edit User Form
    // =========================

    @GetMapping("/users/edit/{id}")
    public String showEditUser(@PathVariable String id,
                               HttpSession session,
                               Model model) {

        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null) {
            return "redirect:/login";
        }

        if (!currentUser.getRole().equalsIgnoreCase("admin")) {
            return "redirect:/";
        }

        User user = userService.findById(id);

        if (user == null) {
            return "redirect:/admin/users";
        }

        UserDTO userDTO = new UserDTO();

        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setPassword(user.getPassword());
        userDTO.setFullName(user.getFullName());
        userDTO.setRole(user.getRole());
        userDTO.setStatus(user.getStatus());

        model.addAttribute("user", currentUser);
        model.addAttribute("userDTO", userDTO);

        return "edit-user";
    }

    // =========================
    // Edit User
    // =========================

    @PostMapping("/users/edit")
    public String editUser(
            @Valid
            @ModelAttribute("userDTO") UserDTO userDTO,

            BindingResult result,

            HttpSession session,

            Model model) {

        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null) {
            return "redirect:/login";
        }

        if (!currentUser.getRole().equalsIgnoreCase("admin")) {
            return "redirect:/";
        }

        if (userService.existsByUsernameExceptId(
                userDTO.getUsername(),
                userDTO.getId())) {

            result.rejectValue(
                    "username",
                    "error.username",
                    "Username already exists."
            );
        }

        if (result.hasErrors()) {

            model.addAttribute("user", currentUser);

            return "edit-user";
        }

        User user = userService.findById(userDTO.getId());

        if (user == null) {
            return "redirect:/admin/users";
        }

        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        user.setFullName(userDTO.getFullName());
        user.setRole(userDTO.getRole());
        user.setStatus(userDTO.getStatus());

        userService.updateUser(user);

        return "redirect:/admin/users";
    }

    // =========================
    // Lock User
    // =========================

    @PostMapping("/users/lock")
    public String lockUser(@RequestParam String id, HttpSession session) {

        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null) {
            return "redirect:/login";
        }

        if (!currentUser.getRole().equalsIgnoreCase("admin")) {
            return "redirect:/";
        }

        User user = userService.findById(id);

        if (user != null) {

            user.setStatus("locked");

            userService.updateUser(user);
        }

        return "redirect:/admin/users";
    }

    // =========================
    // Unlock User
    // =========================

    @PostMapping("/users/unlock")
    public String unlockUser(@RequestParam String id, HttpSession session) {

        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null) {
            return "redirect:/login";
        }

        if (!currentUser.getRole().equalsIgnoreCase("admin")) {
            return "redirect:/";
        }

        User user = userService.findById(id);

        if (user != null) {

            user.setStatus("active");

            userService.updateUser(user);
        }

        return "redirect:/admin/users";
    }

    // =========================
    // Change Role
    // =========================

    @PostMapping("/users/change-role")
    public String changeRole(@RequestParam String id,
                             @RequestParam String role,
                             HttpSession session) {

        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null) {
            return "redirect:/login";
        }

        if (!currentUser.getRole().equalsIgnoreCase("admin")) {
            return "redirect:/";
        }

        User user = userService.findById(id);

        if (user != null) {

            user.setRole(role);

            userService.updateUser(user);
        }

        return "redirect:/admin/users";
    }

    // =========================
    // Soft Delete
    // =========================

    @PostMapping("/users/delete")
    public String deleteUser(@RequestParam String id, HttpSession session) {

        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null) {
            return "redirect:/login";
        }

        if (!currentUser.getRole().equalsIgnoreCase("admin")) {
            return "redirect:/";
        }

        User user = userService.findById(id);

        if (user != null) {

            user.setStatus("locked");

            userService.updateUser(user);
        }

        return "redirect:/admin/users";
    }

}