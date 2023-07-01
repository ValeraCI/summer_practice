package com.ledivax.services.api;

import com.ledivax.dto.account.AccountMainDataDto;
import com.ledivax.dto.account.RegistrationRequest;
import com.ledivax.dto.account.UpdateAccountDataDto;
import com.ledivax.dto.account.UpdateAccountRoleDto;

import java.util.List;

public interface AccountService {

    Long save(RegistrationRequest accountDataDto);

    AccountMainDataDto findById(Long id);

    List<AccountMainDataDto> findAll(String firstResult, String limit);

    void updateData(Long id, UpdateAccountDataDto accountUpdateDto);

    void updateRole(Long id, UpdateAccountRoleDto accountUpdateDto);

    void deleteById(Long id);

    void addSavedAlbum(Long accountId, Long albumId);

    void removeSavedAlbum(Long accountId, Long albumId);
}
