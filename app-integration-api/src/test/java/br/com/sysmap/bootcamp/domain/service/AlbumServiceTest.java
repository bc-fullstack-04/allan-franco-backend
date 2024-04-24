package br.com.sysmap.bootcamp.domain.service;

import br.com.sysmap.bootcamp.domain.entities.Album;
import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.model.AlbumModel;
import br.com.sysmap.bootcamp.domain.repository.AlbumRepository;
import br.com.sysmap.bootcamp.domain.service.integration.SpotifyApi;
import br.com.sysmap.bootcamp.dto.WalletDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.xml.crypto.Data;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class AlbumServiceTest {
    @Autowired
    private AlbumService albumService;

    @MockBean
    private AlbumRepository albumRepository;

    @MockBean
    private RabbitTemplate template;

    @MockBean
    private UsersService usersService;

    @MockBean
    private SpotifyApi spotifyApi;

    private Users user;

    private Album album;

    @BeforeEach
    public void setup() {
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
    @DisplayName("Test Buying Album By User")
    public  void testBuyAlbumByUser() throws Exception {
        // Simulate User Authentication
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(usersService.findByEmail(anyString())).thenReturn(user);

        when(albumRepository.save(any(Album.class))).thenReturn(album);

        // Set Mock SpotifyAPI Authentication
        List<AlbumModel> albumModels = Arrays.asList(new AlbumModel(), new AlbumModel());
        when(spotifyApi.getAlbums(anyString())).thenReturn(albumModels);

        Album result = albumService.buyAlbumByUser(album);

        assertEquals(album, result);

        verify(template).convertAndSend(anyString(), any(WalletDto.class));

        when(albumRepository.findByIdSpotifyAndUsers(anyString(), any(Users.class)))
                .thenThrow(new DataIntegrityViolationException("Simulated exception"));
        assertThrows(DataIntegrityViolationException.class, () -> {
            albumService.buyAlbumByUser(album);
        });


    }

    @Test
    @DisplayName("Test Getting Album By User")
    public  void testGetAlbumByUser() throws Exception {
        List<Album> albums = Arrays.asList(album);

        // Simulate User Authentication
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(usersService.findByEmail(anyString())).thenReturn(user);
        when(albumRepository.findAllByUsers(user)).thenReturn(albums);

        assertEquals(albums, albumService.getAlbumByUser());
    }

    @Test
    @DisplayName("Test Getting All Albums")
    public  void testGetAllAlbums() throws Exception {
        // Simulate User Authentication
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Set Mock SpotifyAPI Authentication
        List<AlbumModel> albumModels = Arrays.asList(new AlbumModel(), new AlbumModel());
        when(spotifyApi.getAlbums(anyString())).thenReturn(albumModels);

        assertEquals(albumModels, albumService.getAllAlbums("Teste"));
    }

    @Test
    @DisplayName("Test Removing Album By ID")
    public  void testRemoveAlbumByID() throws Exception {
        // Simulate User Authentication
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(usersService.findByEmail(authentication.getPrincipal().toString())).thenReturn(user);
        when(albumRepository.findByIdAndUsers(1L, user)).thenReturn(album);

        albumService.removeAlbumByID(1L);

        verify(albumRepository).delete(album);
    }
}
