package com.project.LibraryApp.controller;

import com.project.LibraryApp.model.Kitap;
import com.project.LibraryApp.service.KitapService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/kitaplar")
public class KitapController {

    private final KitapService kitapService;

    public KitapController(KitapService kitapService) {
        this.kitapService = kitapService;
    }

    @GetMapping
    public List<Kitap> getAllKitaplar() {
        return kitapService.findAllKitaplar();
    }

    @GetMapping("/arama")
    public List<Kitap> searchKitap(@RequestParam String keyword) {
        return kitapService.searchKitaplar(keyword);
    }
}