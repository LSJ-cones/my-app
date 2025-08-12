package com.blog.toy;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*") // CORS 허용
@RestController
@RequestMapping("/api")
@Tag(name = "테스트", description = "기본 테스트 API")
public class HelloController {

    @Operation(summary = "Hello 테스트", description = "Spring Boot 애플리케이션이 정상적으로 실행되는지 확인하는 테스트 API")
    @GetMapping("/hello")
    public String hello() {
        return "Hello from Spring Boot!";
    }
}
