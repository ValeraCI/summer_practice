package com.ledivax.services.security;

import com.ledivax.models.AccountDetails;
import com.ledivax.models.RoleTitle;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class AccountSecurityService {

    public boolean hasAccess(Long accountId, AccountDetails accountDetails) {
        return accountId.equals(accountDetails.getId())
                ||
                accountDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .anyMatch(auth -> auth.equals(RoleTitle.ROLE_ADMINISTRATOR.toString())
                                || auth.equals(RoleTitle.ROLE_OWNER.toString()));
    }
}
