package br.com.sysmap.bootcamp.domain.service;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class UsersService {

    private final UsersRepository usersRepository;

    //POST
    @Transactional(propagation = Propagation.REQUIRED)
    public Users save(Users user){
        log.info("Saving user: {}", user);
        return this.usersRepository.save(user);
    }

    //PUT


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
