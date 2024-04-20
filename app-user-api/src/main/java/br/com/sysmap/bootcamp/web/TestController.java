package br.com.sysmap.bootcamp.web;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/test")

public class TestController {
    @GetMapping("/hello")
    public String hello(){
        return "Hello World!";
    }
}

