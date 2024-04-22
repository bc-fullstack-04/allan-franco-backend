package br.com.sysmap.bootcamp.domain.service;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.entities.Wallet;
import br.com.sysmap.bootcamp.domain.repository.UsersRepository;
import br.com.sysmap.bootcamp.domain.repository.WalletRepository;
import br.com.sysmap.bootcamp.dto.AuthDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UsersServiceTest {
    @Autowired
    private UsersService usersService;

    @MockBean
    private UsersRepository usersRepository;

    @MockBean
    private WalletRepository walletRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Users user = Users.builder().build();
    private Wallet wallet = Wallet.builder().build();
    private AuthDto authDto = new AuthDto();

    @BeforeEach
    public void setup() {
        user.toBuilder()
                .id(1L)
                .name("teste")
                .email("test")
                .password("teste")
                .build();

        wallet.toBuilder()
                .id(1L)
                .balance(new BigDecimal("100"))
                .lastUpdate(LocalDateTime.now())
                .points(0L)
                .users(user)
                .build();

        authDto.setEmail("teste");
        authDto.setPassword("123");
        authDto.setId(1L);
        authDto.setToken("testToken");

    }

    @Test
    @DisplayName("Test Creating User")
    public void testCreatingUser() {
        when(usersRepository.save(any(Users.class))).thenReturn(user);
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        assertEquals(user,usersService.createUser(user));
    }

    @Test
    @DisplayName("Test Updating User")
    public void testUpdatingUser() throws Exception {
        when(usersRepository.findById(1L)).thenReturn(Optional.ofNullable(user));
        when(usersRepository.save(any(Users.class))).thenReturn(user);

        assertEquals(user, usersService.updateUser(user));
    }

    @Test
    @DisplayName("Test Getting Users By Id")
    public void testGettingUsersById() throws Exception {
        when(usersRepository.findById(1L)).thenReturn(Optional.ofNullable(user));

        assertEquals(user, usersService.getUserById(1L));
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
        when(usersRepository.findByEmail("test")).thenReturn(Optional.ofNullable(user));

        UserDetails userDetails = usersService.loadUserByUsername("test");

        assertEquals(user.getEmail(), userDetails.getUsername());
        assertEquals(user.getPassword(), userDetails.getPassword());
    }

    @Test
    @DisplayName("Authenticating User")
    public  void testAuth() throws Exception {
        when(usersRepository.findByEmail("teste")).thenReturn(Optional.ofNullable(user));

        AuthDto result = usersService.auth(authDto);

        StringBuilder password = new StringBuilder().append(user.getEmail()).append(":").append(user.getPassword());

        String expectedToken = Base64.getEncoder().withoutPadding().encodeToString((password.toString()).getBytes());
        assertEquals(expectedToken, result.getToken());

        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getId(), result.getId());
    }
}
