package com.banco.banking.api.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.banco.banking.api.model.Cuenta;

//CuentaRepository
public interface CuentaRepository extends JpaRepository<Cuenta, Long> {
	 List<Cuenta> findByClienteId(Long clienteId);
	}
