package com.ledivax.dto.account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RegistrationRequest {

    @NotEmpty(message = "The nickname should not be empty")
    @Size(min = 5, max = 50,
            message = "The length of the nickname should be in the range from 5 to 50")
    private String nickname;

    @NotEmpty(message = "The email should not be empty")
    @Email(message = "The string is not an email")
    private String email;

    @Size(min = 4, max = 256,
            message = "The length of the password should be in the range from 4 to 256")
    @NotEmpty(message = "The password should not be empty")
    private String password;
}
