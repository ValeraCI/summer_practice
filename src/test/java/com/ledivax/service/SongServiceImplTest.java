package com.ledivax.service;

import com.ledivax.dao.AccountDao;
import com.ledivax.dao.AlbumDao;
import com.ledivax.dao.GenreDao;
import com.ledivax.dao.SongDao;
import com.ledivax.dto.song.SongCreateDto;
import com.ledivax.dto.song.SongInfoDto;
import com.ledivax.models.Account;
import com.ledivax.models.Album;
import com.ledivax.models.Genre;
import com.ledivax.models.Song;
import com.ledivax.services.SongServiceImpl;
import com.ledivax.util.ObjectCreator;
import com.ledivax.util.mappers.SongMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SongServiceImplTest {

    @Mock
    private SongDao songDao;
    @Mock
    private AlbumDao albumDao;
    @Mock
    private AccountDao accountDao;
    @Mock
    private GenreDao genreDao;
    @Mock
    private SongMapper songMapper;

    @InjectMocks
    private SongServiceImpl songService;

    @Test
    public void testSave() {
        SongCreateDto songCreateDto = new SongCreateDto();
        songCreateDto.setAlbumId(1L);
        songCreateDto.setGenreId(1L);
        List<Account> authors = new ArrayList<>();
        Genre genre = new Genre();
        Song song = new Song();
        Album album = new Album();
        album.setTitle("Test");

        when(accountDao.findByIds(any())).thenReturn(authors);
        when(genreDao.findById(anyLong())).thenReturn(genre);
        when(albumDao.findById(anyLong())).thenReturn(album);
        when(songMapper.toEntity(eq(songCreateDto), eq(authors), eq(genre), anyString())).thenReturn(song);
        when(songDao.save(song)).thenReturn(1L);

        Long id = songService.save(songCreateDto);

        assertEquals(Long.valueOf(1), id);
        verify(accountDao).findByIds(any());
        verify(genreDao).findById(anyLong());
        verify(albumDao).findById(anyLong());
        verify(songMapper).toEntity(eq(songCreateDto), eq(authors), eq(genre), anyString());
        verify(songDao).save(song);
    }

    @Test
    public void testDeleteById() {
        Account account = ObjectCreator.createAccount(1L, "test", "test", "test");
        Song song = new Song();
        List<Account> authors = new ArrayList<>();
        authors.add(account);
        song.setAuthors(authors);

        songService.deleteById(1L);

        verify(songDao).deleteById(1L);
    }

    @Test
    public void testFindSongInfoDtoByAlbumId() {
        List<Song> songs = new ArrayList<>();
        List<SongInfoDto> songInfoDtoList = new ArrayList<>();

        when(songDao.findByAlbumId(anyLong())).thenReturn(songs);
        when(songMapper.toSongInfoDtoList(songs)).thenReturn(songInfoDtoList);

        List<SongInfoDto> result = songService.findByAlbumId(1L);

        assertEquals(songInfoDtoList, result);
        verify(songDao).findByAlbumId(1L);
        verify(songMapper).toSongInfoDtoList(songs);
    }

    @Test
    public void testFindSongInfoDtoById() {
        Song song = new Song();
        SongInfoDto songInfoDto = new SongInfoDto();

        when(songDao.findById(anyLong())).thenReturn(song);
        when(songMapper.toSongInfoDto(song)).thenReturn(songInfoDto);

        SongInfoDto result = songService.findById(1L);

        assertEquals(songInfoDto, result);
        verify(songDao).findById(1L);
        verify(songMapper).toSongInfoDto(song);
    }

    @Test
    public void testFindSongsInfoDtoByGenreTitle() {
        Genre genre = new Genre();
        List<Song> songs = new ArrayList<>();
        List<SongInfoDto> songInfoDtoList = new ArrayList<>();

        when(genreDao.findByTitle(anyString())).thenReturn(genre);
        when(songDao.findByGenre(eq(genre), anyInt(), anyInt())).thenReturn(songs);
        when(songMapper.toSongInfoDtoList(songs)).thenReturn(songInfoDtoList);

        List<SongInfoDto> result = songService.findByGenreTitle("BLUES", "1", "10");

        assertEquals(songInfoDtoList, result);
        verify(genreDao).findByTitle("BLUES");
        verify(songDao).findByGenre(genre, 0, 10);
        verify(songMapper).toSongInfoDtoList(songs);
    }

    @Test
    public void testFindSongsInfoDtoByTitle() {
        List<Song> songs = new ArrayList<>();
        List<SongInfoDto> songInfoDtoList = new ArrayList<>();

        when(songDao.findByTitle(anyString(), anyInt(), anyInt())).thenReturn(songs);
        when(songMapper.toSongInfoDtoList(songs)).thenReturn(songInfoDtoList);

        List<SongInfoDto> result = songService.findByTitle("Test", "1", "10");

        assertEquals(songInfoDtoList, result);
        verify(songDao).findByTitle("Test", 0, 10);
        verify(songMapper).toSongInfoDtoList(songs);
    }

    @Test
    public void testFindByParameterGenre() {
        Genre genre = new Genre();
        List<Song> songs = new ArrayList<>();
        List<SongInfoDto> songInfoDtoList = new ArrayList<>();

        when(genreDao.findByTitle(anyString())).thenReturn(genre);
        when(songDao.findByGenre(eq(genre), anyInt(), anyInt())).thenReturn(songs);
        when(songMapper.toSongInfoDtoList(songs)).thenReturn(songInfoDtoList);

        List<SongInfoDto> result =
                songService.findByParameter("BLUES", "BY_GENRE", "1", "10");

        assertEquals(songInfoDtoList, result);
        verify(genreDao).findByTitle("BLUES");
        verify(songDao).findByGenre(genre, 0, 10);
        verify(songMapper).toSongInfoDtoList(songs);
    }

    @Test
    public void testFindByParameterTitle() {
        List<Song> songs = new ArrayList<>();
        List<SongInfoDto> songInfoDtoList = new ArrayList<>();

        when(songDao.findByTitle(anyString(), anyInt(), anyInt())).thenReturn(songs);
        when(songMapper.toSongInfoDtoList(songs)).thenReturn(songInfoDtoList);

        List<SongInfoDto> result =
                songService.findByParameter("Test", "BY_TITLE", "1", "10");

        assertEquals(songInfoDtoList, result);
        verify(songDao).findByTitle("Test", 0, 10);
        verify(songMapper).toSongInfoDtoList(songs);
    }
}