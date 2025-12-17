package com.project.LibraryApp.controller;

import com.project.LibraryApp.model.Kullanici;
import com.project.LibraryApp.service.AuthService;
import com.project.LibraryApp.service.KullaniciService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final KullaniciService kullaniciService;

    public AuthController(AuthService authService, KullaniciService kullaniciService) {
        this.authService = authService;
        this.kullaniciService = kullaniciService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        try {
            Map<String, Object> response = authService.authenticateAndGenerateToken(
                    request.get("kullaniciAdi"),
                    request.get("sifre")
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("hata", e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Kullanici kullanici) {
        try {
            Kullanici yeniKullanici = kullaniciService.registerUser(kullanici);
            return new ResponseEntity<>(yeniKullanici, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("hata", "Kayıt başarısız."), HttpStatus.BAD_REQUEST);
        }
    }
}