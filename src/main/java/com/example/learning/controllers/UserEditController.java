package com.example.learning.controllers;

import com.example.learning.model.Role;
import com.example.learning.model.User;
import com.example.learning.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

@Controller
@RequestMapping("/userEdit")
public class UserEditController {

    @Autowired
    UserService userService;


    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("edit/{user}")
    public String userEditForm(@PathVariable User user, Model model) {
        model.addAttribute("user", user);
        model.addAttribute("roles", Arrays.asList(Role.values()));
        return "user/userEdit";
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/edit")
    public String editUser(
            @ModelAttribute User updatedUser,
            @RequestParam Map<String, String> form,
            @RequestParam("userId") User user) {

//        user.setUsername(username);
        userService.updateUserInfoByAdmin(updatedUser, form, user);
        return "redirect:/user/showAllUsers";
    }


    @GetMapping("/profile")
    public String userProfile(
            @AuthenticationPrincipal User user,
            Model model) {
        model.addAttribute("user", user);

        return "user/userProfile";
    }

    @PostMapping("/profile")
    public String updateProfile(
            @AuthenticationPrincipal User user,
            @RequestParam String password,
            @RequestParam String email
    ) {
        userService.updateProfile(user, password, email);

        return "redirect:/userEdit/profile";
    }
}


