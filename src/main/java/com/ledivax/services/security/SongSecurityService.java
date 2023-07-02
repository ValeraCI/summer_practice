package com.ledivax.services.security;

import com.ledivax.annotations.Loggable;
import com.ledivax.dao.SongDao;
import com.ledivax.models.AEntity;
import com.ledivax.models.AccountDetails;
import com.ledivax.models.RoleTitle;
import com.ledivax.models.Song;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SongSecurityService {
    private final SongDao songDao;

    @Loggable
    public boolean hasAccess(Long songId, AccountDetails accountDetails) {
        Song song = songDao.findById(songId);

        return song.getAuthors()
                .stream()
                .map(AEntity::getId)
                .anyMatch(id -> id.equals(accountDetails.getId()))
                ||
                accountDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .anyMatch(auth -> auth.equals(RoleTitle.ROLE_ADMINISTRATOR.toString())
                                || auth.equals(RoleTitle.ROLE_OWNER.toString()));
    }
}
