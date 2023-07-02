package com.ledivax.services;

import com.ledivax.annotations.Loggable;
import com.ledivax.dao.AccountDao;
import com.ledivax.models.Account;
import com.ledivax.models.AccountDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AccountDao accountDao;

    @Override
    @Loggable
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountDao.findByEmail(username);

        if (Objects.isNull(account)) {
            throw new UsernameNotFoundException("User with email: " + username + " not found");
        }

        return new AccountDetails(account);
    }
}






