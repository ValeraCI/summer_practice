package com.ledivax.util;

import com.ledivax.models.Account;
import com.ledivax.models.Album;
import com.ledivax.models.Genre;
import com.ledivax.models.Location;
import com.ledivax.models.LoginDetails;
import com.ledivax.models.Role;
import com.ledivax.models.RoleTitle;
import com.ledivax.models.Song;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

public class ObjectCreator {
    public static Account createAccount(Long id, String nickname, String email, String password) {
        Account account = new Account();
        account.setId(id);
        account.setNickname(nickname);
        account.setRegistrationDate(LocalDate.now());
        LoginDetails loginDetails = new LoginDetails(account, email, password);
        loginDetails.setAccount(account);
        account.setLoginDetails(loginDetails);
        account.setRole(new Role(3L, RoleTitle.ROLE_USER));
        account.setCreatedAlbums(new HashSet<>());
        account.setSavedAlbums(new HashSet<>());
        return account;
    }

    public static Account createOwnerAccount(Long id, String nickname, String email, String password) {
        Account account = new Account();
        account.setId(id);
        account.setNickname(nickname);
        account.setRegistrationDate(LocalDate.now());
        LoginDetails loginDetails = new LoginDetails(account, email, password);
        loginDetails.setAccount(account);
        account.setLoginDetails(loginDetails);
        account.setRole(new Role(1L, RoleTitle.ROLE_OWNER));
        account.setCreatedAlbums(new HashSet<>());
        account.setSavedAlbums(new HashSet<>());
        return account;
    }

    public static Album createAlbum(String title, Account account) {
        Album album = new Album();

        album.setTitle(title);
        album.setCreateDate(LocalDate.now());
        album.setCreator(account);
        return album;
    }

    public static Song createSong(String title, Genre genre, List<Account> authors) {
        Song song = new Song();

        song.setTitle(title);
        song.setGenre(genre);
        Location location = new Location(song, "./somePath");
        song.setLocation(location);
        song.setAuthors(authors);
        return song;
    }
}
