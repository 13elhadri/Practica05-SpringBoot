package org.example.practica1.categoria.controllers;


import jakarta.validation.Valid;
import org.example.practica1.categoria.dto.CategoriaDto;
import org.example.practica1.categoria.mappers.CategoriaMapper;
import org.example.practica1.categoria.models.Categoria;
import org.example.practica1.categoria.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("${api.path:/api}/${api.version:/v1}/categorias")
public class CategoriaRestController {

    private final CategoriaService service;
    private final CategoriaMapper mapper;

    @Autowired
    public CategoriaRestController(CategoriaService service, CategoriaMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<List<Categoria>> getCategorias(){return ResponseEntity.ok(service.getAll());}

    @GetMapping("{id}")
    public ResponseEntity<Categoria> getCategoria(@PathVariable UUID id){return ResponseEntity.ok(service.getById(id));}

    @PostMapping
    public ResponseEntity<Categoria> createCategoria(@Valid @RequestBody CategoriaDto categoria){
        var result = service.create(mapper.fromDto(categoria));
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("{id}")
    public ResponseEntity<Categoria> updateCategoria(@PathVariable UUID id, @RequestBody CategoriaDto categoria){

        var result= service.update(id,mapper.fromDto(categoria));

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteCategoria(@PathVariable UUID id){
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
