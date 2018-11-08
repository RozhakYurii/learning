package com.example.learning.model;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

@Entity
@Data
@Builder
public class User implements UserDetails {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Email(message = "email isn`t correct")
    @NotBlank(message = "Email can`t be empty")
    private String email;

    private String activationCode;

    @NotBlank(message = "Username can`t be empty")
    @Length(min = 5, max = 12, message = "please select username from 5 to 12 characters")
    private String username;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Past(message = "We are seriously doubt that you will be born in the future \uD83D\uDE09")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Password can`t be empty")
    @Length(min = 8, message = "Password should be at least 8 characters long")
    private String password;

    @Transient
//    @NotBlank(message = "Password confirmation can`t be empty")
    private String passwordConfirmation;

    private boolean active;


    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}
