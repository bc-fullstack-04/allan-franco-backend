package br.com.sysmap.bootcamp.domain.service;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.entities.Wallet;
import br.com.sysmap.bootcamp.domain.repository.UsersRepository;
import br.com.sysmap.bootcamp.domain.repository.WalletRepository;
import br.com.sysmap.bootcamp.domain.validation.UsersValidation;
import br.com.sysmap.bootcamp.dto.AuthDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Slf4j
@Service
public class UsersService implements UserDetailsService {

    private final UsersRepository usersRepository;
    private final WalletRepository walletRepository;
    private final UsersValidation usersValidation;
    private final PasswordEncoder passwordEncoder;

    @Transactional(propagation = Propagation.REQUIRED)
    public Users createUser(Users user) {
        // Validates that e-mail exists
        usersValidation.emailAlreadyExists(user.getEmail());

        // Set Wallet
        user = user.toBuilder().password(this.passwordEncoder.encode(user.getPassword())).build();
        Wallet wallet = Wallet.builder()
                .balance(BigDecimal.valueOf(1000))
                .lastUpdate(LocalDateTime.now())
                .points(0L)
                .users(user)
                .build();

        this.walletRepository.save(wallet);

        log.info("Saving user: {}", user);

        return this.usersRepository.save(user);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Users updateUser(Users user) {
        // Validates that the ID was provided
        Users existingUser = this.usersRepository.findById(user.getId()).orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Validates that e-mail exists
        if (!existingUser.getEmail().equals(user.getEmail())) {
            usersValidation.emailAlreadyExists(user.getEmail());
        }

        //Update user by fields
        Users updatedUser = existingUser.toBuilder()
                .name(user.getName() != null ? user.getName() : existingUser.getName())
                .email(user.getEmail() != null ? user.getEmail() : existingUser.getEmail())
                .password(user.getPassword() != null ? this.passwordEncoder.encode(user.getPassword()) : existingUser.getPassword())
                .build();

        log.info("Updating user: {}", user.getId());
        return usersRepository.save(updatedUser);
    }

    @Transactional(readOnly = true)
    public Users getUserById(long id){
        Users existingUser = this.usersRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        log.info("Showing user: {}", id);
        return existingUser;
    }

    @Transactional(readOnly = true)
    public List<Users> getAllUser(){
        log.info("Showing all users");
        return this.usersRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Users> usersOptional = this.usersRepository.findByEmail(username);

        return usersOptional.map(users -> new User(users.getEmail(), users.getPassword(), new ArrayList<GrantedAuthority>()))
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AuthDto auth(AuthDto authDto) {
        Users users = usersValidation.findByEmail(authDto.getEmail());

        if (!this.passwordEncoder.matches(authDto.getPassword(), users.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        StringBuilder password = new StringBuilder().append(users.getEmail()).append(":").append(users.getPassword());

        return AuthDto.builder().email(users.getEmail()).token(
                Base64.getEncoder().withoutPadding().encodeToString(password.toString().getBytes())
        ).id(users.getId()).build();
    }
}
