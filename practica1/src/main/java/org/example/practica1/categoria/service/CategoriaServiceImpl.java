package org.example.practica1.categoria.service;

import lombok.extern.slf4j.Slf4j;
import org.example.practica1.categoria.exceptions.CategoriaExists;
import org.example.practica1.categoria.exceptions.CategoriaNotFound;
import org.example.practica1.categoria.models.Categoria;
import org.example.practica1.categoria.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;


@CacheConfig(cacheNames = {"categoria"})
@Service
@Slf4j
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository repository;

    @Autowired
    public CategoriaServiceImpl(CategoriaRepository repository) {
        this.repository = repository;
    }


    @Override
    public List<Categoria> getAll() {
        log.info("Obteniendo todas las categorías desde la base de datos");
        return repository.findAll();
    }

    @Override
    @Cacheable(key = "#id")
    public Categoria getById(UUID id) {
        log.info("Obteniendo la categoría con ID: {}", id);
        return repository.findById(id).orElseThrow(() -> new CategoriaNotFound(id));
    }

    @Override
    @CachePut(key = "#result.id")
    public Categoria create(Categoria categoria) {
        log.info("Creando nueva categoría: {}", categoria);
        repository.findByNombre(categoria.getNombre())
                .ifPresent(existingCategoria -> {
                    throw new CategoriaExists(categoria.getNombre());
                });
        return repository.save(categoria);
    }

    @Override
    @CachePut(key = "#id")
    public Categoria update(UUID id, Categoria categoria) {
        log.info("Actualizando la categoría con ID: {}", id);
        var res = repository.findById(id).orElseThrow(() -> new CategoriaNotFound(id));
        repository.findByNombre(categoria.getNombre())
                .ifPresent(existingCategoria -> {
                    throw new CategoriaExists(categoria.getNombre());
                });
        res.setNombre(categoria.getNombre());
        res.setUpdatedAt(LocalDateTime.now());
        res.setIsDeleted(categoria.getIsDeleted());
        return repository.save(res);
    }

    @Override
    @CacheEvict(key = "#id")
    public void delete(UUID id) {
        log.info("Eliminando la categoría con ID: {}", id);
        Categoria categoria = repository.findById(id).orElseThrow(() -> new CategoriaNotFound(id));
        categoria.setIsDeleted(true);
        repository.deleteById(id);
    }
}

