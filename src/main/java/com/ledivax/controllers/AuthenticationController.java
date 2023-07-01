package com.ledivax.controllers;

import com.ledivax.annotations.Loggable;
import com.ledivax.dto.AuthRequest;
import com.ledivax.services.api.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/authenticate")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Loggable
    @GetMapping("/login")
    public ResponseEntity login(@RequestBody AuthRequest request) {
        return authenticationService.login(request);
    }
}
