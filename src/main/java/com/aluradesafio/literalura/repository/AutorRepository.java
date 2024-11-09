package com.aluradesafio.literalura.repository;

import com.aluradesafio.literalura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AutorRepository extends JpaRepository<Autor, Long> {


    @Query("SELECT a FROM Autor a WHERE a.nombre = :nombre")
    Autor findByNombre(String nombre);


    @Query("SELECT a FROM Autor a WHERE a.fechaNacimiento < :anio and a.fechaMuerte > :anio")
    List<Autor> findByFechaNacimientoAfter(int anio);
}
