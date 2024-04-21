package br.com.sysmap.bootcamp.domain.service;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.entities.Wallet;
import br.com.sysmap.bootcamp.domain.repository.UsersRepository;
import br.com.sysmap.bootcamp.domain.repository.WalletRepository;
import br.com.sysmap.bootcamp.domain.validation.UsersValidation;
import br.com.sysmap.bootcamp.dto.WalletDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Slf4j
@Service
public class WalletService{

    private final UsersService usersService;

    private final WalletRepository walletRepository;
    private final UsersValidation usersValidation;

    public void debit(WalletDto walletDto) {
        Users users = usersValidation.findByEmail(walletDto.getEmail());
        Wallet wallet = walletRepository.findByUsers(users).orElseThrow();
        wallet.setBalance(wallet.getBalance().subtract(walletDto.getValue()));

//        wallet.setPoints(); Aqui deve se implementar o desafio de pontos

        walletRepository.save(wallet);
    }

    // POST
    public Wallet addCreditToWalletByUser(BigDecimal value){

        Users authenticatedUser = usersValidation.findByEmail(getAuthenticatedUser());

        Wallet wallet = this.walletRepository.findByUsers(authenticatedUser)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));

        // Sum Wallet's balance with entry value
        wallet.setBalance(wallet.getBalance().add(value));

        log.info("Add {} credit to wallet", value);
        return this.walletRepository.save(wallet);
    }

    // GET
    public Wallet getWalletByUser() {
        // Get Authenticated User's email
        Users authenticatedUser = usersValidation.findByEmail(getAuthenticatedUser());

        log.info("Showing {}'s Wallet", authenticatedUser.getName());
        return this.walletRepository.findByUsers(authenticatedUser).orElseThrow(() -> new IllegalArgumentException("Wallet not found"));
    }

    public String getAuthenticatedUser() {
        // Get the context of security
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Get username security (email)
        return authentication.getName();
    }
}
