package com.banco.banking.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banco.banking.api.model.Cliente;

//ClienteRepository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {}


