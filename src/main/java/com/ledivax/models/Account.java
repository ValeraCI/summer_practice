package com.ledivax.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "accounts")

public class Account extends AEntity {
    @Column(name = "nickname")
    private String nickname;

    @Column(name = "registration_date")
    private LocalDate registrationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToOne(mappedBy = "account", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private LoginDetails loginDetails;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "authors",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "song_id")
    )
    private Set<Song> songsPerformed;

    @OneToMany(mappedBy = "creator", fetch = FetchType.LAZY)
    private Set<Album> createdAlbums;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "saved_albums",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "album_id")
    )
    private Set<Album> savedAlbums;

}