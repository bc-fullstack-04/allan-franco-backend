package br.com.sysmap.bootcamp.domain.service;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.entities.Wallet;
import br.com.sysmap.bootcamp.domain.repository.WalletRepository;
import br.com.sysmap.bootcamp.domain.validation.UsersValidation;
import br.com.sysmap.bootcamp.dto.WalletDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Service
public class WalletService{

    private final WalletRepository walletRepository;
    private final UsersValidation usersValidation;

    public void debit(WalletDto walletDto) {
        Users users = usersValidation.findByEmail(walletDto.getEmail());
        Wallet wallet = walletRepository.findByUsers(users).orElseThrow();

        Wallet updatedWalletValue = wallet.toBuilder()
                .balance(wallet.getBalance().subtract(walletDto.getValue()))
                .points(wallet.getPoints() + setPointPerDay())
                .build();

        this.walletRepository.save(updatedWalletValue);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    // POST
    public Wallet addCreditToWalletByUser(BigDecimal value){

        Users authenticatedUser = usersValidation.findByEmail(getAuthenticatedUser());

        Wallet wallet = this.walletRepository.findByUsers(authenticatedUser)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));

        // Sum Wallet's balance with entry value
        Wallet updatedWalletValue = wallet.toBuilder().balance(wallet.getBalance().add(value)).build();


        log.info("Add {} credit to wallet", value);
        return this.walletRepository.save(updatedWalletValue);
    }

    @Transactional(readOnly = true)
    // GET
    public Wallet getWalletByUser() {
        // Get Authenticated User's email
        Users authenticatedUser = usersValidation.findByEmail(getAuthenticatedUser());

        log.info("Showing {}'s Wallet", authenticatedUser.getName());
        return this.walletRepository.findByUsers(authenticatedUser).orElseThrow(() -> new IllegalArgumentException("Wallet not found"));
    }

    private String getAuthenticatedUser() {
        // Get the context of security
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Get username security (email)
        return authentication.getName();
    }

    public Long setPointPerDay() {
        Map<String, Long> pointPerDay = new HashMap<>();
        pointPerDay.put("SUNDAY", 25L);
        pointPerDay.put("MONDAY", 7L);
        pointPerDay.put("TUESDAY", 6L);
        pointPerDay.put("WEDNESDAY", 2L);
        pointPerDay.put("THURSDAY", 10L);
        pointPerDay.put("FRIDAY", 15L);
        pointPerDay.put("SATURDAY", 20L);

        DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
        return pointPerDay.get(dayOfWeek.toString());
    }
}
