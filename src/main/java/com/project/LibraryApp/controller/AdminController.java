package com.project.LibraryApp.controller;

import com.project.LibraryApp.model.Kitap;
import com.project.LibraryApp.model.Yazar;
import com.project.LibraryApp.model.Kategori;
import com.project.LibraryApp.service.KitapService;
import com.project.LibraryApp.service.YazarService;
import com.project.LibraryApp.service.KategoriService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final KitapService kitapService;
    private final YazarService yazarService;
    private final KategoriService kategoriService;

    public AdminController(KitapService kitapService, YazarService yazarService, KategoriService kategoriService) {
        this.kitapService = kitapService;
        this.yazarService = yazarService;
        this.kategoriService = kategoriService;
    }


    @PostMapping("/yazar")
    public ResponseEntity<Yazar> createYazar(@RequestBody Yazar yazar) {
        return new ResponseEntity<>(yazarService.saveYazar(yazar), HttpStatus.CREATED);
    }
    @GetMapping("/yazarlar")
    public List<Yazar> getAllYazarlar() { return yazarService.findAllYazarlar(); }


    @PostMapping("/kategori")
    public ResponseEntity<Kategori> createKategori(@RequestBody Kategori kategori) {
        return new ResponseEntity<>(kategoriService.saveKategori(kategori), HttpStatus.CREATED);
    }
    @GetMapping("/kategoriler")
    public List<Kategori> getAllKategoriler() { return kategoriService.findAllKategoriler(); }


    @PostMapping("/kitap")
    public ResponseEntity<Kitap> createKitap(@RequestBody Kitap kitap) {
        return new ResponseEntity<>(kitapService.saveKitap(kitap), HttpStatus.CREATED);
    }


    @DeleteMapping("/kitap/{id}")
    public ResponseEntity<Void> deleteKitap(@PathVariable Long id) {
        kitapService.deleteKitap(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @DeleteMapping("/yazar/{id}")
    public ResponseEntity<Void> deleteYazar(@PathVariable Long id) {
        yazarService.deleteYazar(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @DeleteMapping("/kategori/{id}")
    public ResponseEntity<Void> deleteKategori(@PathVariable Long id) {
        kategoriService.deleteKategori(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}