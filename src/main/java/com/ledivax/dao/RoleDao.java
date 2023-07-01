package com.ledivax.dao;

import com.ledivax.dao.abstractDao.AbstractDao;
import com.ledivax.models.Role;
import org.springframework.stereotype.Repository;

@Repository
public class RoleDao extends AbstractDao<Role, Long> {
    public RoleDao() {
        super(Role.class);
    }
}
