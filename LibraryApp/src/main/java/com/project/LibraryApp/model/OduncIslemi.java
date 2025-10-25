package com.project.LibraryApp.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Table (name = "odunc_islemleri")

public class OduncIslemi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    @ManyToOne
    @JoinColumn(name = "kullanici_id", nullable = false)
    private Kullanici kullanici;

    @ManyToOne
    @JoinColumn(name = "kitap_id", nullable = false)
    private Kitap kitap;


    @Column(nullable = false)
    private LocalDate oduncTarihi;


    @Column(nullable = false)
    private LocalDate beklenenIadeTarihi;

    private LocalDate gercekIadeTarihi;


    @OneToOne(mappedBy = "oduncIslemi", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Ceza ceza;
}
