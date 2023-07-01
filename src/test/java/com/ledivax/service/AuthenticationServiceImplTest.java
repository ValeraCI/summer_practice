package com.ledivax.service;

import com.ledivax.dto.AuthRequest;
import com.ledivax.services.AuthenticationServiceImpl;
import com.ledivax.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtUtil jwtUtil;
    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Test
    public void testLogin() {
        AuthRequest request = new AuthRequest("testEmail", "testPassword");
        String username = request.getEmail();
        String password = request.getPassword();
        UserDetails userDetails = User.builder()
                .username(username)
                .password(password)
                .roles("testRole")
                .build();


        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtUtil.create(authentication)).thenReturn("token");

        ResponseEntity responseEntity = authenticationService.login(request);

        Map<Object, Object> response = (Map<Object, Object>) responseEntity.getBody();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(username, response.get("username"));
        assertEquals("token", response.get("token"));
    }

    @Test
    public void testLoginException() {
        AuthRequest request = new AuthRequest("testEmail", "testPassword");

        Authentication authentication = new UsernamePasswordAuthenticationToken(null, null);
        authentication.setAuthenticated(false);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        BadCredentialsException badCredentialsException =
                assertThrows(BadCredentialsException.class, () -> {
                    authenticationService.login(request);
                });

        assertEquals("Invalid username or password", badCredentialsException.getMessage());
    }
}
