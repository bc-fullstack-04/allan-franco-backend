package br.com.sysmap.bootcamp.domain.service;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.entities.Wallet;
import br.com.sysmap.bootcamp.domain.repository.UsersRepository;
import br.com.sysmap.bootcamp.domain.repository.WalletRepository;
import br.com.sysmap.bootcamp.domain.validation.UsersValidation;
import br.com.sysmap.bootcamp.dto.AuthDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class UsersServiceTest {
    @Autowired
    private UsersService usersService;

    @MockBean
    private UsersRepository usersRepository;

    @MockBean
    private WalletRepository walletRepository;

    @MockBean
    private UsersValidation usersValidation;

    private Users user;
    private Wallet wallet;
    private AuthDto authDto;

    @BeforeEach
    public void setup() {
        user = Users.builder()
                .id(1L)
                .name("teste")
                .email("teste")
                .password("123")
                .build();

        wallet = Wallet.builder()
                .id(1L)
                .balance(new BigDecimal("100"))
                .lastUpdate(LocalDateTime.now())
                .points(0L)
                .users(user)
                .build();

        authDto = AuthDto.builder()
                .email("test")
                .password("wrongPassword")
                .id(1L)
                .token("testToken")
                .build();
    }

    @Test
    @DisplayName("Test Creating User")
    public void testCreatingUser() {
        when(usersRepository.save(any(Users.class))).thenReturn(user);
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);
        assertEquals(user, usersService.createUser(user));
    }

    @Test
    @DisplayName("Test Updating User")
    public void testUpdatingUser() throws Exception {
        Users user = Users.builder()
                .id(1L)
                .name("teste")
                .email("test")
                .password("teste")
                .build();

        when(usersRepository.findById(anyLong())).thenReturn(Optional.ofNullable((user)));
        when(usersRepository.save(any(Users.class))).thenReturn(user);

        assertEquals(user, usersService.updateUser(user));
    }

    @Test
    @DisplayName("Test Getting Users By Id")
    public void testGettingUsersById() throws Exception {
        when(usersRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        assertEquals(user, usersService.getUserById(anyLong()));
    }

    @Test
    @DisplayName("Test Getting All Users")
    public  void testGettingAllUsers() throws Exception {
        List<Users> usersList = Arrays.asList(user);

        when(usersRepository.findAll()).thenReturn((List<Users>) usersList);

        assertEquals(usersList, usersService.getAllUser());
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
    @DisplayName("Authenticating User")
    public void testAuth() throws Exception {
        when(usersRepository.findByEmail(anyString())).thenReturn(Optional.ofNullable(user));

        assertThrows(RuntimeException.class, () -> usersService.auth(authDto));
        verify(usersValidation).findByEmail(anyString());
    }
}
