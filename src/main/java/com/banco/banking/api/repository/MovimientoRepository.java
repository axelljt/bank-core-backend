package com.banco.banking.api.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.banco.banking.api.model.Movimiento;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {
    
    // Método extra útil: Buscar movimientos por cuenta
    List<Movimiento> findByCuentaId(Long cuentaId);
    
    // Método extra: Buscar movimientos por tipo (DEBITO/CREDITO)
    List<Movimiento> findByTipo(String tipo);
}
