package com.ledivax.dao;

import com.ledivax.exceptions.DataBaseWorkException;
import com.ledivax.models.Account;
import com.ledivax.util.ObjectCreator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class AccountDaoTest {
    @Autowired
    private AccountDao accountDao;

    @Test
    public void testFindById() {
        Account account = accountDao.findById(1L);

        assertNotNull(account);
        assertEquals("Valerix", account.getNickname());
        assertEquals(1L, account.getRole().getId().longValue());
    }

    @Test
    public void testFindByEmail() {
        Account account = accountDao.findByEmail("cidikvalera@gmail.com");

        assertNotNull(account);
        assertEquals("cidikvalera@gmail.com", account.getLoginDetails().getEmail());
        assertEquals("Valerix", account.getNickname());
        assertEquals(1L, account.getRole().getId().longValue());
    }

    @Test
    public void testFindAll() {
        List<Account> accounts = accountDao.findAll(1, 10);

        assertNotNull(accounts);
        assertTrue(accounts.size() <= 10);
    }

    @Test
    public void testSave() {
        Account account = ObjectCreator.createAccount(null, "Tester", "test@mail.ru", "1234");

        Long index = accountDao.save(account);
        account = accountDao.findById(index);

        assertNotNull(account);
        assertEquals("test@mail.ru", account.getLoginDetails().getEmail());
        assertEquals("Tester", account.getNickname());
        assertEquals(3L, account.getRole().getId().longValue());
    }

    @Test
    public void testUpdate() {
        Account account = accountDao.findById(2L);
        assertEquals("MaJIeHkuu_Ho_BeJIuKuu", account.getNickname());

        account.setNickname("MaJIeHkuu_u_He_BeJIuKuu");
        accountDao.update(account);

        account = accountDao.findById(2L);
        assertEquals("MaJIeHkuu_u_He_BeJIuKuu", account.getNickname());
    }

    @Test
    public void testUpdatePassword() {
        Account account = accountDao.findById(1L);
        account.getLoginDetails().setPassword("123456");

        account = accountDao.findById(1L);

        assertEquals("cidikvalera@gmail.com", account.getLoginDetails().getEmail());
        assertEquals("Valerix", account.getNickname());
        assertEquals(1, account.getRole().getId().longValue());
        assertEquals("123456", account.getLoginDetails().getPassword());
    }

    @Test
    public void testDeleteById() {
        accountDao.deleteById(3L);

        DataBaseWorkException dataBaseWorkException =
                assertThrows(DataBaseWorkException.class, () -> {
                    accountDao.findById(3L);
                });

        assertEquals("No entity found for query", dataBaseWorkException.getMessage());
    }

    @Test
    public void testFindWithSavedAlbumsByIdInBetween() {
        List<Account> accounts = accountDao.findWithSavedAlbumsByIdInBetween(1L, 7L);

        for (Account account : accounts) {
            assertNotNull(account.getSavedAlbums());
            assertTrue(account.getId() >= 1);
            assertTrue(account.getId() < 7);
        }
    }

    @Test
    void testFindByIds() {
        Set<Long> accountIds = Set.of(1L, 2L, 3L);

        List<Account> accounts = accountDao.findByIds(accountIds);

        assertNotNull(accountIds);
        assertEquals(3, accounts.size());
    }

    @Test
    public void testGetTotalCount() {
        Long num = accountDao.getTotalCount();

        assertEquals(10, num);
    }
}
