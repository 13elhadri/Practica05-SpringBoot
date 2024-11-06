package org.example.practica1.funko.mappers;

import static org.junit.jupiter.api.Assertions.*;


import org.example.practica1.categoria.models.Categoria;
import org.example.practica1.funko.dto.FunkoDto;
import org.example.practica1.funko.models.Funko;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class MapperTest {

    private Mapper mapper;
    private Categoria categoriaTest;

    @BeforeEach
    void setUp() {
        mapper = new Mapper();

        categoriaTest = Categoria.builder()
                .id(UUID.randomUUID())
                .nombre(Categoria.Nombre.DISNEY)
                .isDeleted(false)
                .build();
    }

    @Test
    void fromDto() {
        FunkoDto funkoDto = new FunkoDto("Funko Test", 20, categoriaTest.getId(), "Test Description", "test.jpg");

        Funko funko = mapper.fromDto(funkoDto, categoriaTest);


        assertNotNull(funko);
        assertEquals(funkoDto.nombre(), funko.getNombre());
        assertEquals(funkoDto.precio(), funko.getPrecio());
        assertEquals(categoriaTest, funko.getCategoria());
        assertEquals("Test Description", funko.getDescripcion().getDescripcion());
        assertEquals("test.jpg", funko.getImagen());
    }

    @Test
    void fromDtoImageNull() {
        FunkoDto funkoDto = new FunkoDto("Funko Test", 20, categoriaTest.getId(), "Test Description", null);

        Funko funko = mapper.fromDto(funkoDto, categoriaTest);

        assertNotNull(funko);
        assertEquals(Funko.IMAGE_DEFAULT, funko.getImagen());  // Verifica que se use la imagen por defecto
    }
}
