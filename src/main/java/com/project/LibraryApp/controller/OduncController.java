package com.project.LibraryApp.controller;

import com.project.LibraryApp.model.Ceza;
import com.project.LibraryApp.model.OduncIslemi;
import com.project.LibraryApp.service.CezaService;
import com.project.LibraryApp.service.OduncService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/odunc")
public class OduncController {

    private final OduncService oduncService;
    private final CezaService cezaService;

    public OduncController(OduncService oduncService, CezaService cezaService) {
        this.oduncService = oduncService;
        this.cezaService = cezaService;
    }

    @PostMapping("/al")
    public ResponseEntity<?> oduncAl(@RequestBody Map<String, Object> request) {
        try {
            Long kId = ((Number) request.get("kullaniciId")).longValue();
            Long kitId = ((Number) request.get("kitapId")).longValue();
            LocalDate tarih = LocalDate.parse((String) request.get("iadeTarihi"));
            oduncService.kitapOduncAl(kId, kitId, tarih);
            return new ResponseEntity<>(Map.of("mesaj", "Başarılı"), HttpStatus.CREATED);
        }
        catch (Exception e) {
        e.printStackTrace(); // Hatayı konsola kırmızı yazılarla döker
        return new ResponseEntity<>(Map.of("hata", e.getMessage()), HttpStatus.BAD_REQUEST);
    }
    }

    @PutMapping("/iade/{islemId}")
    public ResponseEntity<?> iadeEt(@PathVariable Long islemId) {
        oduncService.kitapIadeEt(islemId);
        return ResponseEntity.ok(Map.of("mesaj", "İade alındı"));
    }

    @GetMapping("/user/{userId}")
    public List<OduncIslemi> getKullaniciIslemleri(@PathVariable Long userId) {
        return oduncService.findIslemlerByKullanici(userId);
    }

    @GetMapping("/cezalar/kullanici/{userId}")
    public List<Ceza> getKullaniciCezalari(@PathVariable Long userId) {
        return cezaService.findCezalarByKullanici(userId);
    }

    @PutMapping("/ceza/odeme/{cezaId}")
    public void odemeYap(@PathVariable Long cezaId) {
        cezaService.odemeYap(cezaId);
    }
}