package br.com.sysmap.bootcamp.domain.service;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class UsersService {

    private final UsersRepository usersRepository;

    @Transactional(propagation = Propagation.REQUIRED)

    //POST
    public Users save(Users user){
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
}
