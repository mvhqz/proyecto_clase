package com.example.application.data.service;

import com.example.application.data.entity.Clientes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ClientesRepository extends JpaRepository<Clientes, Long>, JpaSpecificationExecutor<Clientes> {

}
