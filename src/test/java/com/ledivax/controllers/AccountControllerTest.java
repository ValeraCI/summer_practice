package com.ledivax.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ledivax.dto.AuthRequest;
import com.ledivax.dto.account.AccountMainDataDto;
import com.ledivax.dto.account.RegistrationRequest;
import com.ledivax.dto.account.UpdateAccountDataDto;
import com.ledivax.dto.account.UpdateAccountRoleDto;
import com.ledivax.security.filters.JwtFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@SpringBootTest
@ActiveProfiles("test")
public class AccountControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private String token;

    @BeforeEach
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .addFilter(jwtFilter)
                .alwaysDo(print())
                .dispatchOptions(true).build();


        String json = objectMapper.writeValueAsString(
                new AuthRequest("cidikvalera@gmail.com", "1111"));

        MvcResult result = mockMvc.perform(get("/authenticate/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andReturn();

        token = "Bearer " + result.getResponse()
                .getContentAsString()
                .split(":")[2]
                .replace('\"', ' ')
                .replace('}', ' ')
                .trim();
    }

    @Test
    public void testFindAll() throws Exception {
        MvcResult result = mockMvc.perform(get("/accounts")
                        .header("Authorization", token))
                .andReturn();

        List<AccountMainDataDto> list =
                objectMapper.readValue(result.getResponse().getContentAsString(),
                        new TypeReference<List<AccountMainDataDto>>() {
                        });

        assertEquals(list.get(0).getNickname(), "Valerix");
        assertEquals(list.get(0).getId().longValue(), 1);
    }

    @Test
    public void testFindById() throws Exception {
        MvcResult result = mockMvc.perform(get("/accounts/{id}", 1)
                        .header("Authorization", token))
                .andReturn();

        AccountMainDataDto account =
                objectMapper.readValue(result.getResponse().getContentAsString(), AccountMainDataDto.class);

        assertEquals(account.getNickname(), "Valerix");
        assertEquals(account.getId().longValue(), 1);
    }

    @Test
    public void testRemoveById() throws Exception {
        MvcResult result = mockMvc.perform(post("/accounts/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new RegistrationRequest("nickname", "GuineaPig@gmail.com",
                                        "password"))))
                .andReturn();

        Long id = Long.valueOf(result.getResponse().getContentAsString());


        mockMvc.perform(delete("/accounts/{id}", id)
                        .header("Authorization", token))
                .andReturn();


        result = mockMvc.perform(get("/accounts/{id}", id)
                        .header("Authorization", token))
                .andReturn();

        assertEquals(400, result.getResponse().getStatus());
    }

    @Test
    public void testUpdateData() throws Exception {
        UpdateAccountDataDto accountDto = new UpdateAccountDataDto("TestNick", "TestPass");

        mockMvc.perform(patch("/accounts/{id}", 9)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountDto)));

        MvcResult result = mockMvc.perform(get("/accounts/{id}", 9)
                        .header("Authorization", token))
                .andReturn();

        AccountMainDataDto account =
                objectMapper.readValue(result.getResponse().getContentAsString(), AccountMainDataDto.class);

        assertEquals(account.getNickname(), "TestNick");
    }

    @Test
    public void testAddRemoveSavedAlbum() throws Exception {
        mockMvc.perform(post("/accounts/{accountId}/albums/{albumId}", 10, 1)
                .header("Authorization", token));

        MvcResult result = mockMvc.perform(delete("/accounts/{accountId}/albums/{albumId}", 10, 1)
                        .header("Authorization", token))
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    public void testAddSavedAlbumException() throws Exception {
        mockMvc.perform(post("/accounts/{accountId}/albums/{albumId}", 10, 1)
                .header("Authorization", token));

        MvcResult result = mockMvc.perform(post("/accounts/{accountId}/albums/{albumId}", 10, 1)
                        .header("Authorization", token))
                .andReturn();

        assertEquals(400, result.getResponse().getStatus());
    }

    @Test
    public void testRemoveSavedAlbumException() throws Exception {
        mockMvc.perform(delete("/accounts/{accountId}/albums/{albumId}", 10, 2)
                .header("Authorization", token));

        MvcResult result = mockMvc.perform(delete("/accounts/{accountId}/albums/{albumId}", 10, 2)
                        .header("Authorization", token))
                .andReturn();

        assertEquals(400, result.getResponse().getStatus());
    }

    @Test
    public void testSave() throws Exception {
        MvcResult result = mockMvc.perform(post("/accounts/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new RegistrationRequest("nickname", "cidikvalera13231232@gmail.com",
                                        "password"))))
                .andReturn();

        result = mockMvc.perform(get("/accounts/{id}", result.getResponse().getContentAsString())
                        .header("Authorization", token))
                .andReturn();

        AccountMainDataDto account =
                objectMapper.readValue(result.getResponse().getContentAsString(), AccountMainDataDto.class);

        assertEquals(account.getNickname(), "nickname");
    }

    @Test
    public void testSaveValidException() throws Exception {
        MvcResult result = mockMvc.perform(post("/accounts/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new RegistrationRequest("", "cidikvalera@gmail.com", ""))))
                .andReturn();

        assertEquals(400, result.getResponse().getStatus());
    }

    @Test
    public void testUpdateRole() throws Exception {
        UpdateAccountRoleDto updateAccountRoleDto = new UpdateAccountRoleDto(2L);

        MvcResult result =
                mockMvc.perform(patch("/accounts/role/{id}", 8)
                                .header("Authorization", token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateAccountRoleDto)))
                        .andReturn();

        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    public void testUpdateOwnerRole() throws Exception {
        UpdateAccountRoleDto updateAccountRoleDto = new UpdateAccountRoleDto(2L);

        MvcResult result =
                mockMvc.perform(patch("/accounts/role/{id}", 1)
                                .header("Authorization", token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateAccountRoleDto)))
                        .andReturn();

        assertEquals(400, result.getResponse().getStatus());
    }
}