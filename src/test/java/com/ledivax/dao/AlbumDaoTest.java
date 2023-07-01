package com.ledivax.dao;

import com.ledivax.exceptions.DataBaseWorkException;
import com.ledivax.models.AEntity;
import com.ledivax.models.Album;
import com.ledivax.util.ObjectCreator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class AlbumDaoTest {
    @Autowired
    private AlbumDao albumDao;
    @Autowired
    private AccountDao accountDao;
    @PersistenceContext
    protected EntityManager entityManager;

    @Test
    public void testFindById() {
        Album album = albumDao.findById(1L);

        assertEquals("?", album.getTitle());
        assertEquals(1L, album.getId().longValue());
    }

    @Test
    public void testFindByTitle() {
        List<Album> albums = albumDao.findByTitle("?", 0, 10);

        Album album = albums.stream().filter(a -> a.getId() == 1).findFirst().get();

        assertEquals("?", album.getTitle());
        assertEquals(7L, album.getCreator().getId().longValue());
        assertEquals(1L, album.getId().longValue());
    }

    @Test
    public void testFindAll() {
        List<Album> albums = albumDao.findAll(1, 10);

        assertNotNull(albums);
        assertTrue(albums.size() <= 10);
    }

    @Test
    public void testSave() {
        Album album = ObjectCreator.createAlbum("TestAlbum", accountDao.findById(1L));

        Long index = albumDao.save(album);
        album = albumDao.findById(index);

        assertEquals("TestAlbum", album.getTitle());
        assertEquals(1L, album.getCreator().getId().longValue());
    }

    @Test
    public void testUpdate() {
        Album album = albumDao.findById(2L);
        assertEquals("LAST ONE", album.getTitle());

        album.setTitle("LAST TWO");
        albumDao.update(album);

        album = albumDao.findById(2L);
        assertEquals("LAST TWO", album.getTitle());
    }

    @Test
    public void testDeleteById() {
        albumDao.deleteById(4L);

        DataBaseWorkException dataBaseWorkException =
                assertThrows(DataBaseWorkException.class, () -> {
                    albumDao.findById(4L);
                });

        assertEquals("No entity found for query", dataBaseWorkException.getMessage());
    }

    @Test
    public void testFindSavedFromByAccountId() {
        List<Album> albums = albumDao.findSavedFromByAccountId(2L);

        assertEquals(1L, albums.size());
        assertEquals("?", albums.get(0).getTitle());
    }

    @Test
    public void testFindCreatedFromAccountId() {
        List<Album> albums = albumDao.findCreatedFromAccountId(7L);

        assertEquals(1L, albums.size());
        assertEquals("?", albums.get(0).getTitle());
    }

    @Test
    public void testFindByIdWithCreator() {
        Album album = albumDao.findById(1L);
        entityManager.detach(album);

        assertNotNull(album.getCreator());
        assertEquals("?", album.getTitle());
        assertEquals(1L, album.getId().longValue());
    }

    @Test
    public void testFindByIds() {
        Set<Long> ids = Set.of(1L, 2L);

        List<Album> albums = albumDao.findByIds(ids);
        assertEquals(2, albums.size());
    }

    @Test
    public void testFindRandomExcept() {
        Set<Long> excludedIds = Set.of(1L);

        List<Album> albums = albumDao.findRandomExcept(2, excludedIds);
        assertEquals(2, albums.size());
        assertFalse(albums
                .stream()
                .map(AEntity::getId)
                .collect(Collectors.toList())

                .contains(1L));
    }

    @Test
    public void testGetTotalCount() {
        Long num = albumDao.getTotalCount();
        assertNotNull(num);
    }
}