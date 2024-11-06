package org.example.practica1.categoria.mappers;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.*;

import org.example.practica1.categoria.dto.CategoriaDto;
import org.example.practica1.categoria.models.Categoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CategoriaMapperTest {

    private CategoriaMapper categoriaMapper;

    @BeforeEach
    void setUp() {
        categoriaMapper = new CategoriaMapper();
    }

    @Test
    void fromDto() {
        CategoriaDto categoriaDto = new CategoriaDto("DISNEY", false);

        Categoria categoria = categoriaMapper.fromDto(categoriaDto);

        assertNotNull(categoria);
        assertEquals(Categoria.Nombre.DISNEY, categoria.getNombre());
        assertFalse(categoria.getIsDeleted());
    }

    @Test
    void fromDtoInvalidNombre() {
        CategoriaDto categoriaDto = new CategoriaDto("INVALID_NAME", false);

        assertThrows(IllegalArgumentException.class, () -> categoriaMapper.fromDto(categoriaDto));
    }
}
