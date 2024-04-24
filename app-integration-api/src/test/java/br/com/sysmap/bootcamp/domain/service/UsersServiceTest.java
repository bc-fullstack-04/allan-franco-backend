package br.com.sysmap.bootcamp.domain.service;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class UsersServiceTest {
    @Autowired
    private UsersService usersService;

    @MockBean
    private UsersRepository usersRepository;

    private Users user;

    @BeforeEach
    public void setup() {
        user = Users.builder()
                .id(1L)
                .email("teste")
                .password("123")
                .build();
    }
    @Test
    @DisplayName("Test Load User By Username")
    public  void testLoadUserByUsername() throws Exception {
        when(usersRepository.findByEmail(anyString())).thenReturn(Optional.ofNullable(user));

        UserDetails userDetails = usersService.loadUserByUsername(anyString());

        assertEquals(user.getEmail(), userDetails.getUsername());
        assertEquals(user.getPassword(), userDetails.getPassword());
    }

    @Test
    @DisplayName("Testing Finding User By Email")
    public void testFindByEmail() throws Exception {
        when(usersRepository.findByEmail(anyString())).thenReturn(Optional.ofNullable(user));

        assertEquals(user, usersService.findByEmail(user.getEmail()));
    }
}
