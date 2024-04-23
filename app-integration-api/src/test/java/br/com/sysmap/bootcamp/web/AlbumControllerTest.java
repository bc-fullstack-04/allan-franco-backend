package br.com.sysmap.bootcamp.web;

import br.com.sysmap.bootcamp.domain.entities.Album;
import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.model.AlbumModel;
import br.com.sysmap.bootcamp.domain.service.AlbumService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class AlbumControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AlbumService albumService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Users user;

    private Album album;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
        user = Users.builder()
                .id(1L)
                .email("test")
                .password("123")
                .build();

        album = Album.builder()
                .id(1L)
                .name("Teste")
                .idSpotify("test123")
                .artistName("Test Artist")
                .imageUrl("Test Image")
                .value(BigDecimal.valueOf(50))
                .users(user)
                .build();
    }

    @Test
    @DisplayName("Test Buying Album")
    public void testBuyingAlbum() throws Exception {
        when(albumService.buyAlbumByUser(album)).thenReturn(album);

        mockMvc.perform(post("/albums/sale")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(album)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test Getting Album by User")
    public void testGetAlbumByUser() throws Exception {
        List<Album> albumList = Arrays.asList(album);

        when(albumService.getAlbumByUser()).thenReturn(albumList);

        mockMvc.perform(get("/albums/my-collection")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(albumList)));
    }

    @Test
    @DisplayName("Test Getting All Albums")
    public void testGetAlbums() throws Exception {
        List<AlbumModel> albumModel = Arrays.asList(new AlbumModel());

        when(albumService.getAllAlbums("Teste")).thenReturn(albumModel);

        mockMvc.perform(get("/albums/all?search=Teste")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(albumModel)));
    }

    @Test
    @DisplayName("Test Deleting Album By ID")
    public void testRemoveAlbumById() throws Exception {
        albumService.removeAlbumByID(1L);

        mockMvc.perform(delete("/albums/remove/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
