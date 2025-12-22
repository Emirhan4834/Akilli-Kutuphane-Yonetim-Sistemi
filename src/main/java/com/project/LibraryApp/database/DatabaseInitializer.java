package com.project.LibraryApp.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        createHizliCezaTrigger();
        createStokLogTableAndTrigger();
        createRaporProcedure();
    }


    private void createHizliCezaTrigger() {
        try {
            String createFunctionSql = """
                CREATE OR REPLACE FUNCTION set_hizli_ceza_tarihi() 
                RETURNS TRIGGER AS $$
                BEGIN
                    NEW.beklenen_iade_tarihi := CURRENT_TIMESTAMP + INTERVAL '1 minute';
                    RETURN NEW;
                END;
                $$ LANGUAGE plpgsql;
            """;

            String createTriggerSql = """
                DROP TRIGGER IF EXISTS trg_hizli_ceza ON odunc_islemleri;
                
                CREATE TRIGGER trg_hizli_ceza
                BEFORE INSERT ON odunc_islemleri
                FOR EACH ROW
                EXECUTE FUNCTION set_hizli_ceza_tarihi();
            """;

            jdbcTemplate.execute(createFunctionSql);
            jdbcTemplate.execute(createTriggerSql);
            System.out.println("✅ [1/3] Hızlı Ceza Trigger'ı güncellendi.");
        } catch (Exception e) {
            System.err.println("❌ Hızlı Ceza Trigger hatası: " + e.getMessage());
        }
    }


    private void createStokLogTableAndTrigger() {
        try {
            String createTableSql = """
                CREATE TABLE IF NOT EXISTS kitap_stok_log (
                    id BIGSERIAL PRIMARY KEY,
                    kitap_id BIGINT,
                    kitap_adi VARCHAR(255),
                    eski_stok INT,
                    yeni_stok INT,
                    islem_tarihi TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                );
            """;

            String createFunctionSql = """
                CREATE OR REPLACE FUNCTION stok_takip_func()
                RETURNS TRIGGER AS $$
                BEGIN
                    -- Sadece stok sayısı değiştiyse log at
                    IF OLD.kopya_sayisi <> NEW.kopya_sayisi THEN
                        INSERT INTO kitap_stok_log (kitap_id, kitap_adi, eski_stok, yeni_stok)
                        VALUES (OLD.id, OLD.ad, OLD.kopya_sayisi, NEW.kopya_sayisi);
                    END IF;
                    RETURN NEW;
                END;
                $$ LANGUAGE plpgsql;
            """;

            String createTriggerSql = """
                DROP TRIGGER IF EXISTS trg_stok_degisim ON kitap;
                
                CREATE TRIGGER trg_stok_degisim
                AFTER UPDATE ON kitap
                FOR EACH ROW
                EXECUTE FUNCTION stok_takip_func();
            """;

            jdbcTemplate.execute(createTableSql);
            jdbcTemplate.execute(createFunctionSql);
            jdbcTemplate.execute(createTriggerSql);
            System.out.println("✅ [2/3] Stok Log Tablosu ve Trigger'ı güncellendi.");

        } catch (Exception e) {
            System.err.println("❌ Stok Trigger hatası: " + e.getMessage());
        }
    }


    private void createRaporProcedure() {
        try {
            String createProcSql = """
                CREATE OR REPLACE PROCEDURE gecikme_kontrol_raporu()
                LANGUAGE plpgsql
                AS $$
                DECLARE
                    kayit RECORD;
                    gecikme INT;
                    gunluk_ceza DECIMAL := 0.50;
                    toplam_borc DECIMAL;
                BEGIN
                    RAISE NOTICE '--- GECİKMİŞ KİTAP RAPORU ---';
                    
                    FOR kayit IN 
                        SELECT k.ad, o.beklenen_iade_tarihi 
                        FROM odunc_islemleri o
                        JOIN kitap k ON o.kitap_id = k.id
                        WHERE o.beklenen_iade_tarihi < CURRENT_TIMESTAMP 
                        AND o.gercek_iade_tarihi IS NULL
                    LOOP
                        gecikme := EXTRACT(DAY FROM (CURRENT_TIMESTAMP - o.beklenen_iade_tarihi));
                        toplam_borc := gecikme * gunluk_ceza;
                        RAISE NOTICE 'Kitap: %, Gecikme: % Gün, Tahmini Ceza: % TL', kayit.ad, gecikme, toplam_borc;
                    END LOOP;
                    
                    RAISE NOTICE '--- RAPOR BİTTİ ---';
                END;
                $$;
            """;

            jdbcTemplate.execute(createProcSql);
            System.out.println("✅ [3/3] Rapor Prosedürü güncellendi.");

        } catch (Exception e) {
            System.err.println("❌ Rapor Prosedürü hatası: " + e.getMessage());
        }
    }
}