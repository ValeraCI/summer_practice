package com.ledivax.dto.account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpdateAccountRoleDto {

    @NotNull(message = "The roleId should not be null")
    private Long roleId;
}
