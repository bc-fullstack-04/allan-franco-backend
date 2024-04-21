package br.com.sysmap.bootcamp.domain.validation;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UsersValidation {
    private final UsersRepository usersRepository;

    public void emailAlreadyExists(String email) {
        if (this.usersRepository.findByEmail(email).isPresent()) {
            throw new DataIntegrityViolationException("User already exists");
        }
    }

    public void userFieldsAreSet(Users user) {
        if(user.getName() == null || user.getName().isEmpty() ||
            user.getEmail() == null || user.getEmail().isEmpty() ||
            user.getPassword() == null || user.getPassword().isEmpty()){
            throw new IllegalArgumentException("All fields must be set");
        }
    }

    public Users findByEmail(String email) {
        return this.usersRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }
}
