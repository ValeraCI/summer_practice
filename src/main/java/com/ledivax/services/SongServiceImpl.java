package com.ledivax.services;

import com.ledivax.annotations.Loggable;
import com.ledivax.dao.AccountDao;
import com.ledivax.dao.AlbumDao;
import com.ledivax.dao.GenreDao;
import com.ledivax.dao.SongDao;
import com.ledivax.dto.song.SongCreateDto;
import com.ledivax.dto.song.SongInfoDto;
import com.ledivax.models.Account;
import com.ledivax.models.Genre;
import com.ledivax.models.Song;
import com.ledivax.services.api.SongService;
import com.ledivax.util.Convertor;
import com.ledivax.util.Paginator;
import com.ledivax.util.SongFindParameter;
import com.ledivax.util.mappers.SongMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SongServiceImpl implements SongService {
    private final SongDao songDao;
    private final AlbumDao albumDao;
    private final AccountDao accountDao;
    private final GenreDao genreDao;
    private final SongMapper songMapper;


    @Override
    @Loggable
    public Long save(SongCreateDto songCreateDto) {
        List<Account> authors = accountDao.findByIds(songCreateDto.getAuthorsId());
        Genre genre = genreDao.findById(songCreateDto.getGenreId());

        Song song = songMapper.toEntity(songCreateDto, authors, genre, createLocation(songCreateDto));
        return songDao.save(song);
    }

    @Override
    @Loggable
    public void deleteById(Long id) {
        songDao.deleteById(id);
    }

    @Override
    @Loggable
    public List<SongInfoDto> findByAlbumId(Long albumId) {
        return songMapper.toSongInfoDtoList(
                songDao.findByAlbumId(albumId)
        );
    }

    @Override
    @Loggable
    public SongInfoDto findById(Long id) {
        return songMapper.toSongInfoDto(songDao.findById(id));
    }

    @Override
    @Loggable
    public List<SongInfoDto> findByGenreTitle(String genreTitle, String pageNumber, String limit) {
        Integer pageNumberInteger = Convertor.stringToInteger(pageNumber);
        Integer limitInteger = Convertor.stringToInteger(limit);

        limitInteger = Paginator.limitingMinimumValueToOne(limitInteger);


        Genre genre = genreDao.findByTitle(genreTitle);

        Long totalCount = songDao.getTotalCount();
        Integer firstResult = Paginator.getFirstElement(pageNumberInteger, totalCount, limitInteger);

        return songMapper.toSongInfoDtoList(
                songDao.findByGenre(genre, Math.toIntExact(firstResult), limitInteger)
        );
    }

    @Override
    @Loggable
    public List<SongInfoDto> findByTitle(String title, String pageNumber, String limit) {
        Integer pageNumberInteger = Convertor.stringToInteger(pageNumber);
        Integer limitInteger = Convertor.stringToInteger(limit);

        limitInteger = Paginator.limitingMinimumValueToOne(limitInteger);

        Long totalCount = songDao.getTotalCount();
        Integer firstResult = Paginator.getFirstElement(pageNumberInteger, totalCount, limitInteger);

        return songMapper.toSongInfoDtoList(
                songDao.findByTitle(title, Math.toIntExact(firstResult), limitInteger)
        );
    }

    @Override
    @Loggable
    public List<SongInfoDto> findByParameter(String parameter, String findBy, String pageNumber, String limit) {
        List<SongInfoDto> resultList = null;
        SongFindParameter songFindParameter = Convertor.stringToSongFindParameter(findBy);

        switch (songFindParameter) {
            case BY_GENRE:
                resultList = findByGenreTitle(parameter, pageNumber, limit);
                break;
            case BY_TITLE:
                resultList = findByTitle(parameter, pageNumber, limit);
                break;
        }
        return resultList;
    }

    private String createLocation(SongCreateDto songCreateDto) {
        return ".\\music\\" + songCreateDto.getAlbumCreator() +
                "\\" +
                albumDao.findById(songCreateDto.getAlbumId()).getTitle() +
                "\\" +
                songCreateDto.getTitle() +
                ".mp3";
    }
}
