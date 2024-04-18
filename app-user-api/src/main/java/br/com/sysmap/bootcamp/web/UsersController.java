package br.com.sysmap.bootcamp.web;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("users/")
public class UsersController {

    private final UsersService usersService;

    //POST
    @PostMapping("/save")
    public ResponseEntity<Users> save(@RequestBody Users user){
        return ResponseEntity.ok(this.usersService.save(user));
    }

    //PUT


    //GET SPECIFIC USER
    @GetMapping("/{id}")
    public ResponseEntity<Users> getOneUser(@PathVariable String id){
        return ResponseEntity.ok(this.usersService.getOneUser(Long.parseLong(id)));
    }

    //GET ALL USERS
    @GetMapping("/all")
    public ResponseEntity<List<Users>> getAllUser(){
        return ResponseEntity.ok(this.usersService.getAllUser());
    }
}
