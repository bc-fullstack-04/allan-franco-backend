package br.com.sysmap.bootcamp.domain.service;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.entities.Wallet;
import br.com.sysmap.bootcamp.domain.repository.WalletRepository;
import br.com.sysmap.bootcamp.domain.validation.UsersValidation;
import br.com.sysmap.bootcamp.dto.WalletDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
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

    private Users user;
    private Wallet wallet;
    private WalletDto walletDto;

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
                .balance(BigDecimal.valueOf(1000))
                .points(15L)
                .users(user)
                .build();

        walletDto = new WalletDto("test", BigDecimal.valueOf(100));
    }

    @Test
    @DisplayName("Test Debit")
    public void testDebit() throws Exception {
        when(usersValidation.findByEmail(anyString())).thenReturn(user);
        when(walletRepository.findByUsers(any(Users.class))).thenReturn(Optional.of(wallet));

        // Execute debit method
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        walletService.debit((walletDto));

        ArgumentCaptor<Wallet> walletCaptor = ArgumentCaptor.forClass(Wallet.class);
        verify(walletRepository).save(walletCaptor.capture());
    }

    @Test
    @DisplayName("Test Adding Credit To Wallet By User")
    public void testAddingCreditToWalletByUser() throws Exception {

        // Simulate Authentication
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Just to mock Component Validation
        when(usersValidation.findByEmail(anyString())).thenReturn(user);

        when(walletRepository.findByUsers(any(Users.class))).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);
        walletService.addCreditToWalletByUser(BigDecimal.valueOf(10));
        //assertEquals(wallet, );
    }

    @Test
    @DisplayName("Test Getting Wallet By User")
    public void testGetWalletByUser() throws Exception {
        // Simulate Authentication
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Just to mock Component Validation
        Mockito.when(usersValidation.findByEmail(anyString())).thenReturn(user);

        when(walletRepository.findByUsers(user)).thenReturn(Optional.of(wallet));

        assertEquals(wallet, walletService.getWalletByUser());
    }

}
