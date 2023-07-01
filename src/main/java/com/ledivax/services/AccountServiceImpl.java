package com.ledivax.services;

import com.ledivax.dao.AccountDao;
import com.ledivax.dao.AlbumDao;
import com.ledivax.dao.RoleDao;
import com.ledivax.dto.account.AccountMainDataDto;
import com.ledivax.dto.account.RegistrationRequest;
import com.ledivax.dto.account.UpdateAccountDataDto;
import com.ledivax.dto.account.UpdateAccountRoleDto;
import com.ledivax.exceptions.DataChangesException;
import com.ledivax.models.Account;
import com.ledivax.models.Album;
import com.ledivax.models.Role;
import com.ledivax.models.RoleTitle;
import com.ledivax.services.api.AccountService;
import com.ledivax.util.Convertor;
import com.ledivax.util.Paginator;
import com.ledivax.util.mappers.AccountMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountServiceImpl implements AccountService {

    private final AccountDao accountDao;
    private final AlbumDao albumDao;
    private final RoleDao roleDao;
    private final AccountMapper accountMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Long save(RegistrationRequest accountDataDto) {
        Account account = accountMapper.toEntity(accountDataDto);

        account.getLoginDetails().setPassword(
                passwordEncoder.encode(account.getLoginDetails().getPassword())
        );

        return accountDao.save(account);
    }

    @Override
    public AccountMainDataDto findById(Long id) {
        Account account = accountDao.findById(id);

        return accountMapper.toAccountMainDataDto(account);
    }


    @Override
    public List<AccountMainDataDto> findAll(String pageNumber, String limit) {
        Integer pageNumberInteger = Convertor.stringToInteger(pageNumber);
        Integer limitInteger = Convertor.stringToInteger(limit);

        limitInteger = Paginator.limitingMinimumValueToOne(limitInteger);

        Long totalCount = accountDao.getTotalCount();
        Integer firstResult = Paginator.getFirstElement(pageNumberInteger, totalCount, limitInteger);

        return accountMapper.toAccountMainDataDtoList(
                accountDao.findAll(Math.toIntExact(firstResult), limitInteger)
        );
    }

    @Override
    public void updateData(Long id, UpdateAccountDataDto accountUpdateDto) {
        Account account = accountDao.findById(id);

        String password = passwordEncoder.encode(accountUpdateDto.getPassword());
        account.getLoginDetails().setPassword(password);
        account.setNickname(accountUpdateDto.getNickname());
        accountDao.update(account);
    }

    @Override
    public void updateRole(Long id, UpdateAccountRoleDto accountUpdateDto) {
        Account account = accountDao.findById(id);

        if (account.getRole().getRoleTitle() == RoleTitle.ROLE_OWNER) {
            throw new DataChangesException("You can't change the 'OWNER' role");
        }

        Role role = roleDao.findById(accountUpdateDto.getRoleId());

        if (role.getRoleTitle() == RoleTitle.ROLE_OWNER) {
            Account owner = accountDao.findByRole(RoleTitle.ROLE_OWNER);
            owner.setRole(roleDao.findById(2L));
            accountDao.update(owner);
        }

        account.setRole(role);
        accountDao.update(account);
    }

    @Override
    public void deleteById(Long id) {
        accountDao.deleteById(id);
    }

    @Override
    public void addSavedAlbum(Long accountId, Long albumId) {
        Account account = accountDao.findWithSavedAlbumsById(accountId);
        Album album = albumDao.findById(albumId);

        if (!account.getSavedAlbums().contains(album)) {
            account.getSavedAlbums().add(album);
        } else {
            throw new DataChangesException("Album has already been saved");
        }
    }

    @Override
    public void removeSavedAlbum(Long accountId, Long albumId) {
        Account account = accountDao.findWithSavedAlbumsById(accountId);
        Album album = albumDao.findById(albumId);

        if (account.getSavedAlbums().contains(album)) {
            account.getSavedAlbums().remove(album);
        } else {
            throw new DataChangesException("Album not saved");
        }
    }
}
