package org.example.practica1.categoria.service;

import static org.junit.jupiter.api.Assertions.*;


import org.example.practica1.categoria.exceptions.CategoriaExists;
import org.example.practica1.categoria.exceptions.CategoriaNotFound;
import org.example.practica1.categoria.models.Categoria;
import org.example.practica1.categoria.repository.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceImplTest {

    @Mock
    private CategoriaRepository repository;

    @InjectMocks
    private CategoriaServiceImpl service;

    private Categoria categoriaTest;

    @BeforeEach
    void setUp() {
        categoriaTest = Categoria.builder()
                .id(UUID.randomUUID())
                .nombre(Categoria.Nombre.DISNEY)
                .isDeleted(false)
                .build();
    }

    @Test
    void getAll() {
        when(repository.findAll()).thenReturn(List.of(categoriaTest));

        var result = service.getAll();

        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals(categoriaTest.getNombre(), result.get(0).getNombre()),
                () -> assertEquals(categoriaTest.getId(), result.get(0).getId())
        );

        verify(repository, times(1)).findAll();
    }

    @Test
    void getById() {
        when(repository.findById(categoriaTest.getId())).thenReturn(Optional.of(categoriaTest));

        var result = service.getById(categoriaTest.getId());

        assertAll(
                () -> assertEquals(categoriaTest.getId(), result.getId()),
                () -> assertEquals(categoriaTest.getNombre(), result.getNombre())
        );

        verify(repository, times(1)).findById(categoriaTest.getId());
    }

    @Test
    void getByIdNotFoundException() {
        when(repository.findById(categoriaTest.getId())).thenReturn(Optional.empty());

        var result = assertThrows(CategoriaNotFound.class, () -> service.getById(categoriaTest.getId()));

        assertEquals("Categoria con " + categoriaTest.getId() + " no encontrada", result.getMessage());

        verify(repository, times(1)).findById(categoriaTest.getId());
    }

    @Test
    void create() {
        when(repository.findByNombre(categoriaTest.getNombre())).thenReturn(Optional.empty());
        when(repository.save(categoriaTest)).thenReturn(categoriaTest);

        var result = service.create(categoriaTest);

        assertAll(
                () -> assertEquals(categoriaTest.getId(), result.getId()),
                () -> assertEquals(categoriaTest.getNombre(), result.getNombre())
        );

        verify(repository, times(1)).findByNombre(categoriaTest.getNombre());
        verify(repository, times(1)).save(categoriaTest);
    }

    @Test
    void createCategoriaExistsException() {
        when(repository.findByNombre(categoriaTest.getNombre())).thenReturn(Optional.of(categoriaTest));

        var result = assertThrows(CategoriaExists.class, () -> service.create(categoriaTest));

        assertEquals("Categoria con " + categoriaTest.getNombre() + " ya existente", result.getMessage());

        verify(repository, times(1)).findByNombre(categoriaTest.getNombre());
        verify(repository, times(0)).save(any());
    }

    @Test
    void update() {
        Categoria updatedCategoria = Categoria.builder()
                .id(categoriaTest.getId())
                .nombre(Categoria.Nombre.SUPERHEROES)
                .isDeleted(false)
                .build();

        when(repository.findById(categoriaTest.getId())).thenReturn(Optional.of(categoriaTest));
        when(repository.findByNombre(updatedCategoria.getNombre())).thenReturn(Optional.empty());
        when(repository.save(any(Categoria.class))).thenReturn(updatedCategoria);

        var result = service.update(categoriaTest.getId(), updatedCategoria);

        assertAll(
                () -> assertEquals(updatedCategoria.getId(), result.getId()),
                () -> assertEquals(updatedCategoria.getNombre(), result.getNombre())
        );

        verify(repository, times(1)).findById(categoriaTest.getId());
        verify(repository, times(1)).findByNombre(updatedCategoria.getNombre());
        verify(repository, times(1)).save(any(Categoria.class));
    }

    @Test
    void updateNotFoundException() {
        when(repository.findById(categoriaTest.getId())).thenReturn(Optional.empty());

        var result = assertThrows(CategoriaNotFound.class, () -> service.update(categoriaTest.getId(), categoriaTest));

        assertEquals("Categoria con " + categoriaTest.getId() + " no encontrada", result.getMessage());

        verify(repository, times(1)).findById(categoriaTest.getId());
        verify(repository, times(0)).save(any());
    }

    @Test
    void delete() {
        when(repository.findById(categoriaTest.getId())).thenReturn(Optional.of(categoriaTest));
        doNothing().when(repository).deleteById(categoriaTest.getId());

        service.delete(categoriaTest.getId());

        verify(repository, times(1)).findById(categoriaTest.getId());
        verify(repository, times(1)).deleteById(categoriaTest.getId());
    }

    @Test
    void deleteNotFoundException() {
        when(repository.findById(categoriaTest.getId())).thenReturn(Optional.empty());

        var result = assertThrows(CategoriaNotFound.class, () -> service.delete(categoriaTest.getId()));

        assertEquals("Categoria con " + categoriaTest.getId() + " no encontrada", result.getMessage());

        verify(repository, times(1)).findById(categoriaTest.getId());
        verify(repository, times(0)).deleteById(any());
    }
}
