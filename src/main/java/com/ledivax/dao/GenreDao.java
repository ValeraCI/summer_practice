package com.ledivax.dao;

import com.ledivax.dao.abstractDao.AbstractDao;
import com.ledivax.exceptions.DataBaseWorkException;
import com.ledivax.models.Genre;
import com.ledivax.models.GenreTitle;
import com.ledivax.models.Genre_;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@Repository
public class GenreDao extends AbstractDao<Genre, Long> {
    public GenreDao() {
        super(Genre.class);
    }

    public Genre findByTitle(String genreTitle) {
        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

            CriteriaQuery<Genre> criteriaQuery = criteriaBuilder.createQuery(typeParameterClass);
            Root<Genre> root = criteriaQuery.from(typeParameterClass);

            criteriaQuery
                    .select(root)
                    .where(criteriaBuilder
                            .equal(root.get(Genre_.GENRE_TITLE), GenreTitle.valueOf(genreTitle))
                    );

            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (Exception e) {
            throw new DataBaseWorkException(e.getMessage(), e);
        }
    }
}
