package com.ledivax.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@NamedEntityGraphs({
        @NamedEntityGraph(name = "album-only-entity-graph"),
        @NamedEntityGraph(name = "album-songsIn-entity-graph",
                attributeNodes = {@NamedAttributeNode("songsIn")})
})
@Table(name = "albums")
public class Album extends AEntity {
    @Column(name = "title")
    private String title;

    @Column(name = "create_date")
    private LocalDate createDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private Account creator;

    @ManyToMany(mappedBy = "savedAlbums")
    private Set<Account> savedFrom;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "songs_in_albums",
            joinColumns = @JoinColumn(name = "album_id"),
            inverseJoinColumns = @JoinColumn(name = "song_id")
    )
    private Set<Song> songsIn;
}
