
package com.hiveform.controller.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.hiveform.services.IAuthService;

import com.hiveform.dto.auth.DtoForgotPasswordIU;
import com.hiveform.dto.auth.DtoGoogleAuthIU;
import com.hiveform.dto.auth.DtoLoginIU;
import com.hiveform.dto.auth.DtoRegisterIU;
import com.hiveform.dto.auth.DtoResetPasswordIU;
import com.hiveform.dto.auth.DtoVerifyEmailIU;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private IAuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody DtoLoginIU loginRequestDto) {
        return ResponseEntity.ok(authService.login(loginRequestDto));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody DtoRegisterIU registerRequestDto) {
        String message = authService.register(registerRequestDto);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestBody DtoVerifyEmailIU dto) {
        String result = authService.verifyEmail(dto);
        if (result.equals("E-posta başarıyla doğrulandı.")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/google")
    public ResponseEntity<?> google(@RequestBody DtoGoogleAuthIU googleAuthRequestDto) {
        // TODO: implement google auth logic
        return ResponseEntity.ok().build();
    }

    @GetMapping("/google/callback")
    public ResponseEntity<?> callback(@RequestParam(required = true) String code, @RequestParam(required = true) String state) {
        // TODO: implement callback logic
        return ResponseEntity.ok().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody DtoForgotPasswordIU forgotPasswordRequestDto) {
        // TODO: implement forgot password logic
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody DtoResetPasswordIU resetPasswordRequestDto) {
        // TODO: implement reset password logic
        return ResponseEntity.ok().build();
    }

}
