package com.banco.banking.api.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banco.banking.api.exceptions.SaldoInsuficienteException;
import com.banco.banking.api.model.Movimiento;
import com.banco.banking.api.repository.CuentaRepository;
import com.banco.banking.api.repository.MovimientoRepository;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api/movimientos")
public class MovimientoController {

    @Autowired private MovimientoRepository movRepo;
    @Autowired private CuentaRepository cuentaRepo;

    @PostMapping("/cuenta/{id}")
    @Transactional
    public ResponseEntity<?> registrar(@PathVariable Long id, @RequestBody Movimiento mov) {
        return cuentaRepo.findById(id).map(cuenta -> {
            
            // 1. Validar si la cuenta está activa (Regla extra de seguridad)
            if (!cuenta.isEstado()) {
                return ResponseEntity.badRequest().body("La cuenta está inactiva.");
            }

            double nuevoSaldo = cuenta.getSaldo();

            // 2. Lógica de Negocio según tus imágenes
            if ("Retiro".equalsIgnoreCase(mov.getTipo())) {
                if (cuenta.getSaldo() < mov.getMonto()) {
                    // Lanzamos la excepción si el saldo no alcanza
                    throw new SaldoInsuficienteException("Saldo no disponible");
                }
                nuevoSaldo -= mov.getMonto();
            } 
            else if ("Deposito".equalsIgnoreCase(mov.getTipo())) {
                nuevoSaldo += mov.getMonto();
            }

            // 3. Actualizar y Guardar
            cuenta.setSaldo(nuevoSaldo);
            cuentaRepo.save(cuenta);

            mov.setCuenta(cuenta);
            mov.setFecha(LocalDateTime.now());
            mov.setSaldoActual(nuevoSaldo);
            
            return ResponseEntity.ok(movRepo.save(mov));

        }).orElse(ResponseEntity.notFound().build());
    }
}
