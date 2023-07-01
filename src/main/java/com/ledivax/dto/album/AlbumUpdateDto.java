package com.ledivax.dto.album;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AlbumUpdateDto {
    @NotEmpty(message = "The title should not be empty")
    @Size(min = 1, max = 50,
            message = "The title should be in the range from 1 to 50")
    private String title;
}
