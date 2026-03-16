package com.banco.banking.api.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banco.banking.api.dto.CuentaClienteDTO;
import com.banco.banking.api.model.Cuenta;
import com.banco.banking.api.service.CuentaService;

import jakarta.validation.Valid;
/**
 * Controlador REST para la gestión de Cuentas en el sistema bancario.
 * Proporciona endpoints para realizar operaciones CRUD y consultas especializadas.
 * @author Axell
 * @version 1.0
 */
@RestController
@RequestMapping("/api/cuentas")
@CrossOrigin(origins = "http://localhost:4200")
public class CuentaController {

	@Autowired private CuentaService service;

    /**
     * Obtiene todas las cuentas registradas.
     * @return Lista de {@link Cuenta}.
     */
    @GetMapping
    public List<Cuenta> getAll() { 
        return service.listarTodas(); 
    }

    /**
     * Crea una cuenta genérica asociada a un cliente.
     */
    @PostMapping("/cliente/{clienteId}")
    public ResponseEntity<Cuenta> create(@PathVariable Long clienteId, @Valid @RequestBody Cuenta cuenta) {
        try {
            return ResponseEntity.ok(service.crearCuenta(clienteId, cuenta));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint especializado para crear cuentas de tipo CORRIENTE.
     */
    @PostMapping("/corriente/cliente/{clienteId}")
    public ResponseEntity<CuentaClienteDTO> crearCuentaCorriente(
            @PathVariable Long clienteId, 
            @RequestBody Cuenta nuevaCuenta) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(service.crearCuentaCorriente(clienteId, nuevaCuenta));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Actualización total de una cuenta.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Cuenta> update(@PathVariable Long id, @Valid @RequestBody Cuenta nueva) {
        try {
            return ResponseEntity.ok(service.actualizar(id, nueva));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Actualización parcial de campos de la cuenta (PATCH).
     * @param id ID de la cuenta.
     * @param updates Mapa de campos a modificar.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Cuenta> patch(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        try {
            return ResponseEntity.ok(service.actualizacionParcial(id, updates));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Elimina una cuenta por su ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
