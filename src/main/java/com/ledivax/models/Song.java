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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "songs")
@NamedEntityGraphs({
        @NamedEntityGraph(name = "song-only-entity-graph"),
        @NamedEntityGraph(name = "song-authors-entity-graph",
                attributeNodes = {@NamedAttributeNode("authors")})
})

public class Song extends AEntity {
    @Column(name = "title")
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id")
    private Genre genre;

    @ManyToMany(mappedBy = "songsPerformed", fetch = FetchType.LAZY)
    private List<Account> authors;

    @OneToOne(mappedBy = "song", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private Location location;

    @ManyToMany(mappedBy = "songsIn")
    private Set<Album> containedIn;

}
