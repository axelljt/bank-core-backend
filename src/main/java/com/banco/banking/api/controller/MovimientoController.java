package com.banco.banking.api.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.banco.banking.api.dto.CuentaClienteDTO;
import com.banco.banking.api.dto.EstadoCuentaDTO;
import com.banco.banking.api.dto.ReporteMovimientoDTO;
import com.banco.banking.api.enums.TipoMovimientoEnum;
import com.banco.banking.api.exceptions.SaldoInsuficienteException;
import com.banco.banking.api.model.Movimiento;
import com.banco.banking.api.repository.CuentaRepository;
import com.banco.banking.api.repository.MovimientoRepository;
import com.banco.banking.api.utils.BancoMessages;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/movimientos")
@CrossOrigin(origins = "http://localhost:4200")
public class MovimientoController {

    @Autowired private MovimientoRepository movRepo;
    @Autowired private CuentaRepository cuentaRepo;

    
    @GetMapping
    public List<Movimiento> getAll() { return movRepo.findAll();}
    
    
    @GetMapping("/clientes-reporte")
    public ResponseEntity<List<CuentaClienteDTO>> obtenerReporteClientes() {
        
        // Obtenemos todos los movimientos registrados
        List<Movimiento> movimientos = movRepo.findAllConDetalles();

        List<CuentaClienteDTO> lista = movimientos.stream().map(m -> {
            // Cálculo del saldo inicial: Saldo Final - (Monto con su signo)
            double montoConSigno = m.getTipo().equalsIgnoreCase("Retiro") ? -m.getMonto() : m.getMonto();
            double saldoPrevio = m.getSaldoActual() - montoConSigno;
            
            // Obtenemos el nombre del cliente desde la relación jerárquica
            String nombreCompleto = m.getCuenta().getCliente().getNombre() + " " + 
                                   m.getCuenta().getCliente().getApellido();

            return new CuentaClienteDTO(
                m.getCuenta().getNumeroCuenta(),
                m.getCuenta().getTipoCuenta(),
                saldoPrevio,
                m.getCuenta().getEstado(),
                nombreCompleto
            );
        }).collect(Collectors.toList());

        return ResponseEntity.ok(lista);
    }
    
    @GetMapping("/lista-movimientos")
    public ResponseEntity<List<EstadoCuentaDTO>> obtenerListaCompleta() {
        
        // Traemos todos los movimientos sin filtros
        List<Movimiento> movimientos = movRepo.findAll();

        List<EstadoCuentaDTO> reporte = movimientos.stream().map(m -> {
            // Cálculo del saldo previo: Saldo Final - Monto (si fue depósito) o Saldo Final + Monto (si fue retiro)
            double montoMovimiento = m.getTipo().equalsIgnoreCase("Retiro") ? -m.getMonto() : m.getMonto();
            double saldoInicial = m.getSaldoActual() - montoMovimiento;
            
            return new EstadoCuentaDTO(
                m.getCuenta().getNumeroCuenta(),
                m.getCuenta().getTipoCuenta(),
                saldoInicial,
                m.getCuenta().getEstado(),
                //montoMovimiento
                m.getTipo()
            );
        }).collect(Collectors.toList());

        return ResponseEntity.ok(reporte);
    }
    
    @GetMapping("/reporte")
    public ResponseEntity<List<ReporteMovimientoDTO>> obtenerReporte(
            @RequestParam Long clienteId,
            @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate inicio,
            @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fin) {
        
        LocalDateTime fechaInicio = inicio.atStartOfDay();
        LocalDateTime fechaFin = fin.atTime(LocalTime.MAX);

        // Obtenemos los movimientos originales
        List<Movimiento> movimientos = movRepo.findByClienteAndFechas(clienteId, fechaInicio, fechaFin);

        // Los transformamos al DTO con la información extra
        List<ReporteMovimientoDTO> reporte = movimientos.stream().map(m -> new ReporteMovimientoDTO(
                m.getFecha(),
                m.getCuenta().getCliente().getNombre() + " " + m.getCuenta().getCliente().getApellido(),
                m.getCuenta().getNumeroCuenta(),
                m.getTipo(),
                m.getMonto(),
                m.getSaldoActual()
        )).collect(Collectors.toList());

        return ResponseEntity.ok(reporte);
    }
    
    @PostMapping("/cuenta/{id}")
    @Transactional
    public ResponseEntity<?> registrar(@PathVariable Long id, @Valid @RequestBody Movimiento mov) {
        return cuentaRepo.findById(id).map(cuenta -> {
            
            // 1. Validación de estado
            if (!cuenta.getEstado()) {
                return ResponseEntity.badRequest().body(BancoMessages.CUENTA_INACTIVA);
            }

            // 2. Convertir el String que viene del JSON a nuestro Enum
            TipoMovimientoEnum tipo = TipoMovimientoEnum.valueOf(mov.getTipo().toUpperCase());

            // 3. Lógica para Retiros
            if (tipo == TipoMovimientoEnum.RETIRO) {
                // Regla: Saldo disponible
                if (cuenta.getSaldo() < mov.getMonto()) {
                    throw new SaldoInsuficienteException(BancoMessages.SALDO_NO_DISPONIBLE);
                }

                // Regla: Cupo Diario
                Double totalHoy = movRepo.sumMontoByCuentaAndTipoAndFecha(
                    id, tipo.getDescripcion(), LocalDate.now().atStartOfDay(), LocalDateTime.now());
                
                if (((totalHoy != null ? totalHoy : 0) + mov.getMonto()) > BancoMessages.LIMITE_DIARIO) {
                    return ResponseEntity.badRequest().body(BancoMessages.CUPO_EXCEDIDO);
                }
            }

            // 4. Actualización de saldo genérica (Suma o Resta según el Enum)
            double nuevoSaldo = cuenta.getSaldo() + (mov.getMonto() * tipo.getFactor());
            
            cuenta.setSaldo(nuevoSaldo);
            cuentaRepo.save(cuenta);

            mov.setCuenta(cuenta);
            mov.setFecha(LocalDateTime.now());
            mov.setSaldoActual(nuevoSaldo);
            
            return ResponseEntity.ok(movRepo.save(mov));

        }).orElse(ResponseEntity.notFound().build());
    }
}
