package org.example.practica1.funko.repository;

import org.example.practica1.funko.models.Funko;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FunkoRepository extends JpaRepository<Funko, Long>{}
