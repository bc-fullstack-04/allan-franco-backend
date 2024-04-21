package br.com.sysmap.bootcamp.web;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.entities.Wallet;
import br.com.sysmap.bootcamp.domain.service.WalletService;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class WalletControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WalletService walletService;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Test Adding To Wallet By User")
    public void testAddingToWalletByUser() throws Exception {
        Users users = Users.builder().id(1L).name("teste").email("teste").password("123").build();
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setBalance(BigDecimal.valueOf(1000));
        wallet.setPoints(15L);
        wallet.setUsers(users);

        when(walletService.addCreditToWalletByUser(BigDecimal.valueOf(10))).thenReturn(wallet);

        mockMvc.perform(post("/wallet/credit/{value}", 10)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(wallet)));
    }

    @Test
    @DisplayName("Test Getting Wallet by User")
    public void testGettingWalletByUser() throws Exception {
        Users users = Users.builder().id(1L).name("teste").email("teste").password("123").build();
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setBalance(BigDecimal.valueOf(1000));
        wallet.setPoints(15L);
        wallet.setUsers(users);

        when(walletService.getWalletByUser()).thenReturn(wallet);

        mockMvc.perform(get("/wallet", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(wallet)));
    }
}
