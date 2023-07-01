package com.ledivax.controllers;

import com.ledivax.annotations.Loggable;
import com.ledivax.dto.song.SongCreateDto;
import com.ledivax.dto.song.SongInfoDto;
import com.ledivax.models.AccountDetails;
import com.ledivax.services.api.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/songs")
@RequiredArgsConstructor
public class SongController {

    private final SongService songService;

    @Loggable
    @PostMapping
    public Long save(@Valid @RequestBody SongCreateDto songCreateDto) {
        return songService.save(songCreateDto);
    }

    @Loggable
    @DeleteMapping("/{id}")
    @PreAuthorize("@songSecurityService.hasAccess(#id, principal)")
    public void deleteById(@PathVariable("id") Long id) {
        songService.deleteById(id);
    }

    @Loggable
    @GetMapping("/{id}")
    public SongInfoDto findById(@PathVariable("id") Long id) {
        return songService.findById(id);
    }

    @Loggable
    @GetMapping(value = "search/{parameter}")
    public List<SongInfoDto> findByParameter(@PathVariable("parameter") String parameter,
                                             @RequestParam(name = "findBy",
                                                     defaultValue = "BY_TITLE") String findBy,
                                             @RequestParam(name = "pageNumber", defaultValue = "1") String pageNumber,
                                             @RequestParam(name = "limit", defaultValue = "10") String limit) {

        return songService.findByParameter(parameter, findBy, pageNumber, limit);
    }

    @Loggable
    @GetMapping("search/album/{albumId}")
    public List<SongInfoDto> findByAlbumId(@PathVariable("albumId") Long albumId) {
        return songService.findByAlbumId(albumId);
    }
}
