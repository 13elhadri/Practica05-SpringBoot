package org.example.practica1.funko.controller;

import jakarta.validation.Valid;
import org.example.practica1.funko.dto.FunkoDto;
import org.example.practica1.funko.mappers.Mapper;
import org.example.practica1.funko.models.Funko;
import org.example.practica1.funko.services.FunkosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.path:/api}/${api.version:/v1}/funkos")
public class FunkoRestController {

    private final FunkosService service;

    @Autowired
    public FunkoRestController(FunkosService service) {
        this.service = service;
    }


    @GetMapping
    public ResponseEntity<List<Funko>> getFunkos() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("{id}")
    public ResponseEntity<Funko> getFunko(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<Funko> createFunko(@Valid @RequestBody FunkoDto funkoDto) {
        Funko createdFunko = service.create(funkoDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFunko);
    }

    @PatchMapping(value = "/imagen/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Funko> nuevoProducto(
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file) {

        if (!file.isEmpty()) {

            return ResponseEntity.ok(service.updateImage(id, file));

        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se ha enviado una imagen para el producto o esta está vacía");
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<Funko> updateFunko(@PathVariable Long id, @RequestBody FunkoDto funko) {
        var result= service.update(id,funko);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteFunko(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
