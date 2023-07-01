package com.ledivax.dao.abstractDao;

import com.ledivax.exceptions.DataBaseWorkException;
import com.ledivax.models.AEntity;
import com.ledivax.models.AEntity_;
import lombok.RequiredArgsConstructor;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.List;

@RequiredArgsConstructor
public abstract class AbstractDao<T extends AEntity, PK extends Serializable> implements GenericDao<T, Long> {
    protected final Class<T> typeParameterClass;

    @PersistenceContext
    protected EntityManager entityManager;

    @Override
    public Long save(T entity) {
        try {
            entityManager.persist(entity);
            entityManager.flush();
            return entity.getId();
        } catch (PersistenceException e) {
            throw new DataBaseWorkException("This email is already registered", e);
        } catch (Exception e) {
            throw new DataBaseWorkException(e.getMessage(), e);
        }
    }

    @Override
    public void update(T entity) {
        try {
            entityManager.merge(entity);
            entityManager.flush();
        } catch (Exception e) {
            throw new DataBaseWorkException(e.getMessage(), e);
        }
    }

    @Override
    public void deleteById(Long id) {
        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

            CriteriaDelete<T> criteriaDelete = criteriaBuilder.createCriteriaDelete(typeParameterClass);
            Root<T> root = criteriaDelete.from(typeParameterClass);
            criteriaDelete.where(criteriaBuilder.equal(root.get(AEntity_.ID), id));
            entityManager.createQuery(criteriaDelete).executeUpdate();
            entityManager.flush();
        } catch (Exception e) {
            throw new DataBaseWorkException(e.getMessage(), e);
        }
    }

    @Override
    public List<T> findAll(Integer firstResult, Integer maxResults) {
        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

            CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(typeParameterClass);
            Root<T> root = criteriaQuery.from(typeParameterClass);

            criteriaQuery.orderBy(criteriaBuilder.asc(root.get(AEntity_.ID)));

            criteriaQuery.select(root);

            TypedQuery<T> typedQuery = entityManager.createQuery(criteriaQuery);

            typedQuery.setFirstResult(firstResult);
            typedQuery.setMaxResults(maxResults);

            return typedQuery.getResultList();
        } catch (Exception e) {
            throw new DataBaseWorkException(e.getMessage(), e);
        }
    }

    @Override
    public T findById(Long id) {
        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

            CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(typeParameterClass);
            Root<T> root = criteriaQuery.from(typeParameterClass);

            criteriaQuery
                    .select(root)
                    .where(criteriaBuilder
                            .equal(root.get(AEntity_.ID), id)
                    );

            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (Exception e) {
            throw new DataBaseWorkException(e.getMessage(), e);
        }
    }
}
