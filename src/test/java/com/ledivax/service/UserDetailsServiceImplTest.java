package com.ledivax.service;

import com.ledivax.dao.AccountDao;
import com.ledivax.models.Account;
import com.ledivax.models.LoginDetails;
import com.ledivax.models.Role;
import com.ledivax.models.RoleTitle;
import com.ledivax.services.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplTest {

    @Mock
    private AccountDao accountDao;
    @InjectMocks
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Test
    public void testLoadUserByUsername() {
        Account account = new Account();
        account.setNickname("test");
        account.setLoginDetails(new LoginDetails(account, "test", "test"));
        account.setRole(new Role(1L, RoleTitle.ROLE_OWNER));

        when(accountDao.findByEmail(anyString())).thenReturn(account);

        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername("test");

        assertEquals("test", userDetails.getUsername());
        assertEquals("test", userDetails.getUsername());
        verify(accountDao).findByEmail("test");
    }

    @Test
    public void testLoadUserByUsernameException() {
        when(accountDao.findByEmail(anyString())).thenReturn(null);

        UsernameNotFoundException usernameNotFoundException =
                assertThrows(UsernameNotFoundException.class, () ->
                    userDetailsServiceImpl.loadUserByUsername("test")
                );

        assertEquals("User with email: test not found", usernameNotFoundException.getMessage());
        verify(accountDao).findByEmail("test");
    }
}