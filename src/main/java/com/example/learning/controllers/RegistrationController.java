package com.example.learning.controllers;


import com.example.learning.model.User;
import com.example.learning.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.Map;

import static com.example.learning.utils.ValidationUtils.getValidationErrors;
import static java.lang.String.format;

@Controller
public class RegistrationController {

    @Autowired
    UserService userService;


    @GetMapping(value = "/registration")
    public String showRegistrationPage() {
        return "registration";
    }

    @PostMapping(value = "/registration")
    public String addUser(@ModelAttribute @Valid User user, BindingResult bindingResult, Model model) {
        if (checkIfRegistrationDataHasErrors(user, bindingResult)) {
            processRegistrationErrors(user, bindingResult, model);
        } else {
            userService.saveUserWithRoles(user);
        }
        return "registration";
    }


    @GetMapping("/activate/{code}")
    public String activate(Model model, @PathVariable String code) {
        boolean isActivated = userService.activateUser(code);

        if (isActivated) {
            model.addAttribute("message", "User successfully activated");
        } else {
            model.addAttribute("message", "Wrong activation code");
        }
        return "login";
    }

    private boolean checkIfRegistrationDataHasErrors(User user, BindingResult bindingResult) {
        return (user.getPassword() != null && !user.getPassword().equals(user.getPasswordConfirmation()))
                || bindingResult.hasErrors()
                || userService.userAlreadyExists(user);
    }

    private void processRegistrationErrors(User user, BindingResult bindingResult, Model model) {
        if (user.getPassword() != null && !user.getPassword().equals(user.getPasswordConfirmation())) {
            model.addAttribute("passwordConfirmationError", "Password doesn't match password confirmation");
        }
        if (bindingResult.hasErrors()) {
            Map<String, String> errorAttributes = getValidationErrors(bindingResult);
            model.mergeAttributes(errorAttributes);
        }
        if (userService.userAlreadyExists(user)) {
            model.addAttribute("userExistsError", format("User with username %s already exists", user.getUsername()));
        }
        model.addAttribute("user", user);
    }

}
