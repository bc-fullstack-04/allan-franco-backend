package br.com.sysmap.bootcamp.web;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.entities.Wallet;
import br.com.sysmap.bootcamp.domain.service.UsersService;
import br.com.sysmap.bootcamp.dto.AuthDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import static org.hamcrest.Matchers.is;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class UsersControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsersService usersService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private ObjectMapper objectMapper;
    private Users user;
    private AuthDto authDto;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();

        user = Users.builder()
                .id(1L)
                .name("teste")
                .email("teste")
                .password("123")
                .build();

        authDto = new AuthDto();
        authDto.setEmail("teste");
        authDto.setPassword("123");
        authDto.setId(1L);
        authDto.setToken("testToken");
    }


    @Test
    @DisplayName("Test Creating User")
    public void testCreatingUser() throws Exception {
        when(usersService.createUser(any(Users.class))).thenReturn(user);

        mockMvc.perform(post("/users/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(user)))
            .andExpect(status().isOk());

        doThrow(new RuntimeException("Erro de Bad Request")).when(usersService).createUser(any(Users.class));
        mockMvc.perform(post("/users/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(user)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test Updating User")
    public void testUpdatingUser() throws Exception {
        when(usersService.updateUser(user)).thenReturn(user);

        mockMvc.perform(put("/users/update")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(user)))
            .andExpect(status().isOk());

        doThrow(new RuntimeException("Erro de Bad Request")).when(usersService).updateUser(any(Users.class));
        mockMvc.perform(put("/users/update")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(user)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test Getting Users By Id")
    public void testGettingUsersById() throws Exception {
        when(usersService.getUserById(1L)).thenReturn(user);

        mockMvc.perform(get("/users/{id}", 1L)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(user)));

        doThrow(new RuntimeException("Erro de Bad Request")).when(usersService).getUserById(1L);
        mockMvc.perform(get("/users/{id}", 1L)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test Getting All Users")
    public  void testGettingAllUsers() throws Exception {
        List<Users> users = Arrays.asList(user);

        when(usersService.getAllUser()).thenReturn(users);
        mockMvc.perform(get("/users")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(users)));

        doThrow(new RuntimeException("Erro de Bad Request")).when(usersService).getAllUser();
        mockMvc.perform(get("/users")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Authentication User")
    public  void testeAuth() throws Exception {
        when(usersService.auth(any(AuthDto.class))).thenReturn(authDto);

        mockMvc.perform(post("/users/auth")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(authDto)))
            .andExpect(status().isOk());

        doThrow(new RuntimeException("Erro de Bad Request")).when(usersService).auth(any(AuthDto.class));
        mockMvc.perform(post("/users/auth")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(authDto)))
            .andExpect(status().isBadRequest());
    }
}
