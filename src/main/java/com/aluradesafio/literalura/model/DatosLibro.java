package com.aluradesafio.literalura.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DatosLibro(
        @JsonAlias("results") List<Results> results
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Results(
            @JsonAlias("title") String title,
            @JsonAlias("authors") List<Author> authors,
            @JsonAlias("languages") List<String> languages,
            @JsonAlias("download_count") Integer download_count

    ){}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Author(
            @JsonAlias("name") String name,
            @JsonAlias("birth_year") String birth_year,
            @JsonAlias("death_year") String death_year
    ){}
}
