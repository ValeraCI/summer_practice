package com.ledivax.service;

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
import com.ledivax.models.LoginDetails;
import com.ledivax.models.Role;
import com.ledivax.models.RoleTitle;
import com.ledivax.services.AccountServiceImpl;
import com.ledivax.util.ObjectCreator;
import com.ledivax.util.mappers.AccountMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceImplTest {
    @Mock
    private AccountDao accountDao;
    @Mock
    private AlbumDao albumDao;
    @Mock
    private RoleDao roleDao;
    @Mock
    private AccountMapper accountMapper;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    public void testSave() {
        RegistrationRequest accountDataDto = new RegistrationRequest();
        Account account = ObjectCreator.createAccount(1L, "test", "test", "1234");

        when(accountMapper.toEntity(accountDataDto)).thenReturn(account);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(accountDao.save(account)).thenReturn(1L);

        Long id = accountService.save(accountDataDto);

        assertEquals(Long.valueOf(1), id);
        verify(accountDao).save(account);
        verify(passwordEncoder).encode("1234");
    }

    @Test
    public void testFindAccountMainDataDtoById() {
        Account account = new Account();
        AccountMainDataDto accountMainDataDto = new AccountMainDataDto();
        accountMainDataDto.setId(1L);

        when(accountDao.findById(anyLong())).thenReturn(account);
        when(accountMapper.toAccountMainDataDto(account)).thenReturn(accountMainDataDto);

        accountMainDataDto = accountService.findById(1L);

        assertEquals(Long.valueOf(1), accountMainDataDto.getId());
        verify(accountDao).findById(1L);
        verify(accountMapper).toAccountMainDataDto(account);
    }

    @Test
    public void testFindAllAccountMainDataDto() {
        List<Account> accounts = new ArrayList<>();
        List<AccountMainDataDto> accountMainDataDtoList = new ArrayList<>();

        when(accountDao.getTotalCount()).thenReturn(10L);
        when(accountDao.findAll(0, 10)).thenReturn(accounts);
        when(accountMapper.toAccountMainDataDtoList(accounts)).thenReturn(accountMainDataDtoList);

        List<AccountMainDataDto> result = accountService.findAll("1", "10");

        assertEquals(result, accountMainDataDtoList);
        verify(accountDao).getTotalCount();
        verify(accountDao).findAll(0, 10);
        verify(accountMapper).toAccountMainDataDtoList(accounts);
    }

    @Test
    public void testUpdateData() {
        UpdateAccountDataDto accountUpdateDto = new UpdateAccountDataDto("test", "pass");
        Account account = new Account();
        account.setLoginDetails(new LoginDetails());
        account.setId(1L);
        account.setRole(new Role(3L, RoleTitle.ROLE_USER));

        when(accountDao.findById(anyLong())).thenReturn(account);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        accountService.updateData(1L, accountUpdateDto);

        assertEquals("test", account.getNickname());
        assertEquals("encodedPassword", account.getLoginDetails().getPassword());
        verify(accountDao).findById(1L);
        verify(passwordEncoder).encode("pass");
        verify(accountDao).update(account);
    }

    @Test
    public void testUpdateRole() {
        UpdateAccountRoleDto accountUpdateDto = new UpdateAccountRoleDto(2L);
        Account account = ObjectCreator.createAccount(1L, "test", "test", "test");
        Role role = new Role(2L, RoleTitle.ROLE_ADMINISTRATOR);

        when(accountDao.findById(anyLong())).thenReturn(account);
        when(roleDao.findById(2L)).thenReturn(role);

        accountService.updateRole(1L, accountUpdateDto);

        assertEquals(role, account.getRole());
        verify(accountDao).findById(1L);
        verify(roleDao).findById(2L);
        verify(accountDao).update(account);
    }

    @Test
    public void testUpdateRoleToOwner() {
        UpdateAccountRoleDto accountUpdateDto = new UpdateAccountRoleDto(1L);
        Account userAccount = ObjectCreator.createAccount(1L, "test", "test", "test");
        Account ownerAccount = ObjectCreator.createOwnerAccount(2L, "test", "test", "test");
        Role administratorRole = new Role(2L, RoleTitle.ROLE_ADMINISTRATOR);
        Role ownerRole = new Role(1L, RoleTitle.ROLE_OWNER);

        when(accountDao.findById(anyLong())).thenReturn(userAccount);
        when(roleDao.findById(2L)).thenReturn(administratorRole);
        when(roleDao.findById(1L)).thenReturn(ownerRole);
        when(accountDao.findByRole(RoleTitle.ROLE_OWNER)).thenReturn(ownerAccount);

        accountService.updateRole(1L, accountUpdateDto);

        assertEquals(ownerRole, userAccount.getRole());
        assertEquals(administratorRole, ownerAccount.getRole());
        verify(accountDao).findById(1L);
        verify(roleDao).findById(1L);
        verify(accountDao).findByRole(RoleTitle.ROLE_OWNER);
        verify(roleDao).findById(2L);
        verify(accountDao).update(ownerAccount);
        verify(accountDao).update(userAccount);
    }

    @Test
    public void testUpdateOwnerRole() {
        UpdateAccountRoleDto accountUpdateDto = new UpdateAccountRoleDto(2L);
        Account account = ObjectCreator.createOwnerAccount(1L, "test", "test", "test");

        when(accountDao.findById(anyLong())).thenReturn(account);

        DataChangesException dataChangesException = assertThrows(DataChangesException.class, () -> {
            accountService.updateRole(1L, accountUpdateDto);
        });

        assertEquals("You can't change the 'OWNER' role", dataChangesException.getMessage());
        verify(accountDao).findById(1L);
    }

    @Test
    public void testDeleteById() {
        accountService.deleteById(1L);

        verify(accountDao).deleteById(1L);
    }

    @Test
    public void testAddSavedAlbum() {
        Account account = ObjectCreator.createAccount(1L, "test", "test", "test");
        Album album = new Album();

        when(accountDao.findWithSavedAlbumsById(anyLong())).thenReturn(account);
        when(albumDao.findById(anyLong())).thenReturn(album);

        accountService.addSavedAlbum(1L, 1L);

        assertEquals(1L, account.getSavedAlbums().size());
        verify(accountDao).findWithSavedAlbumsById(1L);
        verify(albumDao).findById(1L);
    }

    @Test
    public void testAddSavedAlbumException() {
        Account account = ObjectCreator.createAccount(1L, "test", "test", "test");
        Album album = new Album();
        account.getSavedAlbums().add(album);

        when(accountDao.findWithSavedAlbumsById(anyLong())).thenReturn(account);
        when(albumDao.findById(anyLong())).thenReturn(album);

        DataChangesException dataChangesException = assertThrows(DataChangesException.class, () -> {
            accountService.addSavedAlbum(1L, 1L);
        });

        assertEquals("Album has already been saved", dataChangesException.getMessage());
        verify(accountDao).findWithSavedAlbumsById(1L);
        verify(albumDao).findById(1L);
    }

    @Test
    public void testRemoveSavedAlbum() {
        Account account = ObjectCreator.createAccount(1L, "test", "test", "test");
        Album album = new Album();
        account.getSavedAlbums().add(album);

        when(accountDao.findWithSavedAlbumsById(anyLong())).thenReturn(account);
        when(albumDao.findById(anyLong())).thenReturn(album);

        accountService.removeSavedAlbum(1L, 1L);

        assertEquals(0L, account.getSavedAlbums().size());
        verify(accountDao).findWithSavedAlbumsById(1L);
        verify(albumDao).findById(1L);
    }

    @Test
    public void testRemoveSavedAlbumException() {
        Account account = ObjectCreator.createAccount(1L, "test", "test", "test");
        Album album = new Album();

        when(accountDao.findWithSavedAlbumsById(anyLong())).thenReturn(account);
        when(albumDao.findById(anyLong())).thenReturn(album);

        DataChangesException dataChangesException = assertThrows(DataChangesException.class, () -> {
            accountService.removeSavedAlbum(1L, 1L);
        });

        assertEquals("Album not saved", dataChangesException.getMessage());
        verify(accountDao).findWithSavedAlbumsById(1L);
        verify(albumDao).findById(1L);
    }
}
