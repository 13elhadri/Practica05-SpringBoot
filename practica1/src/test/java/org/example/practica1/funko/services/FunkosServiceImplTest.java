package org.example.practica1.funko.services;

import org.example.practica1.categoria.exceptions.CategoriaNotFound;
import org.example.practica1.categoria.models.Categoria;
import org.example.practica1.categoria.repository.CategoriaRepository;
import org.example.practica1.descripcion.models.Descripcion;
import org.example.practica1.funko.dto.FunkoDto;
import org.example.practica1.funko.exceptions.FunkoNotFound;
import org.example.practica1.funko.mappers.Mapper;
import org.example.practica1.funko.models.Funko;
import org.example.practica1.funko.repository.FunkoRepository;
import org.example.practica1.storage.service.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FunkosServiceImplTest {

    @Mock
    private FunkoRepository repository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private StorageService storageService;

    @Mock
    private Mapper mapper;

    @InjectMocks
    private FunkosServiceImpl service;

    private Funko funkoTest;
    private Categoria categoriaTest;

    @BeforeEach
    void setUp() {
        categoriaTest = Categoria.builder()
                .id(UUID.randomUUID())
                .nombre(Categoria.Nombre.DISNEY)
                .isDeleted(false)
                .build();

        funkoTest = Funko.builder()
                .id(1L)
                .nombre("Funko Test")
                .precio(15)
                .categoria(categoriaTest)
                .descripcion(new Descripcion("Test Description"))
                .imagen("test.jpg")
                .build();
    }

    @Test
    void getAll() {
        when(repository.findAll()).thenReturn(List.of(funkoTest));
        List<Funko> result = service.getAll();
        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals(funkoTest.getNombre(), result.get(0).getNombre()),
                () -> assertEquals(funkoTest.getId(), result.get(0).getId())
        );
        verify(repository, times(1)).findAll();
    }

    @Test
    void getById() {
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.of(funkoTest));
        Funko result = service.getById(id);
        assertAll(
                () -> assertEquals(funkoTest.getId(), result.getId()),
                () -> assertEquals(funkoTest.getNombre(), result.getNombre())
        );
        verify(repository, times(1)).findById(id);
    }

    @Test
    void getByIdNotFoundException() {
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.empty());
        var exception = assertThrows(FunkoNotFound.class, () -> service.getById(id));
        assertEquals("Funko con " + id + " no encontrado", exception.getMessage());
        verify(repository, times(1)).findById(id);
    }

    @Test
    void create() {
        FunkoDto funkoDto = new FunkoDto("Funko Test", 20, categoriaTest.getId(), "Test Description", "test.jpg");
        when(categoriaRepository.findById(funkoDto.categoriaId())).thenReturn(Optional.of(categoriaTest));
        when(mapper.fromDto(funkoDto, categoriaTest)).thenReturn(funkoTest);
        when(repository.save(funkoTest)).thenReturn(funkoTest);
        Funko result = service.create(funkoDto);
        assertAll(
                () -> assertEquals(funkoTest.getId(), result.getId()),
                () -> assertEquals(funkoDto.nombre(), result.getNombre()),
                () -> assertEquals(funkoDto.imagen(), result.getImagen())
        );
        verify(categoriaRepository, times(1)).findById(funkoDto.categoriaId());
        verify(repository, times(1)).save(funkoTest);
    }

    @Test
    void createCategoriaNotFound() {
        FunkoDto funkoDto = new FunkoDto("Funko Test", 20, UUID.randomUUID(), "Test Description", "testImage.jpg");
        when(categoriaRepository.findById(funkoDto.categoriaId())).thenReturn(Optional.empty());
        var exception = assertThrows(CategoriaNotFound.class, () -> service.create(funkoDto));
        assertEquals("Categoria con " + funkoDto.categoriaId() + " no encontrada", exception.getMessage());
        verify(repository, times(0)).save(any());
    }

    @Test
    void update() {
        FunkoDto funkoDto = new FunkoDto("Funko Test", 25, categoriaTest.getId(), "Updated Description", "test.jpg");
        when(repository.findById(funkoTest.getId())).thenReturn(Optional.of(funkoTest));
        when(categoriaRepository.findById(funkoDto.categoriaId())).thenReturn(Optional.of(categoriaTest));
        when(mapper.fromDto(funkoDto, categoriaTest)).thenReturn(funkoTest);
        when(repository.save(funkoTest)).thenReturn(funkoTest);
        Funko result = service.update(funkoTest.getId(), funkoDto);
        assertAll(
                () -> assertEquals(funkoTest.getId(), result.getId()),
                () -> assertEquals(funkoDto.nombre(), result.getNombre()),
                () -> assertEquals(funkoDto.imagen(), result.getImagen())
        );
        verify(repository, times(1)).findById(funkoTest.getId());
        verify(categoriaRepository, times(1)).findById(funkoDto.categoriaId());
        verify(repository, times(1)).save(funkoTest);
    }

    @Test
    void updateCategoriaNotFound() {
        FunkoDto funkoDto = new FunkoDto("Funko Updated", 25, UUID.randomUUID(), "Updated Description", "updatedImage.jpg");
        when(categoriaRepository.findById(funkoDto.categoriaId())).thenReturn(Optional.empty());
        var exception = assertThrows(CategoriaNotFound.class, () -> service.update(funkoTest.getId(), funkoDto));
        assertEquals("Categoria con " + funkoDto.categoriaId() + " no encontrada", exception.getMessage());
        verify(repository, times(0)).save(any());
    }

    /*
    @Test
    void updateImage() {
        Long id = funkoTest.getId();
        MultipartFile newImage = mock(MultipartFile.class);
        when(repository.findById(id)).thenReturn(Optional.of(funkoTest));
        when(storageService.store(newImage)).thenReturn("newImage.jpg");
        Funko result = service.updateImage(id, newImage);
        assertAll(
                () -> assertEquals(funkoTest.getId(), result.getId()),
                () -> assertEquals("newImage.jpg", result.getImagen())
        );
        verify(repository, times(1)).findById(id);
        verify(storageService, times(1)).store(newImage);
        verify(repository, times(1)).save(result);
    }

     */

    @Test
    void delete() {
        when(repository.findById(funkoTest.getId())).thenReturn(Optional.of(funkoTest));
        doNothing().when(repository).deleteById(funkoTest.getId());
        doNothing().when(storageService).delete(funkoTest.getImagen());
        Funko result = service.delete(funkoTest.getId());
        assertEquals(funkoTest.getId(), result.getId());
        verify(repository, times(1)).findById(funkoTest.getId());
        verify(repository, times(1)).deleteById(funkoTest.getId());
        verify(storageService, times(1)).delete(funkoTest.getImagen());
    }

    @Test
    void deleteNotFound() {
        when(repository.findById(funkoTest.getId())).thenReturn(Optional.empty());
        var exception = assertThrows(FunkoNotFound.class, () -> service.delete(funkoTest.getId()));
        assertEquals("Funko con " + funkoTest.getId() + " no encontrado", exception.getMessage());
        verify(repository, times(1)).findById(funkoTest.getId());
        verify(repository, times(0)).deleteById(any());
        verify(storageService, times(0)).delete(any());
    }
}

