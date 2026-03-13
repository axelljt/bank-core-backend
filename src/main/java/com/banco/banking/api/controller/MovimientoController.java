package com.banco.banking.api.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banco.banking.api.model.Movimiento;
import com.banco.banking.api.repository.CuentaRepository;
import com.banco.banking.api.repository.MovimientoRepository;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api/movimientos")
@CrossOrigin(origins = "*")
public class MovimientoController {

    @Autowired private MovimientoRepository movimientoRepo;
    @Autowired private CuentaRepository cuentaRepo;

    @GetMapping
    public List<Movimiento> getAll() { return movimientoRepo.findAll(); }

    // POST: Registrar movimiento y actualizar saldo de la cuenta
    @PostMapping("/cuenta/{cuentaId}")
    @Transactional // Importante para asegurar la integridad de la db
    public ResponseEntity<Movimiento> registrarMovimiento(@PathVariable Long cuentaId, @RequestBody Movimiento mov) {
        return cuentaRepo.findById(cuentaId).map(cuenta -> {
            
            // Lógica de saldo: Crédito suma, Débito resta
            if ("CREDITO".equalsIgnoreCase(mov.getTipo())) {
                cuenta.setSaldo(cuenta.getSaldo() + mov.getMonto());
            } else if ("DEBITO".equalsIgnoreCase(mov.getTipo())) {
                if (cuenta.getSaldo() < mov.getMonto()) {
                    throw new RuntimeException("Saldo insuficiente");
                }
                cuenta.setSaldo(cuenta.getSaldo() - mov.getMonto());
            }

            mov.setFecha(LocalDateTime.now());
            mov.setCuenta(cuenta);
            mov.setSaldoActual(cuenta.getSaldo()); // Guardamos el saldo que quedó tras el movimiento

            cuentaRepo.save(cuenta); // Actualiza la cuenta
            return ResponseEntity.ok(movimientoRepo.save(mov)); // Guarda el movimiento
        }).orElse(ResponseEntity.notFound().build());
    }
}
