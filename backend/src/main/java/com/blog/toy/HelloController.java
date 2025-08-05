package com.blog.toy;

import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*") // CORS 허용
@RestController("/api")
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello from Spring Boot!";
    }
}
