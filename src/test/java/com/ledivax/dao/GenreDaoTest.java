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
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class GenreDaoTest {

    @Autowired
    private GenreDao genreDao;

    @Test
    public void testGenreDao() {
        String[] programGenres = new String[]{
                "POP", "RAP", "BLUES", "CHANSON", "CLASSICAL", "DISCO", "ELECTRONIC", "JAZZ", "MUSICAL", "RENAISSANCE",
                "ROCK", "SPIRITUAL"
        };
        String daoGenres[] = new String[programGenres.length];
        for (int i = 0; i < daoGenres.length; i++) {
            daoGenres[i] = String.valueOf(genreDao.findById(i + 1L).getGenreTitle());
        }

        assertArrayEquals(programGenres, daoGenres);
    }

    @Test
    public void testFindByTitle() {
        String searchParam = "POP";
        String genreTitle = String.valueOf(genreDao.findByTitle(searchParam).getGenreTitle());

        assertEquals(genreTitle, searchParam);
    }
}