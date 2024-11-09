package com.aluradesafio.literalura.repository;

import com.aluradesafio.literalura.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LibroRepository extends JpaRepository<Libro, Long> {

    @Query("SELECT l FROM Libro l WHERE l.idioma = :idioma")
    List<Libro> findByIdioma(String idioma);

    @Query("SELECT l FROM Libro l WHERE l.titulo = :title")
    Libro findByTitulo(String title);
}
