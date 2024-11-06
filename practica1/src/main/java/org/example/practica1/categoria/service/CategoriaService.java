package org.example.practica1.categoria.service;

import org.example.practica1.categoria.models.Categoria;

import java.util.List;
import java.util.UUID;

public interface CategoriaService {

    List<Categoria> getAll();

    Categoria getById(UUID id);

    Categoria create(Categoria categoria);

    Categoria update(UUID id, Categoria categoria);

    void delete(UUID id);
}
