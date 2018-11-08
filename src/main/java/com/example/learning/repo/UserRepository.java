package com.example.learning.repo;

import com.example.learning.model.Role;
import com.example.learning.model.User;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.Set;


public interface UserRepository extends CrudRepository<User, Integer> {


    Set<User> getAllByDateOfBirthBetween(LocalDate startDate, LocalDate endDate);

    Set<User> getAllByRolesContaining(Role role);

    User getByUsername(String username);

    boolean existsByUsername(String userName);

    User findByActivationCode(String activationCode);

}
