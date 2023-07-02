package com.ledivax.dao;

import com.ledivax.dao.abstractDao.AbstractDao;
import com.ledivax.exceptions.DataBaseWorkException;
import com.ledivax.models.AEntity_;
import com.ledivax.models.Account;
import com.ledivax.models.Account_;
import com.ledivax.models.LoginDetails;
import com.ledivax.models.LoginDetails_;
import com.ledivax.models.Role;
import com.ledivax.models.RoleTitle;
import com.ledivax.models.Role_;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Set;

@Repository
public class AccountDao extends AbstractDao<Account, Long> {

    public AccountDao() {
        super(Account.class);
    }

    public Account findByEmail(String email) {
        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

            CriteriaQuery<Account> query = criteriaBuilder.createQuery(typeParameterClass);

            Root<Account> root = query.from(typeParameterClass);
            Join<Account, LoginDetails> join = root.join(Account_.LOGIN_DETAILS);
            root.fetch(Account_.ROLE, JoinType.INNER);
            query
                    .select(root)
                    .where(criteriaBuilder
                            .equal(join.get(LoginDetails_.EMAIL), email)
                    );

            return entityManager.createQuery(query).getSingleResult();
        } catch (Exception e) {
            throw new DataBaseWorkException(e.getMessage(), e);
        }
    }

    public Account findByRole(RoleTitle roleTitle) {
        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

            CriteriaQuery<Account> query = criteriaBuilder.createQuery(typeParameterClass);

            Root<Account> root = query.from(typeParameterClass);
            Join<Account, Role> join = root.join(Account_.ROLE);
            query
                    .select(root)
                    .where(criteriaBuilder
                            .equal(join.get(Role_.ROLE_TITLE), roleTitle)
                    );

            return entityManager.createQuery(query).getSingleResult();
        } catch (Exception e) {
            throw new DataBaseWorkException(e.getMessage(), e);
        }
    }

    @Override
    public void update(Account entity) {
        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

            CriteriaUpdate<Account> criteriaAccountUpdate =
                    criteriaBuilder.createCriteriaUpdate(typeParameterClass);
            Root<Account> rootAccount = criteriaAccountUpdate.from(typeParameterClass);

            criteriaAccountUpdate
                    .set(rootAccount.get(Account_.NICKNAME), entity.getNickname())
                    .set(rootAccount.get(Account_.ROLE), entity.getRole())
                    .where(criteriaBuilder.equal(rootAccount.get(AEntity_.ID), entity.getId()));

            entityManager.createQuery(criteriaAccountUpdate).executeUpdate();
        } catch (Exception e) {
            throw new DataBaseWorkException(e.getMessage(), e);
        }
    }

    public Account findWithSavedAlbumsById(Long id) {
        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

            CriteriaQuery<Account> query = criteriaBuilder.createQuery(typeParameterClass);

            Root<Account> root = query.from(typeParameterClass);
            root.fetch(Account_.ROLE, JoinType.INNER);
            root.fetch(Account_.SAVED_ALBUMS, JoinType.LEFT);
            query
                    .select(root)
                    .where(criteriaBuilder
                            .equal(root.get(Account_.ID), id)
                    );

            return entityManager.createQuery(query).getSingleResult();
        } catch (Exception e) {
            throw new DataBaseWorkException(e.getMessage(), e);
        }
    }

    public List<Account> findWithSavedAlbumsByIdInBetween(Long minId, Long maxId) {
        try {
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Account> query = builder.createQuery(typeParameterClass);

            Root<Account> root = query.from(typeParameterClass);
            root.fetch(Account_.SAVED_ALBUMS, JoinType.LEFT);

            query.select(root).where(
                    builder.and(
                            builder.greaterThanOrEqualTo(root.get(Account_.ID), minId),
                            builder.lessThan(root.get(Account_.ID), maxId)
                    )
            );

            return entityManager.createQuery(query).getResultList();
        } catch (Exception e) {
            throw new DataBaseWorkException(e.getMessage(), e);
        }
    }

    public List<Account> findByIds(Set<Long> ids) {
        try {
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Account> query = builder.createQuery(typeParameterClass);

            Root<Account> root = query.from(typeParameterClass);
            query.select(root).where(root.get(Account_.ID).in(ids));

            return entityManager.createQuery(query).getResultList();
        } catch (Exception e) {
            throw new DataBaseWorkException(e.getMessage(), e);
        }
    }

    public Long getTotalCount() {
        try {
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Long> query = builder.createQuery(Long.class);
            Root<Account> root = query.from(typeParameterClass);

            query.select(builder.count(root));

            return entityManager.createQuery(query).getSingleResult();
        } catch (Exception e) {
            throw new DataBaseWorkException(e.getMessage(), e);
        }
    }
}