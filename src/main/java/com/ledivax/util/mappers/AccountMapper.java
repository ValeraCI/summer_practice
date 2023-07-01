package com.ledivax.util.mappers;

import com.ledivax.dto.account.AccountMainDataDto;
import com.ledivax.dto.account.RegistrationRequest;
import com.ledivax.models.Account;
import com.ledivax.models.LoginDetails;
import com.ledivax.models.Role;
import com.ledivax.models.RoleTitle;
import lombok.RequiredArgsConstructor;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AccountMapper {
    private final ModelMapper mapper;

    @PostConstruct
    public void setupMapper() {
        mapper.createTypeMap(RegistrationRequest.class, Account.class)
                .addMappings(m -> m.skip(Account::setLoginDetails))
                .addMappings(m -> m.skip(Account::setRegistrationDate))
                .addMappings(m -> m.skip(Account::setRole))
                .setPostConverter(
                        loginRequestToAccountConverter()
                );
    }

    public Converter<RegistrationRequest, Account> loginRequestToAccountConverter() {
        return context -> {
            RegistrationRequest source = context.getSource();
            Account destination = context.getDestination();
            mapLoginRequestSpecificFields(source, destination);
            return context.getDestination();
        };
    }

    public void mapLoginRequestSpecificFields(RegistrationRequest source, Account destination) {
        destination.setRegistrationDate(LocalDate.now());
        LoginDetails loginDetails =
                new LoginDetails(destination, source.getEmail(), source.getPassword());
        destination.setLoginDetails(loginDetails);
        destination.setRole(new Role(3L, RoleTitle.ROLE_USER));
    }

    public Account toEntity(RegistrationRequest dto) {
        return Objects.isNull(dto) ? null : mapper.map(dto, Account.class);
    }

    public AccountMainDataDto toAccountMainDataDto(Account entity) {
        return Objects.isNull(entity) ? null : mapper.map(entity, AccountMainDataDto.class);
    }

    public List<AccountMainDataDto> toAccountMainDataDtoList(List<Account> accounts) {
        return accounts
                .stream()
                .map(account -> Objects.isNull(account) ? null : mapper.map(account, AccountMainDataDto.class))
                .collect(Collectors.toList());
    }
}