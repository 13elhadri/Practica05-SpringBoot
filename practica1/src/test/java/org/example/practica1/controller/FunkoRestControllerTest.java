package org.example.practica1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.practica1.funko.dto.FunkoDto;
import org.example.practica1.funko.exceptions.FunkoNotFound;
import org.example.practica1.funko.mappers.Mapper;
import org.example.practica1.funko.models.Funko;
import org.example.practica1.funko.services.FunkosServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


/*
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class FunkoRestControllerTest {

    private final ObjectMapper mapper = new ObjectMapper();
    @MockBean
    FunkosServiceImpl service;
    @MockBean
    Mapper mapperFunkos;
    @Autowired
    MockMvc mockMvc;

    Funko funkoTest = new Funko();
    FunkoDto funkoDtoTest = new FunkoDto("test", 10, "test");


    @Value("${api.path:/api}/${api.version:/v1}/funkos")
    String myEndpoint;

    @Autowired
    public FunkoRestControllerTest( FunkosServiceImpl service, Mapper mapperFunkos) {
        this.service = service;
        this.mapperFunkos = mapperFunkos;
    }


    @BeforeEach
    void setUp() {
        funkoTest.setId(1L);
        funkoTest.setNombre("test");
        funkoTest.setPrecio(10);
        funkoTest.setCategoria("test");
        mapper.registerModule(new JavaTimeModule());

    }

    @Test
    void getFunkos() throws Exception{
        when(service.getAll()).thenReturn(List.of(funkoTest));

        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        List<Funko> res = mapper.readValue(response.getContentAsString(),
                mapper.getTypeFactory().constructCollectionType(List.class, Funko.class));

        assertEquals(response.getStatus(), HttpStatus.OK.value());

        verify(service, times(1)).getAll();
    }

    @Test
    void getByIdFunkoTest() throws Exception {
        when(service.getById(1L)).thenReturn(funkoTest);

        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint + "/1")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Funko res = mapper.readValue(response.getContentAsString(), Funko.class);

        assertAll(
                () -> assertEquals(response.getStatus(), HttpStatus.OK.value()),
                () -> assertEquals(res.getId(), funkoTest.getId()),
                () -> assertEquals(res.getNombre(), funkoTest.getNombre()),
                () -> assertEquals(res.getPrecio(), funkoTest.getPrecio()),
                () -> assertEquals(res.getCategoria(), funkoTest.getCategoria())
        );

        verify(service, times(1)).getById(1L);
    }

    @Test
    void getByIdFunkoNotFoundTest() throws Exception {
        when(service.getById(1L)).thenThrow(new FunkoNotFound(1L));

        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint + "/1")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(response.getStatus(), HttpStatus.NOT_FOUND.value());

        verify(service, times(1)).getById(1L);
    }

    @Test
    void createFunkoTest() throws Exception {
        when(service.create(funkoTest)).thenReturn(funkoTest);
        when(mapperFunkos.fromDto(funkoDtoTest)).thenReturn(funkoTest);

        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(funkoDtoTest))  // Utilizando la instancia inicializada
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Funko res = mapper.readValue(response.getContentAsString(), Funko.class);

        assertAll(
                () -> assertEquals(HttpStatus.CREATED.value(), response.getStatus()),
                () -> assertEquals(funkoTest.getId(), res.getId())
        );

        verify(service, times(1)).create(funkoTest);
        verify(mapperFunkos, times(1)).fromDto(funkoDtoTest);
    }




    @Test
    void updateFunkoTest() throws Exception {
        FunkoDto funkoDtoTest = new FunkoDto("updatedFunko", 50, "categoria");

        when(service.update(1L, funkoTest)).thenReturn(funkoTest);
        when(mapperFunkos.fromDto(funkoDtoTest)).thenReturn(funkoTest);

        MockHttpServletResponse response = mockMvc.perform(
                        put(myEndpoint + "/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(funkoDtoTest))
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Funko res = mapper.readValue(response.getContentAsString(), Funko.class);

        assertAll(
                () -> assertEquals(response.getStatus(), HttpStatus.OK.value()),
                () -> assertEquals(res.getId(), funkoTest.getId())
        );

        verify(service, times(1)).update(1L, funkoTest);
        verify(mapperFunkos, times(1)).fromDto(funkoDtoTest);
    }

    @Test
    void updateFunkoNotFoundTest() throws Exception {
        when(service.update(1L, funkoTest)).thenThrow(new FunkoNotFound(1L));
        when(mapperFunkos.fromDto(funkoDtoTest)).thenReturn(funkoTest);

        MockHttpServletResponse response = mockMvc.perform(
                        put(myEndpoint + "/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(funkoDtoTest))
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(response.getStatus(), HttpStatus.NOT_FOUND.value());

        verify(service, times(1)).update(1L, funkoTest);
        verify(mapperFunkos, times(1)).fromDto(funkoDtoTest);
    }


    @Test
    void deleteFunkoTest() throws Exception {
        when(service.delete(1L)).thenReturn(funkoTest);

        MockHttpServletResponse response = mockMvc.perform(
                        delete(myEndpoint + "/1")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(response.getStatus(), HttpStatus.NO_CONTENT.value());

        verify(service, times(1)).delete(1L);
    }

    @Test
    void deleteFunkoNotFoundTest() throws Exception {
        when(service.delete(1L)).thenThrow(new FunkoNotFound(1L));

        MockHttpServletResponse response = mockMvc.perform(
                        delete(myEndpoint + "/1")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(response.getStatus(), HttpStatus.NOT_FOUND.value());

        verify(service, times(1)).delete(1L);
    }

}
*/
