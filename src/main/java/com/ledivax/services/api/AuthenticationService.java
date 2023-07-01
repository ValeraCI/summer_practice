package com.ledivax.services.api;

import com.ledivax.dto.AuthRequest;
import org.springframework.http.ResponseEntity;

public interface AuthenticationService {
    ResponseEntity login(AuthRequest request);
}
