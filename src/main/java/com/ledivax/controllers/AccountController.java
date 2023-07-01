package com.ledivax.controllers;

import com.ledivax.annotations.Loggable;
import com.ledivax.dto.account.AccountMainDataDto;
import com.ledivax.dto.account.RegistrationRequest;
import com.ledivax.dto.account.UpdateAccountDataDto;
import com.ledivax.dto.account.UpdateAccountRoleDto;
import com.ledivax.services.api.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public List<AccountMainDataDto> findAll(
            @RequestParam(name = "pageNumber", defaultValue = "1") String pageNumber,
            @RequestParam(name = "limit", defaultValue = "10") String limit) {

        return accountService.findAll(pageNumber, limit);
    }


    @Loggable
    @GetMapping("/{id}")
    public AccountMainDataDto findById(@PathVariable("id") Long id) {
        return accountService.findById(id);
    }

    @Loggable
    @PostMapping("/register")
    public Long save(@Valid @RequestBody RegistrationRequest loginRequest) {
        return accountService.save(loginRequest);
    }

    @Loggable
    @DeleteMapping("/{id}")
    @PreAuthorize("@accountSecurityService.hasAccess(#id, principal)")
    public void removeById(@PathVariable("id") Long id) {
        accountService.deleteById(id);
    }

    @Loggable
    @PatchMapping("/{id}")
    @PreAuthorize("@accountSecurityService.hasAccess(#id, principal)")
    public void updateData(@PathVariable("id") Long id,
                           @Valid @RequestBody UpdateAccountDataDto accountUpdateDto) {

        accountService.updateData(id, accountUpdateDto);
    }

    @Loggable
    @PostMapping("/{accountId}/albums/{albumId}")
    @PreAuthorize("@accountSecurityService.hasAccess(#accountId, principal)")
    public void addSavedAlbum(@PathVariable("accountId") Long accountId,
                              @PathVariable("albumId") Long albumId) {

        accountService.addSavedAlbum(accountId, albumId);
    }

    @Loggable
    @DeleteMapping("/{accountId}/albums/{albumId}")
    @PreAuthorize("@accountSecurityService.hasAccess(#accountId, principal)")
    public void removeSavedAlbum(@PathVariable("accountId") Long accountId,
                                 @PathVariable("albumId") Long albumId) {

        accountService.removeSavedAlbum(accountId, albumId);
    }

    @Loggable
    @PatchMapping("/role/{id}")
    public void updateRole(@PathVariable("id") Long id,
                           @Valid @RequestBody UpdateAccountRoleDto updateAccountRoleDto) {

        accountService.updateRole(id, updateAccountRoleDto);
    }
}
