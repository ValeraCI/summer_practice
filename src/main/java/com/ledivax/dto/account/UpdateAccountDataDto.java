package com.ledivax.dto.account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAccountDataDto {
    @NotEmpty(message = "The nickname should not be empty")
    @Size(min = 5, max = 50,
            message = "The length of the nickname should be in the range from 5 to 50")
    private String nickname;
    @Size(min = 4, max = 256,
            message = "The length of the password should be in the range from 4 to 256")
    @NotEmpty(message = "The password should not be empty")
    private String password;
}
