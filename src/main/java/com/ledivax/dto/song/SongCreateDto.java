package com.ledivax.dto.song;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SongCreateDto {

    @NotEmpty(message = "The title should not be empty")
    @Size(min = 1, max = 50,
            message = "The title should be in the range from 1 to 50")
    private String title;

    @NotNull(message = "The genreId should not be empty")
    private Long genreId;
    @NotNull(message = "The authorsId should not be empty")
    private Set<Long> authorsId;
    @NotNull(message = "The albumCreator should not be empty")
    private String albumCreator;

    @NotNull(message = "The albumId should not be empty")
    private Long albumId;
}
