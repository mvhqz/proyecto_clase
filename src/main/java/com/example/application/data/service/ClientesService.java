package com.example.application.data.service;

import com.example.application.data.entity.Clientes;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ClientesService {

    private final ClientesRepository repository;

    public ClientesService(ClientesRepository repository) {
        this.repository = repository;
    }

    public Optional<Clientes> get(Long id) {
        return repository.findById(id);
    }

    public Clientes update(Clientes entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Clientes> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Clientes> list(Pageable pageable, Specification<Clientes> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
