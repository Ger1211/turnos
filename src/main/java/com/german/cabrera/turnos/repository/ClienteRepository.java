package com.german.cabrera.turnos.repository;

import com.german.cabrera.turnos.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
