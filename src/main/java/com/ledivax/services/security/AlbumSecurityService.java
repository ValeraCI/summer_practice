package com.ledivax.services.security;

import com.ledivax.dao.AlbumDao;
import com.ledivax.models.AccountDetails;
import com.ledivax.models.Album;
import com.ledivax.models.RoleTitle;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlbumSecurityService {
    private final AlbumDao albumDao;

    public boolean hasAccess(Long albumId, AccountDetails accountDetails) {
        Album album = albumDao.findByIdWithCreator(albumId);

        return album.getCreator().getId().equals(accountDetails.getId())
                ||
                accountDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .anyMatch(auth -> auth.equals(RoleTitle.ROLE_ADMINISTRATOR.toString())
                                || auth.equals(RoleTitle.ROLE_OWNER.toString()));
    }
}
