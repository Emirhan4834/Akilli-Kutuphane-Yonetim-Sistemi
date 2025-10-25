package com.project.LibraryApp.repository;

import com.project.LibraryApp.model.Ceza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CezaRepository extends JpaRepository<Ceza, Long> {

}