package com.ledivax.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "genres")
public class Genre extends AEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "title")
    private GenreTitle genreTitle;
}
