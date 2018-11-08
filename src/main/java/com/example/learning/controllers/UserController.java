package com.example.learning.controllers;

import com.example.learning.model.Role;
import com.example.learning.model.User;
import com.example.learning.service.UserService;
import org.hibernate.validator.internal.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static java.lang.String.format;
import static java.time.LocalDate.*;


@Controller
@RequestMapping("/user")
public class UserController {

    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    @Autowired
    private UserService userService;


    @GetMapping("/showAllUsers")
    public String users(Model model) {
        Iterable<User> allUsers = userService.findAll();
        model.addAttribute("users", allUsers);
        return "user/users";
    }

    @GetMapping("/selectUsers")
    public String usersSelection(Model model) {
        List<Role> allRoles = Arrays.asList(Role.values());
        model.addAttribute("roles", allRoles);
        return "user/userSelection";
    }


    @GetMapping(value = "/showUsersWithDateOfBirthBetween")
    public String showUsersWithDateOfBirthBetween(@RequestParam String fromDateParam, @RequestParam String toDateParam, Model model) {
        LocalDate fromDate = StringHelper.isNullOrEmptyString(fromDateParam) ? of(1000, Month.JANUARY, 1) : parse(fromDateParam, formatter);
        LocalDate toDate = StringHelper.isNullOrEmptyString(toDateParam) ? now() : parse(toDateParam, formatter);
        Period period = Period.between(fromDate, toDate);
        Set<User> allUsers = userService.getAllByDateOfBirthBetween(fromDate, toDate);
        String message = allUsers.isEmpty() ? format("There are no users with Date of birth between %s and %s", fromDate, toDate) :
                format("Showing users which were born between %s and %s", fromDate, toDate);
        model.addAttribute("users", allUsers);
        model.addAttribute("message", message);
        return "user/users";
    }

    @GetMapping(value = "/showUsersWithRole")
    public String showUsersWithRole(@RequestParam String role, Model model) {
        Set<User> allUsersWithRole = userService.getAllUsersWithRole(role);
        model.addAttribute("users", allUsersWithRole);
        String message = allUsersWithRole.isEmpty() ? format("There are no users with role %s yet", role) :
                format("Showing users with role %s", role);
        model.addAttribute("message", message);
        return "user/users";
    }


}
