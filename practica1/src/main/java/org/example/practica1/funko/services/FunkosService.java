package org.example.practica1.funko.services;

import org.example.practica1.funko.dto.FunkoDto;
import org.example.practica1.funko.models.Funko;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FunkosService {

    List<Funko> getAll();

    Funko getById(Long id);

    Funko create(FunkoDto funkoDto);

    Funko update(Long id, FunkoDto funkoDto);

    Funko updateImage(Long id, MultipartFile image);

    Funko delete(Long id);

}
