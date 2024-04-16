package br.com.sysmap.bootcamp.web;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("users/")
public class UsersController {

    private final UsersService usersService;

    @PostMapping("/save")
    public ResponseEntity<Users> save(@RequestBody Users user){
        return ResponseEntity.ok(this.usersService.save(user));
    }
}
