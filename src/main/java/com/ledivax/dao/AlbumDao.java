package com.ledivax.dao;

import com.ledivax.dao.abstractDao.AbstractDao;
import com.ledivax.exceptions.DataBaseWorkException;
import com.ledivax.models.AEntity_;
import com.ledivax.models.Account;
import com.ledivax.models.Account_;
import com.ledivax.models.Album;
import com.ledivax.models.Album_;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Set;

@Repository
public class AlbumDao extends AbstractDao<Album, Long> {

    public AlbumDao() {
        super(Album.class);
    }

    public List<Album> findByTitle(String title, Integer firstResult, Integer maxResults) {
        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

            CriteriaQuery<Album> criteriaQuery = criteriaBuilder.createQuery(typeParameterClass);
            Root<Album> root = criteriaQuery.from(typeParameterClass);
            root.join(Album_.SONGS_IN, JoinType.LEFT);

            criteriaQuery
                    .select(root)
                    .where(criteriaBuilder
                            .like(root.get(Album_.TITLE), title + "%")
                    )
                    .groupBy(root.get(Album_.ID));

            TypedQuery<Album> typedQuery = entityManager.createQuery(criteriaQuery);

            typedQuery.setFirstResult(firstResult);
            typedQuery.setMaxResults(maxResults);

            return typedQuery.getResultList();
        } catch (Exception e) {
            throw new DataBaseWorkException(e.getMessage(), e);
        }
    }

    public List<Album> findSavedFromByAccountId(Long id) {
        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

            CriteriaQuery<Album> query = criteriaBuilder.createQuery(typeParameterClass);
            Root<Album> root = query.from(typeParameterClass);
            Join<Account, Album> join = root.join(Album_.SAVED_FROM);

            query.select(root)
                    .where(criteriaBuilder.equal(join.get(Account_.ID), id));

            return entityManager.createQuery(query).getResultList();
        } catch (Exception e) {
            throw new DataBaseWorkException(e.getMessage(), e);
        }
    }

    public List<Album> findCreatedFromAccountId(Long id) {
        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

            CriteriaQuery<Album> query = criteriaBuilder.createQuery(typeParameterClass);
            Root<Album> root = query.from(typeParameterClass);
            Join<Account, Album> join = root.join(Album_.CREATOR, JoinType.LEFT);

            query.select(root)
                    .where(criteriaBuilder.equal(join.get(Account_.ID), id));

            return entityManager.createQuery(query).getResultList();
        } catch (Exception e) {
            throw new DataBaseWorkException(e.getMessage(), e);
        }
    }

    public Album findByIdWithCreator(Long id) {
        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

            CriteriaQuery<Album> criteriaQuery = criteriaBuilder.createQuery(typeParameterClass);
            Root<Album> root = criteriaQuery.from(typeParameterClass);
            root.fetch(Album_.CREATOR);

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

    public List<Album> findByIds(Set<Long> ids) {
        try {
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Album> query = builder.createQuery(typeParameterClass);

            Root<Album> root = query.from(typeParameterClass);
            query.select(root).where(root.get(Account_.ID).in(ids));

            return entityManager.createQuery(query).getResultList();
        } catch (Exception e) {
            throw new DataBaseWorkException(e.getMessage(), e);
        }
    }

    public List<Album> findRandomExcept(Integer num, Set<Long> excludedIds) {
        try {
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Album> query = builder.createQuery(typeParameterClass);
            Expression<Double> random = builder.function("RAND", Double.class);

            Root<Album> root = query.from(typeParameterClass);
            query.select(root)
                    .where(builder
                            .not(root.get(Account_.ID).in(excludedIds)))
                    .orderBy(builder.asc(random));

            TypedQuery<Album> typedQuery = entityManager.createQuery(query);
            typedQuery.setMaxResults(num);

            return typedQuery.getResultList();
        } catch (Exception e) {
            throw new DataBaseWorkException(e.getMessage(), e);
        }
    }

    public Long getTotalCount() {
        try {
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Long> query = builder.createQuery(Long.class);
            Root<Album> root = query.from(typeParameterClass);

            query.select(builder.count(root));

            return entityManager.createQuery(query).getSingleResult();
        } catch (Exception e) {
            throw new DataBaseWorkException(e.getMessage(), e);
        }
    }
}
