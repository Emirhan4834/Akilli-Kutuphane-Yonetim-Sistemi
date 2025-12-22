package com.project.LibraryApp.service;

import com.project.LibraryApp.model.Kullanici;
import com.project.LibraryApp.model.Rol;
import com.project.LibraryApp.repository.KullaniciRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;


@Service
public class KullaniciService {

    private final KullaniciRepository kullaniciRepository;
    private final PasswordEncoder passwordEncoder;
    private final BildirimService bildirimService;

    public KullaniciService(KullaniciRepository kullaniciRepository, PasswordEncoder passwordEncoder, BildirimService bildirimService) {
        this.kullaniciRepository = kullaniciRepository;
        this.passwordEncoder = passwordEncoder;
        this.bildirimService = bildirimService;
    }

    public Kullanici registerUser(Kullanici kullanici) {

        String hashedPassword = passwordEncoder.encode(kullanici.getSifreHash());
        kullanici.setSifreHash(hashedPassword);

        kullanici.setRol(Rol.KULLANICI);


        Kullanici savedUser = kullaniciRepository.save(kullanici);


        try {
            if (savedUser.getMail() != null && !savedUser.getMail().isEmpty()) {
                bildirimService.sendHosgeldinMesaji(savedUser.getMail(), savedUser.getKullaniciAdi());
            }
        }
        catch (Exception e) {
            System.err.println("Hoş geldin maili gönderilemedi: " + e.getMessage());
        }

        return savedUser;
    }

    public List<Kullanici> findAllKullanicilar() {
        return kullaniciRepository.findAll();
    }

    public Optional<Kullanici> findKullaniciById(Long id) {
        return kullaniciRepository.findById(id);
    }

    public Optional<Kullanici> findByKullaniciAdi(String kullaniciAdi) {
        return kullaniciRepository.findByKullaniciAdi(kullaniciAdi);
    }

    public boolean verifyPassword(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }

    public void deleteKullanici(Long id) {
        kullaniciRepository.deleteById(id);
    }
}