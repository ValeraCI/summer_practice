package com.ledivax.dao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class RoleDaoTest {
    @Autowired
    private RoleDao roleDao;

    @Test
    public void testRoleDao() {
        String[] programRoles = new String[]{
                "ROLE_OWNER", "ROLE_ADMINISTRATOR", "ROLE_USER"
        };
        String daoRoles[] = new String[programRoles.length];
        for (int i = 0; i < daoRoles.length; i++) {
            daoRoles[i] = String.valueOf(roleDao.findById(i + 1L).getRoleTitle());
        }

        assertArrayEquals(programRoles, daoRoles);
    }
}
