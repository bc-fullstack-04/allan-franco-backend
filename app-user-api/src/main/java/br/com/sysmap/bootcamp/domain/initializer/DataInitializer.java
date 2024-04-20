package br.com.sysmap.bootcamp.domain.initializer;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.repository.UsersRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class DataInitializer implements CommandLineRunner {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // PRE CONFIGURATION OF ADMIN USER WITH ENCRYPT PASSWORD - TKS CHAT GPT :D
        Users user = Users.builder().id(1L).name("admin").email("admin@auth.com").password(this.passwordEncoder.encode("321")).build();
        usersRepository.save(user);
    }
}