package com.blog.toy.controller;

import com.blog.toy.dto.auth.AuthResponseDto;
import com.blog.toy.dto.auth.LoginRequestDto;
import com.blog.toy.dto.auth.SignupRequestDto;
import com.blog.toy.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "인증 관리", description = "로그인, 회원가입 API")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Operation(summary = "로그인", description = "사용자 로그인을 처리합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "로그인 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        AuthResponseDto response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "회원가입", description = "새로운 사용자 회원가입을 처리합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "회원가입 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터 또는 중복된 사용자명/이메일")
    })
    @PostMapping("/signup")
    public ResponseEntity<AuthResponseDto> signup(@Valid @RequestBody SignupRequestDto signupRequest) {
        AuthResponseDto response = authService.signup(signupRequest);
        return ResponseEntity.ok(response);
    }
}
