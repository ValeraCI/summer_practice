package com.ledivax.controllers;

import com.ledivax.annotations.Loggable;
import com.ledivax.dto.album.AlbumCreateDto;
import com.ledivax.dto.album.AlbumInfoDto;
import com.ledivax.dto.album.AlbumUpdateDto;
import com.ledivax.models.AccountDetails;
import com.ledivax.services.api.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/albums")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService albumService;

    @Loggable
    @GetMapping("/{id}")
    public AlbumInfoDto findById(@PathVariable("id") Long id) {
        return albumService.findById(id);
    }

    @Loggable
    @GetMapping("/search/{title}")
    public List<AlbumInfoDto> findByTitle(@PathVariable String title,
                                          @RequestParam(name = "pageNumber", defaultValue = "1") String pageNumber,
                                          @RequestParam(name = "limit", defaultValue = "10") String limit) {
        return albumService.findByTitle(title, pageNumber, limit);
    }

    @Loggable
    @PostMapping
    public Long save(@Valid @RequestBody AlbumCreateDto createAlbumDto) {
        return albumService.save(createAlbumDto);
    }

    @Loggable
    @DeleteMapping("/{id}")
    @PreAuthorize("@albumSecurityService.hasAccess(#id, principal)")
    public void remove(@PathVariable Long id) {

        albumService.deleteById(id);
    }

    @Loggable
    @PatchMapping("/{id}")
    @PreAuthorize("@albumSecurityService.hasAccess(#id, principal)")
    public void updateData(@PathVariable("id") Long id,
                           @Valid @RequestBody AlbumUpdateDto albumUpdateDto) {

        albumService.updateData(id, albumUpdateDto);
    }

    @Loggable
    @PostMapping("/{albumId}/songs/{songId}")
    @PreAuthorize("@albumSecurityService.hasAccess(#albumId, #accountDetails)")
    public void addSongIn(@PathVariable("albumId") Long albumId,
                          @PathVariable("songId") Long songId,
                          @AuthenticationPrincipal AccountDetails accountDetails) {

        albumService.addSongIn(albumId, songId);
    }

    @Loggable
    @DeleteMapping("/{albumId}/songs/{songId}")
    @PreAuthorize("@albumSecurityService.hasAccess(#albumId, principal)")
    public void removeSongIn(@PathVariable("albumId") Long albumId,
                             @PathVariable("songId") Long songId) {

        albumService.removeSongIn(albumId, songId);
    }

    @Loggable
    @GetMapping("/savedAlbums/{id}")
    public List<AlbumInfoDto> findSavedAlbumsFromAccountId(@PathVariable("id") Long id) {
        return albumService.findSavedByAccountId(id);
    }

    @Loggable
    @GetMapping("/createdAlbums/{id}")
    public List<AlbumInfoDto> findCreatedAlbumsFromAccountId(@PathVariable("id") Long id) {
        return albumService.findByCreatorId(id);
    }

    @Loggable
    @GetMapping
    public List<AlbumInfoDto> findAll(
            @RequestParam(name = "pageNumber", defaultValue = "1") String pageNumber,
            @RequestParam(name = "limit", defaultValue = "10") String limit) {
        return albumService.findAll(pageNumber, limit);
    }

    @Loggable
    @GetMapping("/recommendations")
    public List<AlbumInfoDto> findRecommendedFor(@AuthenticationPrincipal AccountDetails accountDetails,
                                                 @RequestParam(name = "limit",
                                                         defaultValue = "10") String limit) {
        return albumService.findRecommendedFor(accountDetails, limit);
    }
}
