
package com.hiveform.controller.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.hiveform.services.IAuthService;

import com.hiveform.dto.auth.ForgotPasswordRequestDto;
import com.hiveform.dto.auth.GoogleAuthRequestDto;
import com.hiveform.dto.auth.LoginRequestDto;
import com.hiveform.dto.auth.RegisterRequestDto;
import com.hiveform.dto.auth.ResetPasswordRequestDto;
import com.hiveform.dto.auth.VerifyEmailRequestDto;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private IAuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto) {
        // TODO: implement login logic
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequestDto registerRequestDto) {
        String message = authService.register(registerRequestDto);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestBody VerifyEmailRequestDto dto) {
        String result = authService.verifyEmail(dto);
        if (result.equals("E-posta başarıyla doğrulandı.")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/google")
    public ResponseEntity<?> google(@RequestBody GoogleAuthRequestDto googleAuthRequestDto) {
        // TODO: implement google auth logic
        return ResponseEntity.ok().build();
    }

    @GetMapping("/google/callback")
    public ResponseEntity<?> callback(@RequestParam(required = true) String code, @RequestParam(required = true) String state) {
        // TODO: implement callback logic
        return ResponseEntity.ok().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequestDto forgotPasswordRequestDto) {
        // TODO: implement forgot password logic
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequestDto resetPasswordRequestDto) {
        // TODO: implement reset password logic
        return ResponseEntity.ok().build();
    }

}
