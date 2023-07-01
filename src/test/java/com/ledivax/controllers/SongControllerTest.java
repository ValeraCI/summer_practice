package com.ledivax.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ledivax.dto.AuthRequest;
import com.ledivax.dto.song.SongCreateDto;
import com.ledivax.dto.song.SongInfoDto;
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
import org.springframework.web.context.WebApplicationContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@ActiveProfiles("test")
public class SongControllerTest {
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
    public void testFindById() throws Exception {
        MvcResult result = mockMvc.perform(get("/songs/{id}", 2)
                        .header("Authorization", token))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        SongInfoDto song =
                objectMapper.readValue(result.getResponse().getContentAsString(), SongInfoDto.class);

        assertEquals(song.getId(), 2);
        assertEquals(song.getTitle(), "TEASER");
    }

    @Test
    public void testSave() throws Exception {
        SongCreateDto songCreateDto = new SongCreateDto("TestSong", 1L,
                new HashSet<>(Set.of(6L, 7L, 8L)), "XXXTENTCION", 1L);

        MvcResult result = mockMvc.perform(post("/songs")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(songCreateDto)))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        result = mockMvc.perform(get("/songs/{id}", result.getResponse().getContentAsString())
                        .header("Authorization", token))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        SongInfoDto song =
                objectMapper.readValue(result.getResponse().getContentAsString(), SongInfoDto.class);

        assertEquals(song.getTitle(), "TestSong");
    }

    @Test
    public void testRemoveById() throws Exception {
        mockMvc.perform(delete("/songs/{id}", 5)
                        .header("Authorization", token))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();


        MvcResult result = mockMvc.perform(get("/songs/{id}", 5)
                        .header("Authorization", token))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        assertEquals(400, result.getResponse().getStatus());
    }

    @Test
    public void testFindByTitleParameter() throws Exception {
        MvcResult result = mockMvc.perform(get("/songs/search/{parameter}?findBy=BY_TITLE", "NUM")
                        .header("Authorization", token))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        List<SongInfoDto> list =
                objectMapper.readValue(result.getResponse().getContentAsString(),
                        new TypeReference<List<SongInfoDto>>() {
                        });

        assertEquals("NUMB", list.get(0).getTitle());
        assertEquals(8, list.get(0).getId());
    }

    @Test
    public void testFindByAlbumId() throws Exception {
        MvcResult result = mockMvc.perform(get("/songs/search/album/{albumId}", 2)
                        .header("Authorization", token))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        List<SongInfoDto> list =
                objectMapper.readValue(result.getResponse().getContentAsString(),
                        new TypeReference<List<SongInfoDto>>() {
                        });

        assertEquals("TEASER", list.get(0).getTitle());
        assertEquals(2, list.get(0).getId());
    }

    @Test
    public void testFindByGenreParameter() throws Exception {
        MvcResult result = mockMvc.perform(get("/songs/search/{parameter}?findBy=BY_GENRE", "RAP")
                        .header("Authorization", token))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        List<SongInfoDto> list =
                objectMapper.readValue(result.getResponse().getContentAsString(),
                        new TypeReference<List<SongInfoDto>>() {
                        });

        assertEquals("B", list.get(0).getTitle());
        assertEquals(4, list.get(0).getId());
    }
}