package com.example.learning.service;

import com.example.learning.model.Role;
import com.example.learning.model.User;
import com.example.learning.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Collections.singleton;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.getByUsername(username);
    }


    public boolean userAlreadyExists(User user) {
        return userRepository.existsByUsername(user.getUsername());
    }

    public void saveUserWithRoles(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(singleton(Role.USER));
        user.setActivationCode(UUID.randomUUID().toString());

        sendMessage(user);
        userRepository.save(user);

    }

    private void sendMessage(User user) {
        if (!StringUtils.isEmpty(user.getEmail())) {
            String message = String.format(
                    "Hi, %s !\n To activate your account at SpringDemoApp please follow the link bellow \n" +
                            "http://localhost:8080/activate/%s"
                    , user.getUsername(), user.getActivationCode()
            );

            mailService.sendEmail(user.getEmail(), "Activation Code", message);
        }
    }

    public boolean activateUser(String code) {
        User user = userRepository.findByActivationCode(code);

        if (user == null) {
            return false;

        }
        user.setActive(true);
        user.setActivationCode(null);
        userRepository.save(user);

        return true;
    }

    public void updateUserInfoByAdmin(User updatedUser, Map<String, String> form, User user) {
        final Set<String> roles = Arrays.stream(Role.values())
                .map(Role::name)
                .collect(Collectors.toSet());

        user.getRoles().clear();
        form.keySet().stream()
                .filter(roles::contains)
                .map(Role::valueOf)
                .forEach(user.getRoles()::add);
        user.setActive(updatedUser.isActive());
        user.setEmail(updatedUser.getEmail());
        user.setDateOfBirth(updatedUser.getDateOfBirth());

        userRepository.save(user);

    }

    public Iterable<User> findAll() {
        return userRepository.findAll();
    }

    public Set<User> getAllByDateOfBirthBetween(LocalDate fromDate, LocalDate toDate) {
        return userRepository.getAllByDateOfBirthBetween(fromDate, toDate);
    }

    public Set<User> getAllUsersWithRole(String role) {
        final Role userRole = Role.valueOf(role);
        return userRepository.getAllByRolesContaining(userRole);
    }

    public void updateProfile(User user, String password, String email) {
        String userEmail = user.getEmail();

        boolean isEmailChanged = (email != null && !email.equals(userEmail)) ||
                (userEmail != null && !userEmail.equals(email));

        if (isEmailChanged) {
            user.setEmail(email);

            if (!StringUtils.isEmpty(email)) {
                user.setActivationCode(UUID.randomUUID().toString());
            }

            sendMessage(user);
        }
        if (!StringUtils.isEmpty(password)) {
            user.setPassword(passwordEncoder.encode(password));
        }

        userRepository.save(user);

    }
}
