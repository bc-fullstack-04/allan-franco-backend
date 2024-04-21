package br.com.sysmap.bootcamp.domain.service;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.entities.Wallet;
import br.com.sysmap.bootcamp.domain.repository.UsersRepository;
import br.com.sysmap.bootcamp.domain.repository.WalletRepository;
import br.com.sysmap.bootcamp.dto.AuthDto;
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

    @Test
    @DisplayName("Test Creating User")
    public void testCreatingUser() {
        Users users = Users.builder().id(1L).name("teste").email("test").password("teste").build();

        Wallet walletUser = new Wallet();
        walletUser.setBalance(BigDecimal.valueOf(1000.00));
        walletUser.setLastUpdate(LocalDateTime.now());
        walletUser.setPoints(0L);
        walletUser.setUsers(users);

        when(usersRepository.save(any(Users.class))).thenReturn(users);
        when(walletRepository.save(any(Wallet.class))).thenReturn(walletUser);

        assertEquals(users,usersService.createUser(users));
    }

    @Test
    @DisplayName("Test Updating User")
    public void testUpdatingUser() throws Exception {
        Users users = Users.builder().id(1L).name("teste").email("test").password("teste").build();

        when(usersRepository.findById(1L)).thenReturn(Optional.ofNullable(users));
        when(usersRepository.save(any(Users.class))).thenReturn(users);

        assertEquals(users, usersService.updateUser(users));
    }

    @Test
    @DisplayName("Test Getting Users By Id")
    public void testGettingUsersById() throws Exception {
        Users users = Users.builder().id(1L).name("teste").email("test").password("teste").build();

        when(usersRepository.findById(1L)).thenReturn(Optional.ofNullable(users));

        assertEquals(users, usersService.getUserById(1L));
    }

    @Test
    @DisplayName("Test Getting All Users")
    public  void testGettingAllUsers() throws Exception {
        List<Users> usersList = Arrays.asList(Users.builder().id(1L).name("teste").email("teste").password("123").build());

        when(usersRepository.findAll()).thenReturn((List<Users>) usersList);

        assertEquals(usersList, usersService.getAllUser());
    }

    @Test
    @DisplayName("Test Load User By Username")
    public  void testLoadUserByUsername() throws Exception {
        Users users = Users.builder().id(1L).name("teste").email("test").password(passwordEncoder.encode("teste")).build();

        when(usersRepository.findByEmail("test")).thenReturn(Optional.ofNullable(users));

        UserDetails userDetails = usersService.loadUserByUsername("test");

        assertEquals(users.getEmail(), userDetails.getUsername());
        assertEquals(users.getPassword(), userDetails.getPassword());
    }

    @Test
    @DisplayName("Authenticating User")
    public  void testAuth() throws Exception {
        Users users = Users.builder().id(1L).name("teste").email("teste").password(passwordEncoder.encode("test")).build();
        AuthDto authDto = AuthDto.builder().email("teste").password("test").id(1L).build();

        when(usersRepository.findByEmail("teste")).thenReturn(Optional.ofNullable(users));

        AuthDto result = usersService.auth(authDto);

        StringBuilder password = new StringBuilder().append(users.getEmail()).append(":").append(users.getPassword());

        String expectedToken = Base64.getEncoder().withoutPadding().encodeToString((password.toString()).getBytes());
        assertEquals(expectedToken, result.getToken());

        assertEquals(users.getEmail(), result.getEmail());
        assertEquals(users.getId(), result.getId());
    }
}
