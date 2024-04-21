package br.com.sysmap.bootcamp.domain.service;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.entities.Wallet;
import br.com.sysmap.bootcamp.domain.repository.UsersRepository;
import br.com.sysmap.bootcamp.domain.repository.WalletRepository;
import br.com.sysmap.bootcamp.domain.validation.UsersValidation;
import br.com.sysmap.bootcamp.dto.WalletDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class WalletServiceTest {
    @Autowired
    private WalletService walletService;

    @MockBean
    private WalletRepository walletRepository;

    @MockBean
    private UsersValidation usersValidation;

    @Autowired
    private UsersRepository usersRepository;

    @Test
    @DisplayName("Test Debit")
    public void testDebit() throws Exception {
        Users users = Users.builder().id(1L).name("teste").email("test").password("teste").build();
        WalletDto walletDto = WalletDto.builder().email("test").value(BigDecimal.valueOf(500)).build();

        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setBalance(BigDecimal.valueOf(1000));
        wallet.setLastUpdate(LocalDateTime.now());
        wallet.setPoints(0L);
        wallet.setUsers(users);

        // Just to mock Component Validation
        when(usersValidation.findByEmail(Mockito.anyString())).thenReturn(users);

        when(walletRepository.findByUsers(users)).thenReturn(Optional.of(wallet));

        // Execute debit method
        walletService.debit(walletDto);
        when(walletRepository.save(wallet)).thenReturn(wallet);

        assertEquals(walletDto.getValue(), wallet.getBalance());
    }

    @Test
    @DisplayName("Test Adding Credit To Wallet By User")
    public void testAddingCreditToWalletByUser() throws Exception {
        Users users = Users.builder().id(1L).name("teste").email("test").password("teste").build();

        // Simulate Authentication
        Authentication authentication = new UsernamePasswordAuthenticationToken(users, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Just to mock Component Validation
        Mockito.when(usersValidation.findByEmail(Mockito.anyString())).thenReturn(users);

        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setBalance(BigDecimal.valueOf(1000.00));
        wallet.setLastUpdate(LocalDateTime.now());
        wallet.setPoints(0L);
        wallet.setUsers(users);

        when(walletRepository.findByUsers(users)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(wallet)).thenReturn(wallet);

        assertEquals(wallet, walletService.addCreditToWalletByUser(BigDecimal.valueOf(10)));
    }

    @Test
    @DisplayName("Test Getting Wallet By User")
    public void testGetWalletByUser() throws Exception {
        Users users = Users.builder().id(1L).name("teste").email("test").password("teste").build();

        // Simulate Authentication
        Authentication authentication = new UsernamePasswordAuthenticationToken(users, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Just to mock Component Validation
        Mockito.when(usersValidation.findByEmail(Mockito.anyString())).thenReturn(users);

        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setBalance(BigDecimal.valueOf(1000.00));
        wallet.setLastUpdate(LocalDateTime.now());
        wallet.setPoints(0L);
        wallet.setUsers(users);

        when(walletRepository.findByUsers(users)).thenReturn(Optional.of(wallet));

        assertEquals(wallet, walletService.getWalletByUser());
    }

    @Test
    @DisplayName("Test Authenticating User")
    public void testAuthenticatedUser() throws Exception {
        Users users = Users.builder().id(1L).name("teste").email("test").password("teste").build();

        // Simulate Authentication
        Authentication authentication = new UsernamePasswordAuthenticationToken(users, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(usersValidation.findByEmail(users.getEmail())).thenReturn(users);
        assertEquals(String.valueOf(users), walletService.getAuthenticatedUser());
    }


}
