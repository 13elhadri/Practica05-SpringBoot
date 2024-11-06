package org.example.practica1.funko.services;


import org.example.practica1.categoria.exceptions.CategoriaNotFound;
import org.example.practica1.categoria.models.Categoria;
import org.example.practica1.categoria.repository.CategoriaRepository;
import org.example.practica1.funko.dto.FunkoDto;
import org.example.practica1.funko.exceptions.FunkoNotFound;
import org.example.practica1.funko.mappers.Mapper;
import org.example.practica1.funko.repository.FunkoRepository;
import org.example.practica1.funko.models.Funko;
import org.example.practica1.storage.service.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@CacheConfig(cacheNames = {"funkos"})
@Service
public class FunkosServiceImpl implements FunkosService{

    private final Logger logger = LoggerFactory.getLogger(FunkosServiceImpl.class);

    private final FunkoRepository repository;
    private final CategoriaRepository categoriaRepository;
    private final StorageService storageService;
    private final Mapper mapper;

    @Autowired
    public FunkosServiceImpl(FunkoRepository repository, CategoriaRepository categoriaRepository, StorageService storageService, Mapper mapper) {
        this.repository = repository;
        this.categoriaRepository = categoriaRepository;
        this.storageService = storageService;
        this.mapper = mapper;
    }

    @Override
    public List<Funko> getAll() {
        return repository.findAll();
    }

    @Override
    //@Cacheable(key = "#id")
    public Funko getById(Long id) {
        return repository.findById(id).orElseThrow(()-> new FunkoNotFound(id));
    }

    @Override
    //@Cacheable(key = "#funko.id")
    public Funko create(FunkoDto funkoDto) {

        Categoria categoria = categoriaRepository.findById(funkoDto.categoriaId())
                .orElseThrow(() -> new CategoriaNotFound(funkoDto.categoriaId()));
        var funko = mapper.fromDto(funkoDto,categoria);

        return repository.save(funko);
    }

    @Override
    //@Cacheable(key = "#result.id")
    public Funko update(Long id, FunkoDto funkoDto) {

        Categoria categoria = categoriaRepository.findById(funkoDto.categoriaId())
                .orElseThrow(() -> new CategoriaNotFound(funkoDto.categoriaId()));

        var funko = mapper.fromDto(funkoDto,categoria);

        var res = repository.findById(id).orElseThrow(() -> new FunkoNotFound(id));
        res.setNombre(funko.getNombre());
        res.setPrecio(funko.getPrecio());
        res.setUpdatedAt(LocalDateTime.now());
        res.setCategoria(funko.getCategoria());
        res.setDescripcion(funko.getDescripcion());
        res.setImagen(funko.getImagen());
        return repository.save(res);
    }

    @Override
    public Funko updateImage(Long id, MultipartFile image) {
        var funko = this.getById(id);
        String imageStored = storageService.store(image);
        String imageUrl = imageStored;
        var funkoActualizado = new Funko(
                funko.getId(),
                funko.getNombre(),
                funko.getPrecio(),
                funko.getCategoria(),
                funko.getDescripcion(),
                imageUrl,
                funko.getCreatedAt(),
                LocalDateTime.now()
        );
        return repository.save(funkoActualizado);
    }

    @Override
    //@CacheEvict(key = "#id")
    public Funko delete(Long id) {
        Funko funko = repository.findById(id).orElseThrow(() -> new FunkoNotFound(id));
        repository.deleteById(id);
        if (funko.getImagen() != null && !funko.getImagen().equals(Funko.IMAGE_DEFAULT)) {
            storageService.delete(funko.getImagen());
        }
        return funko;
    }
}
