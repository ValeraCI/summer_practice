package com.ledivax.models;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AccountDetails implements UserDetails {
    private final Long id;
    private final List<SimpleGrantedAuthority> authorities;
    private final String password;
    private final String username;

    public AccountDetails(Account account) {
        id = account.getId();

        authorities = new ArrayList(List.of(
                new SimpleGrantedAuthority(account.getRole().getRoleTitle().name())
        ));

        password = account.getLoginDetails().getPassword();

        username = account.getLoginDetails().getEmail();
    }

    public Long getId() {
        return id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
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
        return true;
    }
}
