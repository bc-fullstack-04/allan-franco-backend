package br.com.sysmap.bootcamp.web;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.service.UsersService;
import br.com.sysmap.bootcamp.dto.AuthDto;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RestController
@RequestMapping("users")
@Tag(name="Users", description = "Users API")
public class UsersController {

    private final UsersService usersService;

    //PUT
    @Operation(summary = "Update user")
    @PutMapping("/update")
    public ResponseEntity<Users> updateUser(@RequestBody Users user){
        return ResponseEntity.ok(this.usersService.updateUser(user));
    }

    //POST
    @Operation(summary = "Save user")
    @PostMapping("/create")
    public ResponseEntity<Users> createUser(@RequestBody Users user){
        return ResponseEntity.ok(this.usersService.createUser(user));
    }

    //AUTH
    @Operation(summary = "Auth user")
    @PostMapping("/auth")
    public ResponseEntity<AuthDto> auth(@RequestBody AuthDto user) {
        return ResponseEntity.ok(this.usersService.auth(user));
    }

    //GET ALL USERS
    @Operation(summary = "List users")
    @GetMapping()
    public ResponseEntity<List<Users>> getAllUser(){
        return ResponseEntity.ok(this.usersService.getAllUser());
    }

    //GET SPECIFIC USER
    @Operation(summary = "Get user by ID")
    @GetMapping("/{id}")
    public ResponseEntity<Users> getUserById(@PathVariable String id){
        return ResponseEntity.ok(this.usersService.getUserById(Long.parseLong(id)));
    }
}
