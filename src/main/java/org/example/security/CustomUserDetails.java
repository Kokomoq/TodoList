package org.example.security;

import org.example.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {
    private User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Assuming that the User class has a method getAuthorities() that returns a collection of GrantedAuthority
        // If the User class does not have such a method, you need to modify this method accordingly.
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        // You can customize this based on your requirements
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // You can customize this based on your requirements
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // You can customize this based on your requirements
        return true;
    }

    @Override
    public boolean isEnabled() {
        // You can customize this based on your requirements
        return true;
    }
}
