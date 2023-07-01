package com.ledivax.service;

import com.ledivax.dao.AccountDao;
import com.ledivax.dao.AlbumDao;
import com.ledivax.dao.SongDao;
import com.ledivax.dto.album.AlbumCreateDto;
import com.ledivax.dto.album.AlbumInfoDto;
import com.ledivax.dto.album.AlbumUpdateDto;
import com.ledivax.exceptions.DataChangesException;
import com.ledivax.models.Account;
import com.ledivax.models.AccountDetails;
import com.ledivax.models.Album;
import com.ledivax.models.Song;
import com.ledivax.services.AlbumServiceImpl;
import com.ledivax.util.ObjectCreator;
import com.ledivax.util.mappers.AlbumMapper;
import org.apache.commons.math3.stat.inference.MannWhitneyUTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AlbumServiceImplTest {
    private final AlbumDao albumDao;
    private final AccountDao accountDao;
    private final SongDao songDao;
    private final AlbumMapper albumMapper;
    private final MannWhitneyUTest mannWhitneyUTest;

    private final AlbumServiceImpl albumService;

    public AlbumServiceImplTest() {
        albumDao = Mockito.mock(AlbumDao.class);
        accountDao = Mockito.mock(AccountDao.class);
        songDao = Mockito.mock(SongDao.class);
        albumMapper = Mockito.mock(AlbumMapper.class);
        mannWhitneyUTest = Mockito.mock(MannWhitneyUTest.class);

        albumService = new AlbumServiceImpl(albumDao, accountDao, songDao, albumMapper, mannWhitneyUTest,
                500L, 0.1);
    }


    @Test
    public void testSaveAlbum() {
        AlbumCreateDto albumDto = new AlbumCreateDto();
        albumDto.setCreatorId(1L);
        Account account = new Account();
        Album album = new Album();

        when(accountDao.findById(anyLong())).thenReturn(account);
        when(albumMapper.toEntity(albumDto, account)).thenReturn(album);
        when(albumDao.save(album)).thenReturn(1L);

        Long albumId = albumService.save(albumDto);

        assertEquals(Long.valueOf(1), albumId);
        verify(albumDao).save(album);
        verify(accountDao).findById(anyLong());
        verify(albumMapper).toEntity(albumDto, account);
    }

    @Test
    public void testFindAlbumInfoDtoById() {
        Album album = new Album();
        AlbumInfoDto albumInfoDto = new AlbumInfoDto();

        when(albumDao.findById(anyLong()))
                .thenReturn(album);
        when(albumMapper.toAlbumInfoDto(album)).thenReturn(albumInfoDto);

        AlbumInfoDto result = albumService.findById(1L);

        assertEquals(albumInfoDto, result);
        verify(albumDao).findById(1L);
        verify(albumMapper).toAlbumInfoDto(album);
    }

    @Test
    public void testDeleteById() {
        Account account = ObjectCreator.createAccount(1L, "test", "test", "test");
        Album album = new Album();
        album.setCreator(account);

        albumService.deleteById(1L);

        verify(albumDao).deleteById(1L);
    }

    @Test
    public void testUpdateData() {
        AlbumUpdateDto albumUpdateDto = new AlbumUpdateDto("Test 1");

        Album album = new Album();
        album.setTitle("Test");

        Account account = ObjectCreator.createOwnerAccount(1L, "test", "test", "test");
        album.setCreator(account);

        when(albumDao.findByIdWithCreator(anyLong())).thenReturn(album);

        albumService.updateData(1L, albumUpdateDto);

        verify(albumDao).update(album);
    }

    @Test
    public void testAddSongIn() {
        Account account = ObjectCreator.createAccount(1L, "test", "test", "test");
        Song song = new Song();
        Album album = new Album();
        album.setSongsIn(new HashSet<>());
        album.setCreator(account);

        when(songDao.findById(anyLong())).thenReturn(song);
        when(albumDao.findByIdWithCreator(anyLong())).thenReturn(album);

        albumService.addSongIn(1L, 1L);

        assertEquals(album.getSongsIn().size(), 1);
        verify(albumDao).findByIdWithCreator(1L);
        verify(songDao).findById(1L);
    }

    @Test
    public void testAddSongInException() {
        Account account = ObjectCreator.createAccount(1L, "test", "test", "test");

        Song song = new Song();
        Album album = new Album();
        album.setCreator(account);

        album.setSongsIn(new HashSet<>());
        album.getSongsIn().add(song);

        when(songDao.findById(anyLong())).thenReturn(song);
        when(albumDao.findByIdWithCreator(anyLong())).thenReturn(album);

        DataChangesException dataChangesException = assertThrows(DataChangesException.class, () ->
            albumService.addSongIn(1L, 1L)
        );

        assertEquals("Album already contains such a song", dataChangesException.getMessage());
        verify(songDao).findById(1L);
        verify(albumDao).findByIdWithCreator(1L);
    }

    @Test
    public void testRemoveSavedAlbum() {
        Account account = ObjectCreator.createAccount(1L, "test", "test", "test");

        Song song = new Song();

        Album album = new Album();
        album.setCreator(account);
        album.setSongsIn(new HashSet<>());
        album.getSongsIn().add(song);

        when(songDao.findById(anyLong())).thenReturn(song);
        when(albumDao.findByIdWithCreator(anyLong())).thenReturn(album);

        albumService.removeSongIn(1L, 1L);

        assertEquals(album.getSongsIn().size(), 0);
        verify(songDao).findById(1L);
        verify(albumDao).findByIdWithCreator(1L);
    }

    @Test
    public void testRemoveSavedAlbumException() {
        Account account = ObjectCreator.createAccount(1L, "test", "test", "test");

        Song song = new Song();
        Album album = new Album();
        album.setSongsIn(new HashSet<>());
        album.setCreator(account);

        when(songDao.findById(anyLong())).thenReturn(song);
        when(albumDao.findByIdWithCreator(anyLong())).thenReturn(album);

        DataChangesException dataChangesException = assertThrows(DataChangesException.class, () ->
            albumService.removeSongIn(1L, 1L)
        );

        assertEquals("Album does not contain such a song", dataChangesException.getMessage());
        verify(songDao).findById(1L);
        verify(albumDao).findByIdWithCreator(1L);
    }

    @Test
    public void testFindSavedAlbumsInfoDtoFromAccountId() {
        List<Album> albums = new ArrayList<>();
        List<AlbumInfoDto> albumInfoDtoList = new ArrayList<>();

        when(albumDao.findSavedFromByAccountId(anyLong())).thenReturn(albums);
        when(albumMapper.toAlbumInfoDtoList(albums)).thenReturn(albumInfoDtoList);

        List<AlbumInfoDto> answer = albumService.findSavedByAccountId(1L);

        assertEquals(0, answer.size());
        assertEquals(albumInfoDtoList, answer);
        verify(albumDao).findSavedFromByAccountId(1L);
        verify(albumMapper).toAlbumInfoDtoList(albums);
    }

    @Test
    public void testFindCreatedAlbumInfoDtoFromAccountId() {
        List<Album> albums = new ArrayList<>();
        List<AlbumInfoDto> albumInfoDtoList = new ArrayList<>();

        when(albumDao.findCreatedFromAccountId(anyLong())).thenReturn(albums);
        when(albumMapper.toAlbumInfoDtoList(albums)).thenReturn(albumInfoDtoList);

        List<AlbumInfoDto> answer = albumService.findByCreatorId(1L);

        assertEquals(0, answer.size());
        assertEquals(albumInfoDtoList, answer);
        verify(albumDao).findCreatedFromAccountId(1L);
        verify(albumMapper).toAlbumInfoDtoList(albums);
    }

    @Test
    public void testFindAllAlbumInfoDto() {
        List<Album> albums = new ArrayList<>();
        List<AlbumInfoDto> albumInfoDtoList = new ArrayList<>();

        when(albumDao.getTotalCount()).thenReturn(4L);
        when(albumDao.findAll(0, 10)).thenReturn(albums);
        when(albumMapper.toAlbumInfoDtoList(albums)).thenReturn(albumInfoDtoList);

        List<AlbumInfoDto> answer = albumService.findAll("1", "10");

        assertEquals(0, answer.size());
        assertEquals(albumInfoDtoList, answer);
        verify(albumDao).findAll(0, 10);
        verify(albumMapper).toAlbumInfoDtoList(albums);
    }

    @Test
    public void testFindAlbumInfoDtoByTitle() {
        List<Album> albums = new ArrayList<>();
        List<AlbumInfoDto> albumInfoDtoList = new ArrayList<>();

        when(albumDao.findByTitle(anyString(), anyInt(), anyInt())).thenReturn(albums);
        when(albumMapper.toAlbumInfoDtoList(albums)).thenReturn(albumInfoDtoList);

        List<AlbumInfoDto> answer = albumService.findByTitle("song", "1", "10");

        assertEquals(0, answer.size());
        assertEquals(albumInfoDtoList, answer);
        verify(albumDao).findByTitle("song", 0, 10);
        verify(albumMapper).toAlbumInfoDtoList(albums);
    }

    @Test
    public void testFindRecommendedForLessThanThree() {
        Account account = ObjectCreator.createAccount(1L, "test", "test", "test");
        List<Album> albums = new ArrayList<>();
        List<AlbumInfoDto> result = new ArrayList<>();

        when(accountDao.findWithSavedAlbumsById(anyLong())).thenReturn(account);
        when(albumDao.findRandomExcept(anyInt(), any())).thenReturn(albums);
        when(albumMapper.toAlbumInfoDtoList(any())).thenReturn(result);

        List<AlbumInfoDto> answer = albumService.findRecommendedFor(new AccountDetails(account), "10");

        assertEquals(result, answer);
        verify(accountDao).findWithSavedAlbumsById(1L);
        verify(albumDao).findRandomExcept(eq(10), any());
        verify(albumMapper).toAlbumInfoDtoList(any());
    }

    @Test
    public void testFindRecommendedForMoreThanThree() {
        long numberOfUsersAtTime = 500L;

        Account account = ObjectCreator.createAccount(1L, "test", "test", "test");
        Album album;
        for (long i = 1; i < 8; i++) {
            album = new Album();
            album.setId(i);
            account.getSavedAlbums().add(album);
        }

        List<AlbumInfoDto> result = new ArrayList<>();

        List<Account> accounts = new ArrayList<>();
        Account newAccount = ObjectCreator.createAccount(1L, "test", "test", "test");

        for (long i = 2; i < 10; i++) {
            album = new Album();
            album.setId(i);
            newAccount.getSavedAlbums().add(album);
        }

        accounts.add(newAccount);

        when(accountDao.findWithSavedAlbumsById(anyLong())).thenReturn(account);
        when(accountDao.findWithSavedAlbumsByIdInBetween(1L, numberOfUsersAtTime + 1)).thenReturn(accounts);
        when(mannWhitneyUTest.mannWhitneyUTest(any(), any())).thenReturn(0.1);
        when(accountDao.findWithSavedAlbumsByIdInBetween(numberOfUsersAtTime + 1, numberOfUsersAtTime * 2 + 1))
                .thenReturn(new ArrayList<>());
        when(albumMapper.toAlbumInfoDtoList(any())).thenReturn(result);

        List<AlbumInfoDto> answer = albumService.findRecommendedFor(new AccountDetails(account), "10");

        assertEquals(result, answer);

        verify(accountDao).findWithSavedAlbumsById(1L);
        verify(accountDao).findWithSavedAlbumsByIdInBetween(1L, numberOfUsersAtTime + 1);
        verify(mannWhitneyUTest).mannWhitneyUTest(any(), any());
        verify(accountDao).findWithSavedAlbumsByIdInBetween(numberOfUsersAtTime + 1, numberOfUsersAtTime * 2 + 1);
        verify(albumMapper).toAlbumInfoDtoList(any());
    }
}