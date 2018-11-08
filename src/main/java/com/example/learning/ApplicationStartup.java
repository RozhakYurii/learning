package com.example.learning;

import com.example.learning.model.Role;
import com.example.learning.model.User;
import com.example.learning.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;

@Component
public class ApplicationStartup
        implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    UserRepository userRepository;
    @Autowired
    Environment environment;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


    /**
     * This event is executed as late as conceivably possible to indicate that
     * the application is ready to service requests.
     */
    @Override
    @Transactional
    public void onApplicationEvent(final ApplicationReadyEvent event) {

        final User existedInitUser = userRepository.getByUsername(environment.getProperty("demo.admin.username"));

        Set<Role> roleSet = new HashSet<>(asList(Role.values()));
        if (existedInitUser == null) {
            final User user = User.builder()
                    .email(environment.getProperty("demo.admin.email"))
                    .password(passwordEncoder.encode(environment.getProperty("demo.admin.password")))
                    .username(environment.getProperty("demo.admin.username"))
                    .dateOfBirth(LocalDate.now().minusYears(24L))
                    .active(true)
                    .roles(roleSet)
                    .build();

            userRepository.save(user);
        }
    }

}