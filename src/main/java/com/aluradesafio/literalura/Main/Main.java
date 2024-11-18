package com.aluradesafio.literalura.Main;

import com.aluradesafio.literalura.model.Autor;
import com.aluradesafio.literalura.model.DatosLibro;
import com.aluradesafio.literalura.model.Libro;
import com.aluradesafio.literalura.repository.AutorRepository;
import com.aluradesafio.literalura.repository.LibroRepository;
import com.aluradesafio.literalura.service.ConsumoApi;
import com.aluradesafio.literalura.service.ConvierteDatos;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Main {

    private Scanner scan = new Scanner(System.in);
    private ConsumoApi consumoApi = new ConsumoApi();
    private final String URL_BASE = "https://gutendex.com/books";
    private ConvierteDatos convierteDatos = new ConvierteDatos();
    private LibroRepository libroRepository;
    private AutorRepository autorRepository;

    public Main(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;

    }

    public void menu() throws UnsupportedEncodingException {

        int option =0;

        do {
            System.out.println("""
                ----------------
                Elija la opción a través de su número:
                1. Buscar libro por título
                2. Listar libros registrados
                3. Listar autores registrados
                4. Listar autores vivos en un determinado año
                5. Listar libros por idioma
                0. Salir
                """);

            try {
                option = scan.nextInt();
                switch (option){
                    case 1: buscarLibro();break;
                    case 2: listarLibros();break;
                    case 3: listarAutores();break;
                    case 4: listarAutoresVivos();break;
                    case 5: listarLibrosPorIdioma();break;
                    case 0:
                        System.out.println("Saliendo...");break;
                    default:
                        System.out.println("Opcion no valida");
                }
            } catch (InputMismatchException e) {
                System.out.println("Error: el valor ingresado no es un número entero.");
                scan.next();
                option=-1;
            }

        }while (option != 0);


    }


    public void buscarLibro() throws UnsupportedEncodingException {

        System.out.println("Ingrese el nombre del libro que desea buscar: ");
        String libro;

        while(true){
            libro = scan.nextLine().trim();
            if (!libro.isEmpty()){
                break;
            }
        }

        String encodeLibro = URLEncoder.encode(libro, "UTF-8");

        var json = consumoApi.obtenerDatos(URL_BASE +"/?search="+ encodeLibro);
        DatosLibro datosLibro = convierteDatos.convertir(json, DatosLibro.class);

        if (datosLibro.results().isEmpty()){
            System.out.println("Libro no encontrado");
            return;
        }

        Libro libroValidation = libroRepository.findByTitulo(datosLibro.results().stream().findFirst().get().title());
        if (libroValidation != null){
            System.out.printf("El libro %s ya se encuentra registrado\n", libroValidation.getTitulo());;
            return;
        }

        Autor autorValidation = autorRepository.findByNombre(datosLibro.results().stream().findFirst().get().authors().stream().findFirst().get().name());
        Libro libroSave;

        if (autorValidation != null){
            libroSave = new Libro(datosLibro);
            libroSave.setAutor(autorValidation);
            libroRepository.save(libroSave);
        }else{
            Autor nuevoAutor = new Autor(datosLibro);
            autorRepository.save(nuevoAutor);
            libroSave = new Libro(datosLibro);
            libroSave.setAutor(nuevoAutor);
            libroRepository.save(libroSave);
        }

        showLibro(libroSave);
        System.out.println("Libro agregado.");

    }

    public void listarLibros(){

        List<Libro> libros = libroRepository.findAll();

        if (libros.isEmpty()){
            System.out.println("No se encontraron libros");
            return;
        }

        System.out.println("\n*********** LIBROS REGISTRADOS ***********\n");
        libros.forEach(l-> showLibro(l));

    }

    public void listarAutores(){

        List<Autor> autores = autorRepository.findAll();

        if (autores.isEmpty()){
            System.out.println("No se encontraron autores");
            return;
        }

        System.out.println("\n*********** AUTORES REGISTRADOS ***********");
        autores.forEach(autor ->{

            String libros = autor.getLibros().stream().map(l -> l.getTitulo()).toList().toString();

            System.out.printf("""
                    
                    
                    Autor: %s
                    Fecha de nacimiento: %d
                    Fecha de muerte: %d
                    Libros: %s
                    """, autor.getNombre(), autor.getFechaNacimiento(), autor.getFechaMuerte(), libros);
        });
    }

    public void listarAutoresVivos(){

        System.out.println("Ingrese el año vivo de autor que desee buscar: ");
        int anio = scan.nextInt();

        List<Autor> autores = autorRepository.findByFechaNacimientoAfter(anio);

        if (autores.isEmpty()){
            System.out.println("No se encontraron autores vivos en ese año");
            return;
        }

        System.out.println("\n*********** AUTORES VIVOS EN EL AÑO " + anio + " ***********");

        autores.forEach(autor ->{
            String libros = autor.getLibros().stream().map(l -> l.getTitulo()).toList().toString();

            System.out.printf("""
                    
                    
                    Autor: %s
                    Fecha de nacimiento: %d
                    Fecha de muerte: %d
                    Libros: %s
                    """, autor.getNombre(), autor.getFechaNacimiento(), autor.getFechaMuerte(), libros);
        });
    }

    public void listarLibrosPorIdioma(){
        System.out.println("""
                Ingrese el idioma para buscar los libros:
                es - Español
                en - Inglés
                fr - Francés
                pt - Portugés
                """);

        String idioma;

        while(true){
            idioma = scan.nextLine().trim();
            if (!idioma.isEmpty()){
                break;
            }
        }

        if (!idioma.equals("es") && !idioma.equals("en") && !idioma.equals("fr") && !idioma.equals("pt")){
            System.out.println("Idioma no valido");
            return;
        }



        List<Libro> libros = libroRepository.findByIdioma(idioma);


        String idiomaSeleccionado="";

        switch (idioma){
            case "es": idiomaSeleccionado ="Español"; break;
            case "en": idiomaSeleccionado="Inglés";break;
            case "fr": idiomaSeleccionado="Francés";break;
            case "pt": idiomaSeleccionado="Portugués";break;
        }

        if (libros.isEmpty()){
            System.out.println("No se encontraron libros con el idioma ingresado");
            return;
        }

        System.out.println("\n*********** LIBROS EN EL IDIOMA " + idiomaSeleccionado.toUpperCase() + " ***********\n");


        libros.forEach(l-> showLibro(l));

    }

    public void showLibro(Libro libro){
        System.out.printf("""
                ***** LIBRO *****
                Titulo: %s
                Autor: %s
                Idioma: %s
                Descargas: %d
                *****************
                
                """, libro.getTitulo(), libro.getAutor().getNombre(), libro.getIdioma(), libro.getDescargas());
    }
}
