package com.ledivax.services.api;

import com.ledivax.dto.song.SongCreateDto;
import com.ledivax.dto.song.SongInfoDto;
import com.ledivax.models.AccountDetails;

import java.util.List;

public interface SongService {

    Long save(SongCreateDto songCreateDto);

    void deleteById(Long id);

    List<SongInfoDto> findByAlbumId(Long albumId);

    SongInfoDto findById(Long id);

    List<SongInfoDto> findByGenreTitle(String genreTitle, String pageNumber, String limit);

    List<SongInfoDto> findByTitle(String title, String pageNumber, String limit);

    List<SongInfoDto> findByParameter(String parameter, String findBy, String pageNumber, String limit);
}
