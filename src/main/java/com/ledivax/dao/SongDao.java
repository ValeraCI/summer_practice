package com.ledivax.dao;


import com.ledivax.dao.abstractDao.AbstractDao;
import com.ledivax.exceptions.DataBaseWorkException;
import com.ledivax.models.Album;
import com.ledivax.models.Album_;
import com.ledivax.models.Genre;
import com.ledivax.models.Song;
import com.ledivax.models.Song_;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityGraph;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Map;

@Repository
public class SongDao extends AbstractDao<Song, Long> {

    public SongDao() {
        super(Song.class);
    }

    @Override
    public Song findById(Long id) {
        try {
            EntityGraph graph = entityManager.getEntityGraph("song-authors-entity-graph");
            Map<String, Object> properties = Map.of("javax.persistence.fetchgraph", graph);
            Song song = entityManager.find(Song.class, id, properties);

            if (song == null) {
                throw new DataBaseWorkException("No entity found for query");
            }

            return song;
        } catch (Exception e) {
            throw new DataBaseWorkException(e.getMessage(), e);
        }
    }

    public List<Song> findByGenre(Genre genre, Integer firstResult, Integer maxResults) {
        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

            CriteriaQuery<Song> criteriaQuery = criteriaBuilder.createQuery(typeParameterClass);
            Root<Song> root = criteriaQuery.from(typeParameterClass);
            root.fetch(Song_.AUTHORS);
            root.fetch(Song_.GENRE);

            criteriaQuery
                    .select(root)
                    .where(criteriaBuilder
                            .equal(root.get(Song_.GENRE), genre)
                    )
                    .orderBy(criteriaBuilder.asc(root.get(Song_.TITLE)));

            TypedQuery<Song> typedQuery = entityManager.createQuery(criteriaQuery);
            typedQuery.setFirstResult(firstResult);
            typedQuery.setMaxResults(maxResults);

            return typedQuery.getResultList();
        } catch (Exception e) {
            throw new DataBaseWorkException(e.getMessage(), e);
        }
    }

    public List<Song> findByTitle(String title, Integer firstResult, Integer maxResults) {
        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

            CriteriaQuery<Song> criteriaQuery = criteriaBuilder.createQuery(typeParameterClass);
            Root<Song> root = criteriaQuery.from(typeParameterClass);
            root.fetch(Song_.AUTHORS, JoinType.LEFT);
            root.fetch(Song_.GENRE);

            criteriaQuery
                    .select(root)
                    .where(criteriaBuilder.like(root.get(Song_.TITLE), title + "%"))
                    .orderBy(criteriaBuilder.asc(root.get(Song_.ID)));

            TypedQuery<Song> typedQuery = entityManager.createQuery(criteriaQuery);
            typedQuery.setFirstResult(firstResult);
            typedQuery.setMaxResults(maxResults);

            return typedQuery.getResultList();
        } catch (Exception e) {
            throw new DataBaseWorkException(e.getMessage(), e);
        }
    }

    public List<Song> findByAlbumId(Long albumId) {
        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

            CriteriaQuery<Song> query = criteriaBuilder.createQuery(Song.class);

            Root<Song> root = query.from(typeParameterClass);
            Join<Album, Song> join = root.join(Song_.CONTAINED_IN, JoinType.LEFT);

            query.select(root)
                    .where(criteriaBuilder.equal(join.get(Album_.ID), albumId));

            return entityManager.createQuery(query).getResultList();
        } catch (Exception e) {
            throw new DataBaseWorkException(e.getMessage(), e);
        }
    }

    public Long getTotalCount() {
        try {
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Long> query = builder.createQuery(Long.class);
            Root<Song> root = query.from(typeParameterClass);

            query.select(builder.count(root));

            return entityManager.createQuery(query).getSingleResult();
        } catch (Exception e) {
            throw new DataBaseWorkException(e.getMessage(), e);
        }
    }
}
