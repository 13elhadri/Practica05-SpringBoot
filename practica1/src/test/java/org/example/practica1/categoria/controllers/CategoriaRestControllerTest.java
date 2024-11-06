package org.example.practica1.categoria.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.practica1.categoria.dto.CategoriaDto;
import org.example.practica1.categoria.exceptions.CategoriaExists;
import org.example.practica1.categoria.exceptions.CategoriaNotFound;
import org.example.practica1.categoria.mappers.CategoriaMapper;
import org.example.practica1.categoria.models.Categoria;
import org.example.practica1.categoria.service.CategoriaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
/*
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@ExtendWith(MockitoExtension.class)
class CategoriaRestControllerTest {

    private final String myEndpoint = "/v1/categorias";

    private final Categoria categoria1 = new Categoria(
            UUID.randomUUID(), Categoria.Nombre.SERIE, LocalDateTime.now(), LocalDateTime.now(), false);
    private final Categoria categoria2 = new Categoria(
            UUID.randomUUID(), Categoria.Nombre.DISNEY, LocalDateTime.now(), LocalDateTime.now(), false);

    private final ObjectMapper mapper= new ObjectMapper();

    private final CategoriaDto categoriaDto = new CategoriaDto(Categoria.Nombre.PELICULA.toString(), false);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoriaService categoriaService;

    @MockBean
    private CategoriaMapper categoriaMapper;

    @Autowired
    private JacksonTester<CategoriaDto> jsonCategoriaDto;

    @Autowired
    public CategoriaRestControllerTest(CategoriaService categoriasService) {
        this.categoriaService = categoriasService;
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    void getAllCategorias() throws Exception {
        var list = List.of(categoria1, categoria2);

        // Arrange
        when(categoriaService.getAll()).thenReturn(list);

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        List<Categoria> res = mapper.readValue(response.getContentAsString(),
                mapper.getTypeFactory().constructCollectionType(List.class, Categoria.class));

        // Assert
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(2, res.size()),
                () -> assertEquals(categoria1, res.get(0)),
                () -> assertEquals(categoria2, res.get(1))
        );

        // Verify
        verify(categoriaService, times(1)).getAll();


    }


    @Test
    void getCategoriaById() throws Exception {
        var myLocalEndpoint = myEndpoint + "/1";

        // Arrange
        when(categoriaService.getById(categoria1.getId())).thenReturn(categoria1);

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        get(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Categoria res = mapper.readValue(response.getContentAsString(), Categoria.class);

        // Assert
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(categoria1, res)
        );

        // Verify
        verify(categoriaService, times(1)).getById(categoria1.getId());
    }

    @Test
    void getCategoriaByIdNotFound() throws Exception {
        var myLocalEndpoint = myEndpoint + "/1";
        var id = UUID.randomUUID();

        // Arrange
        when(categoriaService.getById(id)).thenThrow(new CategoriaNotFound(id));

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        get(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();


        // Assert
        assertEquals(404, response.getStatus());

        // Verify
        verify(categoriaService, times(1)).getById(id);
    }

    @Test
    void createCategoria() throws Exception {
        var categoriaDto = new CategoriaDto("TEST", false);

        // Arrange
        when(categoriaService.create(any(Categoria.class))).thenReturn(categoria1);

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                // Le paso el body
                                .content(jsonCategoriaDto.write(categoriaDto).getJson())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Categoria res = mapper.readValue(response.getContentAsString(), Categoria.class);

        // Assert
        assertAll(
                () -> assertEquals(201, response.getStatus()),
                () -> assertEquals(categoria1, res)
        );

        // Verify
        verify(categoriaService, times(1)).create(any(Categoria.class));
    }

    @Test
    void createCategoriaWithBadRequest() throws Exception {
        var categoriaDto = new CategoriaDto("TE", false);


        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                // Le paso el body
                                .content(jsonCategoriaDto.write(categoriaDto).getJson())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // System.out.println(response.getContentAsString());

        // Assert
        assertAll(
                () -> assertEquals(404, response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("El nombre debe tener al menos 3 caracteres"))
        );
    }

    @Test
    void createCategoriaWithNombreExists() throws Exception {
        var categoriaDto = new CategoriaDto("TEST", false);

        // Arrange
        when(categoriaService.create(any(Categoria.class))).thenThrow(new CategoriaExists(Categoria.Nombre.valueOf(categoriaDto.nombre())));

        // Arrange
        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                // Le paso el body
                                .content(jsonCategoriaDto.write(categoriaDto).getJson())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // System.out.println(response.getContentAsString());

        // Assert
        assertAll(
                () -> assertEquals(404, response.getStatus())
        );
    }

    @Test
    void updateCategoria() throws Exception {
        var myLocalEndpoint = myEndpoint + "/1";
        var categoriaDto = new CategoriaDto("TEST", false);
        var id = UUID.randomUUID();

        // Arrange
        when(categoriaService.update(id, any(Categoria.class))).thenReturn(categoria1);

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        put(myLocalEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                // Le paso el body
                                .content(jsonCategoriaDto.write(categoriaDto).getJson())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Categoria res = mapper.readValue(response.getContentAsString(), Categoria.class);

        // Assert
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(categoria1, res)
        );

        // Verify
        verify(categoriaService, times(1)).update(id, any(Categoria.class));
    }

    @Test
    void updateCategoriaNotFound() throws Exception {
        var myLocalEndpoint = myEndpoint + "/1";
        var categoriaDto = new CategoriaDto("TEST", false);
        var id = UUID.randomUUID();

        // Arrange
        when(categoriaService.update(id, any(Categoria.class))).thenThrow(new CategoriaNotFound(id));

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        put(myLocalEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                // Le paso el body
                                .content(jsonCategoriaDto.write(categoriaDto).getJson())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Assert
        assertEquals(404, response.getStatus());
    }

    @Test
    void updateProductWithBadRequest() throws Exception {
        var myLocalEndpoint = myEndpoint + "/1";
        var categoriaDto = new CategoriaDto("TE", false);

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        put(myLocalEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                // Le paso el body
                                .content(jsonCategoriaDto.write(categoriaDto).getJson())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        System.out.println(response.getContentAsString());

        // Assert
        assertAll(
                () -> assertEquals(404, response.getStatus())
                // () -> assertTrue(response.getContentAsString().contains("El nombre debe tener al menos 3 caracteres"))
        );
    }

    @Test
    void updateCategoriaWithNombreExists() throws Exception {
        var myLocalEndpoint = myEndpoint + "/1";
        var categoriaDto = new CategoriaDto("TEST", false);
        var id = UUID.randomUUID();

        // Arrange
        when(categoriaService.update(id, any(Categoria.class))).thenThrow(new CategoriaExists(Categoria.Nombre.valueOf(categoriaDto.nombre())));

        // Arrange
        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        put(myLocalEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                // Le paso el body
                                .content(jsonCategoriaDto.write(categoriaDto).getJson())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // System.out.println(response.getContentAsString());

        // Assert
        assertAll(
                () -> assertEquals(404, response.getStatus())
                // () -> assertTrue(response.getContentAsString().contains("Ya existe una categoría con el nombre " + categoriaDto.getNombre()))
        );
    }

    @Test
    void deleteCategoria() throws Exception {

        var id = UUID.randomUUID();
        var myLocalEndpoint = myEndpoint + "/"+id;



        // Arrange
        doNothing().when(categoriaService).delete(id);

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        delete(myLocalEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Assert
        assertAll(
                () -> assertEquals(404, response.getStatus())
        );

        // Verify
        verify(categoriaService, times(1)).delete(id);
    }

    @Test
    void deleteCategoriaNotFound() throws Exception {
        var myLocalEndpoint = myEndpoint + "/1";
        var id = UUID.randomUUID();

        // Arrange
        doThrow(new CategoriaNotFound(id)).when(categoriaService).delete(id);

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        delete(myLocalEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Assert
        assertEquals(404, response.getStatus());

        // Verify
        verify(categoriaService, times(1)).delete(id);
    }
}

 */

