package br.com.sysmap.bootcamp.domain.service;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.entities.Wallet;
import br.com.sysmap.bootcamp.domain.repository.UsersRepository;
import br.com.sysmap.bootcamp.domain.repository.WalletRepository;
import br.com.sysmap.bootcamp.domain.validation.UsersValidation;
import br.com.sysmap.bootcamp.dto.AuthDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    @MockBean
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setup() {
        user = Users.builder()
                .id(1L)
                .name("teste")
                .email("test")
                .password("testPassword")
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
                .password("testPassword")
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
        Users user2 = Users.builder()
                .id(2L)
                .name(null)
                .email(null)
                .password(null)
                .build();

        when(usersRepository.findById(anyLong())).thenReturn(Optional.ofNullable((user)));
        when(usersRepository.save(any(Users.class))).thenReturn(user);

        assertEquals(user, usersService.updateUser(user));

        when(usersRepository.findById(anyLong())).thenReturn(Optional.ofNullable((user)));

        Users updatedUser = usersService.updateUser(user2);

        assertEquals(user.getName(), updatedUser.getName());
        assertEquals(user.getEmail(), updatedUser.getEmail());
        assertEquals(user.getPassword(), updatedUser.getPassword());
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
        when(usersValidation.findByEmail(anyString())).thenReturn(user);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        AuthDto result = usersService.auth(authDto);
        assertEquals(authDto.getEmail(), result.getEmail());
        assertEquals(authDto.getId(), result.getId());

        verify(usersValidation).findByEmail(anyString());

        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        assertThrows(RuntimeException.class, () -> {
            usersService.auth(authDto);
        });
    }
}
