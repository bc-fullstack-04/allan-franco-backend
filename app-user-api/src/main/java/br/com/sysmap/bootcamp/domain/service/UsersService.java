package br.com.sysmap.bootcamp.domain.service;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.repository.UsersRepository;
import br.com.sysmap.bootcamp.dto.AuthDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.*;

@RequiredArgsConstructor
@Slf4j
@Service
public class UsersService implements UserDetailsService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    //POST
    @Transactional(propagation = Propagation.REQUIRED)
    public Users save(Users user) {

        Optional<Users> usersOptional = this.usersRepository.findByEmail(user.getEmail());
        if (usersOptional.isPresent()) {
            throw new RuntimeException("User already exists");
        }

        user = user.toBuilder().password(this.passwordEncoder.encode(user.getPassword())).build();


        // Aqui deve se criar uma wallet para o user

        log.info("Saving user: {}", user);
        return this.usersRepository.save(user);
    }

    //PUT
    public Users updateByFields(long id, Map<String, Object> fields) {
        Optional<Users> findedUser = usersRepository.findById(id);

        if (findedUser.isPresent()) {
            fields.forEach((key, value) -> {
                Field field = ReflectionUtils.findField(Users.class, key);
                field.setAccessible(true);
                ReflectionUtils.setField(field, findedUser.get(), value);
            });
            log.info("Updating user...");
            return usersRepository.save(findedUser.get());
        }
        return null;
    }

    //GET
    public Users getOneUser(long id){
        log.info("Showing one user: {}", id);
        return this.usersRepository.findById(id).orElse(null);
    }

    //GET ALL
    public List<Users> getAllUser(){
        log.info("Showing all users");
        return this.usersRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Users> usersOptional = this.usersRepository.findByEmail(username);

        return usersOptional.map(users -> new User(users.getEmail(), users.getPassword(), new ArrayList<GrantedAuthority>()))
                .orElseThrow(() -> new UsernameNotFoundException("User not found" + username));
    }

    public Users findByEmail(String email) {
        return this.usersRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public AuthDto auth(AuthDto authDto) {
        Users users = this.findByEmail(authDto.getEmail());

        if (!this.passwordEncoder.matches(authDto.getPassword(), users.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        StringBuilder password = new StringBuilder().append(users.getEmail()).append(":").append(users.getPassword());

        return AuthDto.builder().email(users.getEmail()).token(
                Base64.getEncoder().withoutPadding().encodeToString(password.toString().getBytes())
        ).id(users.getId()).build();
    }
}
