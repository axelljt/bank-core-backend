package com.banco.banking.api.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.banco.banking.api.model.Movimiento;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {
    
    // Método extra útil: Buscar movimientos por cuenta
    List<Movimiento> findByCuentaId(Long cuentaId);
    
    // Método extra: Buscar movimientos por tipo (DEBITO/CREDITO)
    List<Movimiento> findByTipo(String tipo);
    
    @Query("SELECT SUM(m.monto) FROM Movimiento m WHERE m.cuenta.id = :cuentaId " +
            "AND m.tipo = :tipo AND m.fecha BETWEEN :inicio AND :fin")
     Double sumMontoByCuentaAndTipoAndFecha(
         @Param("cuentaId") Long cuentaId, 
         @Param("tipo") String tipo, 
         @Param("inicio") LocalDateTime inicio, 
         @Param("fin") LocalDateTime fin
     );
    
 // Busca movimientos de todas las cuentas pertenecientes a un cliente entre dos fechas
    @Query("SELECT m FROM Movimiento m WHERE m.cuenta.cliente.id = :clienteId " +
           "AND m.fecha BETWEEN :inicio AND :fin ORDER BY m.fecha DESC")
    List<Movimiento> findByClienteAndFechas(
        @Param("clienteId") Long clienteId, 
        @Param("inicio") LocalDateTime inicio, 
        @Param("fin") LocalDateTime fin
    );
    
    @Query("SELECT m FROM Movimiento m JOIN FETCH m.cuenta c JOIN FETCH c.cliente cl")
    List<Movimiento> findAllConDetalles();
}
