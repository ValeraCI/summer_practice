package com.ledivax.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ledivax.dto.AuthRequest;
import com.ledivax.dto.album.AlbumCreateDto;
import com.ledivax.dto.album.AlbumInfoDto;
import com.ledivax.dto.album.AlbumUpdateDto;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@ActiveProfiles("test")
public class AlbumControllerTest {
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
                .dispatchOptions(true).build();

        String json = objectMapper.writeValueAsString(
                new AuthRequest("cidikvalera@gmail.com", "1111"));

        MvcResult result = mockMvc.perform(get("/authenticate/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(MockMvcResultHandlers.print())
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
        MvcResult result = mockMvc.perform(get("/albums")
                        .header("Authorization", token))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        List<AlbumInfoDto> list =
                objectMapper.readValue(result.getResponse().getContentAsString(),
                        new TypeReference<List<AlbumInfoDto>>() {
                        });

        assertEquals(list.get(0).getTitle(), "?");
        assertEquals(list.get(0).getId(), 1);
    }

    @Test
    public void testFindById() throws Exception {
        MvcResult result = mockMvc.perform(get("/albums/{id}", 1)
                        .header("Authorization", token))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        AlbumInfoDto album =
                objectMapper.readValue(result.getResponse().getContentAsString(), AlbumInfoDto.class);

        assertEquals(album.getTitle(), "?");
        assertEquals(album.getId(), 1);
    }

    @Test
    public void testFindByTitle() throws Exception {
        MvcResult result = mockMvc.perform(get("/albums/search/{title}", "LAST ONE")
                        .header("Authorization", token))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        List<AlbumInfoDto> list =
                objectMapper.readValue(result.getResponse().getContentAsString(),
                        new TypeReference<List<AlbumInfoDto>>() {
                        });

        assertEquals(list.get(0).getTitle(), "LAST ONE");
        assertEquals(list.get(0).getId(), 2);
    }

    @Test
    public void testSave() throws Exception {
        MvcResult result = mockMvc.perform(post("/albums")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new AlbumCreateDto("TestAlbum", 1L))))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        result = mockMvc.perform(get("/albums/{id}", result.getResponse().getContentAsString())
                        .header("Authorization", token))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        AlbumInfoDto album =
                objectMapper.readValue(result.getResponse().getContentAsString(), AlbumInfoDto.class);

        assertEquals(album.getTitle(), "TestAlbum");
    }

    @Test
    public void testUpdateData() throws Exception {
        MvcResult result = mockMvc.perform(patch("/albums/{id}", 1)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new AlbumUpdateDto("TestAlbum 2"))))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());

        result = mockMvc.perform(patch("/albums/{id}", 1)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new AlbumUpdateDto("?"))))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    public void testSaveValidException() throws Exception {
        MvcResult result = mockMvc.perform(post("/albums")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new AlbumCreateDto("", null))))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        assertEquals(400, result.getResponse().getStatus());
    }

    @Test
    public void testRemoveById() throws Exception {
        mockMvc.perform(delete("/albums/{id}", 4)
                        .header("Authorization", token))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();


        MvcResult result = mockMvc.perform(get("/albums/{id}", 4)
                        .header("Authorization", token))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        assertEquals(400, result.getResponse().getStatus());
    }

    @Test
    public void testFindSavedAlbumsFromAccountId() throws Exception {
        MvcResult result = mockMvc.perform(get("/albums/savedAlbums/{id}", 1)
                        .header("Authorization", token))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        List<AlbumInfoDto> list =
                objectMapper.readValue(result.getResponse().getContentAsString(),
                        new TypeReference<List<AlbumInfoDto>>() {
                        });

        assertEquals(list.get(0).getTitle(), "?");
        assertEquals(list.get(0).getId(), 1);
    }

    @Test
    public void testFindCreatedAlbumsFromAccountId() throws Exception {
        MvcResult result = mockMvc.perform(get("/albums/createdAlbums/{id}", 7)
                        .header("Authorization", token))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        List<AlbumInfoDto> list =
                objectMapper.readValue(result.getResponse().getContentAsString(),
                        new TypeReference<List<AlbumInfoDto>>() {
                        });

        assertEquals(list.get(0).getTitle(), "?");
        assertEquals(list.get(0).getId(), 1);
    }

    @Test
    public void testAddRemoveSavedAlbum() throws Exception {
        mockMvc.perform(post("/albums/{albumId}/songs/{songId}", 1, 1)
                        .header("Authorization", token))
                .andDo(MockMvcResultHandlers.print());

        mockMvc.perform(delete("/albums/{albumId}/songs/{songId}", 1, 1)
                        .header("Authorization", token))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testAddSavedAlbumException() throws Exception {
        mockMvc.perform(post("/albums/{albumId}/{songId}", 1, 1)
                        .header("Authorization", token))
                .andDo(MockMvcResultHandlers.print());

        MvcResult result = mockMvc.perform(post("/albums/{albumId}/{songId}", 1, 1)
                        .header("Authorization", token))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        assertEquals(result.getResponse().getStatus(), 404);
    }

    @Test
    public void testRemoveSavedAlbumException() throws Exception {
        mockMvc.perform(delete("/albums/{albumId}/{songId}", 1, 1)
                        .header("Authorization", token))
                .andDo(MockMvcResultHandlers.print());

        MvcResult result = mockMvc.perform(delete("/albums/{albumId}/{songId}", 1, 1)
                        .header("Authorization", token))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        assertEquals(result.getResponse().getStatus(), 404);
    }

    @Test
    @GetMapping("/recommendations")
    public void testFindRecommendedFor() throws Exception {
        MvcResult result = mockMvc.perform(get("/albums/recommendations")
                        .header("Authorization", token))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        assertEquals(result.getResponse().getStatus(), 200);
    }
}
