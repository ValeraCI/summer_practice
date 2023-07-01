package com.ledivax.dao.abstractDao;

import com.ledivax.models.AEntity;

import java.io.Serializable;
import java.util.List;

public interface GenericDao<T extends AEntity, PK extends Serializable> {
    PK save(T entity);

    void update(T entity);

    void deleteById(PK id);

    List<T> findAll(Integer firstResult, Integer maxResults);

    T findById(PK id);
}
