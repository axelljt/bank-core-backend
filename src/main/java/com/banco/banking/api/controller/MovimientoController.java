package com.banco.banking.api.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.banco.banking.api.dto.ReporteMovimientoDTO;
import com.banco.banking.api.model.Movimiento;
import com.banco.banking.api.service.MovimientoService;

import jakarta.validation.Valid;

/**
 * Controlador REST encargado de gestionar las operaciones de movimientos bancarios.
 * Proporciona endpoints para CRUD básico y la generación de reportes de estado de cuenta.
 * @author Axell
 * @version 1.0
 */
@RestController
@RequestMapping("/api/movimientos")
@CrossOrigin(origins = "http://localhost:4200")
public class MovimientoController {

    @Autowired
    private MovimientoService service;

    /**
     * Obtiene el listado completo de movimientos registrados en el sistema.
     * * @return {@link List} de todos los objetos {@link Movimiento}.
     */
    @GetMapping
    public List<Movimiento> getAll() {
        return service.listarTodos();
    }

    /**
     * Busca un movimiento específico mediante su identificador único.
     * * @param id Identificador único del movimiento.
     * @return {@link ResponseEntity} con el movimiento encontrado o 404 Not Found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Movimiento> getById(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Registra una nueva transacción (Depósito o Retiro) asociada a una cuenta.
     * Este método dispara la lógica de actualización de saldos y validación de cupos.
     * * @param cuentaId ID de la cuenta donde se aplicará el movimiento.
     * @param mov Objeto movimiento con los datos de la transacción.
     * @return {@link ResponseEntity} con el movimiento creado (201) o error de negocio (400).
     */
    @PostMapping("/cuenta/{cuentaId}")
    public ResponseEntity<?> create(@PathVariable Long cuentaId, @Valid @RequestBody Movimiento mov) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(service.registrarMovimiento(cuentaId, mov));
        } catch (RuntimeException e) {
            // Devuelve el mensaje de error definido en BancoMessages (ej: Saldo no disponible)
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Realiza una actualización parcial de los datos de un movimiento.
     * * @param id Identificador del movimiento a modificar.
     * @param updates Mapa que contiene los campos y valores a actualizar.
     * @return {@link ResponseEntity} con el movimiento actualizado o 404 si no existe.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Movimiento> put(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        try {
            return ResponseEntity.ok(service.actualizarMovimiento(id, updates));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Elimina un movimiento del sistema.
     * Nota: La implementación en el servicio revierte el saldo afectado en la cuenta.
     * * @param id Identificador del movimiento a eliminar.
     * @return {@link ResponseEntity} 204 No Content en éxito o 404 si falla.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            service.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Genera un reporte detallado de movimientos para un cliente específico dentro de un rango de fechas.
     * Útil para la funcionalidad de "Estado de Cuenta".
     * * @param clienteId ID del cliente propietario de las cuentas.
     * @param inicio Fecha inicial del rango (ISO DATE_TIME).
     * @param fin Fecha final del rango (ISO DATE_TIME).
     * @return {@link ResponseEntity} con la lista de {@link ReporteMovimientoDTO}.
     */
    @GetMapping("/reporte")
    public ResponseEntity<List<ReporteMovimientoDTO>> getReporte(
            @RequestParam Long clienteId,
            @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate inicio,
            @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fin) {
        
    	// Convertimos LocalDate a LocalDateTime para que el Service sea feliz
        LocalDateTime fechaInicio = inicio.atStartOfDay(); // 00:00:00
        LocalDateTime fechaFin = fin.atTime(LocalTime.MAX); // 23:59:59.999
        
        List<ReporteMovimientoDTO> reporte = service.generarReporte(clienteId, fechaInicio, fechaFin);
        return ResponseEntity.ok(reporte);
    }
}