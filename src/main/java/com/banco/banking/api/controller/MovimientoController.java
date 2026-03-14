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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.banco.banking.api.dto.ReporteMovimientoDTO;
import com.banco.banking.api.model.Movimiento;
import com.banco.banking.api.service.MovimientoService;

@RestController
@RequestMapping("/movimientos")
@CrossOrigin(origins = "http://localhost:4200") // Para permitir la conexión con Angular
public class MovimientoController {

    @Autowired
    private MovimientoService movimientoService;

    /**
     * Registro de un nuevo movimiento (Depósito o Retiro)
     * POST /movimientos?cuentaId=1
     */
    @PostMapping
    public ResponseEntity<Movimiento> crearMovimiento(
            @RequestParam Long cuentaId, 
            @RequestBody Movimiento movimiento) {
        
        Movimiento nuevoMovimiento = movimientoService.registrarMovimiento(cuentaId, movimiento);
        return new ResponseEntity<>(nuevoMovimiento, HttpStatus.CREATED);
    }

    /**
     * Generación de reporte de estado de cuenta
     * GET /movimientos/reporte?clienteId=1&inicio=01-03-2024&fin=31-03-2024
     */
    @GetMapping("/reporte")
    public ResponseEntity<?> obtenerReporte(
            @RequestParam Long clienteId,
            @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate inicio,
            @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fin) {

        LocalDateTime fechaInicio = inicio.atStartOfDay();
        LocalDateTime fechaFin = fin.atTime(LocalTime.MAX);

        List<ReporteMovimientoDTO> listaReporte = movimientoService.generarReporte(clienteId, fechaInicio, fechaFin);
        
        if (listaReporte.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        // Generamos el Base64
        String base64Reporte = movimientoService.generarReporteBase64(listaReporte);

        // Devolvemos ambos formatos en un Map o DTO de respuesta
        return ResponseEntity.ok(Map.of(
            "reporteJson", listaReporte,
            "reporteBase64", base64Reporte
        ));
    }
}