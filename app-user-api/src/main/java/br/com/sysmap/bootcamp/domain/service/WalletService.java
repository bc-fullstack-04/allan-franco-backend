package br.com.sysmap.bootcamp.domain.service;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.entities.Wallet;
import br.com.sysmap.bootcamp.domain.repository.WalletRepository;
import br.com.sysmap.bootcamp.dto.WalletDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class WalletService {
    private final UsersService usersService;
    private final WalletRepository walletRepository;

    public void debit(WalletDto walletDto) {
        Users users = usersService.findByEmail(walletDto.getEmail());
        Wallet wallet = walletRepository.findByUsers(users).orElseThrow();
        wallet.setBalance(wallet.getBalance().subtract(walletDto.getValue()));

//        wallet.setPoints(); Aqui deve se implementar o desafio de pontos

        walletRepository.save(wallet);


    }
}
