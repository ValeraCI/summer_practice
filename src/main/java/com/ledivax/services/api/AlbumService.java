package com.ledivax.services.api;

import com.ledivax.dto.album.AlbumCreateDto;
import com.ledivax.dto.album.AlbumInfoDto;
import com.ledivax.dto.album.AlbumUpdateDto;
import com.ledivax.models.AccountDetails;

import java.util.List;

public interface AlbumService {

    Long save(AlbumCreateDto albumDto);

    void updateData(Long id, AlbumUpdateDto albumDto);

    AlbumInfoDto findById(Long id);

    void deleteById(Long id);

    void addSongIn(Long albumId, Long songId);

    void removeSongIn(Long albumId, Long songId);

    List<AlbumInfoDto> findSavedByAccountId(Long accountId);

    List<AlbumInfoDto> findByCreatorId(Long accountId);

    List<AlbumInfoDto> findAll(String pageNumber, String limit);

    List<AlbumInfoDto> findByTitle(String title, String pageNumber, String limit);

    List<AlbumInfoDto> findRecommendedFor(AccountDetails accountDetails, String limit);
}
