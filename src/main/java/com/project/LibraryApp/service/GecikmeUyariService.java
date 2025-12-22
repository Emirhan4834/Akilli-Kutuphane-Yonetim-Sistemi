package com.project.LibraryApp.service;

import com.project.LibraryApp.model.OduncIslemi;
import com.project.LibraryApp.repository.OduncIslemiRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class GecikmeUyariService {

    private final OduncIslemiRepository oduncRepo;
    private final BildirimService bildirimService;

    public GecikmeUyariService(OduncIslemiRepository oduncRepo, BildirimService bildirimService) {
        this.oduncRepo = oduncRepo;
        this.bildirimService = bildirimService;
    }


    @Scheduled(fixedRate = 30000)
    @Transactional
    public void gecikenleriUyar() {

        LocalDateTime now = LocalDateTime.now();

        List<OduncIslemi> gecikenler =
                oduncRepo.findAllByGercekIadeTarihiIsNullAndBeklenenIadeTarihiBeforeAndGecikmeMailGonderildiMiFalse(now);

        for (OduncIslemi islem : gecikenler) {
            long gecikmeGun = ChronoUnit.DAYS.between(islem.getBeklenenIadeTarihi(), now);
            if (gecikmeGun <= 0) gecikmeGun = 1;

            bildirimService.sendLateReturnMail(
                    islem.getKullanici(),
                    islem.getKitap(),
                    islem.getBeklenenIadeTarihi().toLocalDate(),
                    gecikmeGun
            );

            islem.setGecikmeMailGonderildiMi(true);
            oduncRepo.save(islem);

            System.out.println("Gecikme maili gönderildi: " + islem.getKullanici().getAd());
        }
    }
}