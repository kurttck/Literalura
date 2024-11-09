package com.aluradesafio.literalura.service;

public interface IConvierteDatos {
    <T> T convertir(String json, Class<T> clase);
}
