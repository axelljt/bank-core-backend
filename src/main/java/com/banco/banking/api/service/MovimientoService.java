package com.banco.banking.api.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.banco.banking.api.dto.EstadoCuentaDTO;
import com.banco.banking.api.dto.ReporteMovimientoDTO;
import com.banco.banking.api.enums.TipoMovimientoEnum;
import com.banco.banking.api.exceptions.SaldoInsuficienteException;
import com.banco.banking.api.model.Cuenta;
import com.banco.banking.api.model.Movimiento;
import com.banco.banking.api.repository.CuentaRepository;
import com.banco.banking.api.repository.MovimientoRepository;
import com.banco.banking.api.utils.BancoMessages;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.transaction.Transactional;
@Service
public class MovimientoService {

    @Autowired
    private MovimientoRepository movRepo;

    @Autowired
    private CuentaRepository cuentaRepo;

    @Transactional
    public Movimiento registrarMovimiento(Long cuentaId, Movimiento mov) {
        Cuenta cuenta = cuentaRepo.findById(cuentaId)
                .orElseThrow(() -> new RuntimeException(BancoMessages.CUENTA_INACTIVA));

        if (!cuenta.getEstado()) {
            throw new RuntimeException(BancoMessages.CUENTA_INACTIVA);
        }

        TipoMovimientoEnum tipo = TipoMovimientoEnum.valueOf(mov.getTipo().toUpperCase());

        if (tipo == TipoMovimientoEnum.RETIRO) {
            validarLimitesRetiro(cuenta, mov.getMonto());
        }

        double nuevoSaldo = cuenta.getSaldo() + (mov.getMonto() * tipo.getFactor());

        cuenta.setSaldo(nuevoSaldo);
        cuentaRepo.save(cuenta);

        mov.setCuenta(cuenta);
        mov.setFecha(LocalDateTime.now());
        mov.setSaldoActual(nuevoSaldo);
        
        return movRepo.save(mov);
    }

    private void validarLimitesRetiro(Cuenta cuenta, Double montoARetirar) {
        if (cuenta.getSaldo() < montoARetirar) {
            throw new SaldoInsuficienteException(BancoMessages.SALDO_NO_DISPONIBLE);
        }

        // Regla: Cupo diario de $1000
        LocalDateTime inicioDia = LocalDate.now().atStartOfDay();
        LocalDateTime finDia = LocalDate.now().atTime(LocalTime.MAX);

        // Inyectamos movRepo para sumar los retiros de hoy
        Double totalRetiradoHoy = movRepo.sumMontoByCuentaAndTipoAndFecha(
                cuenta.getId(), 
                TipoMovimientoEnum.RETIRO.getDescripcion(), 
                inicioDia, 
                finDia
        );

        if (totalRetiradoHoy == null) totalRetiradoHoy = 0.0;

        if ((totalRetiradoHoy + montoARetirar) > BancoMessages.LIMITE_DIARIO) {
            throw new RuntimeException(BancoMessages.CUPO_EXCEDIDO);
        }
    }

    public List<ReporteMovimientoDTO> generarReporte(Long clienteId, LocalDateTime inicio, LocalDateTime fin) {
        List<Movimiento> movimientos = movRepo.findByClienteAndFechas(clienteId, inicio, fin);

        return movimientos.stream().map(m -> {
            TipoMovimientoEnum tipo = TipoMovimientoEnum.valueOf(m.getTipo().toUpperCase());
            double valorMovimiento = m.getMonto() * tipo.getFactor();
            
            // Saldo Inicial antes de este movimiento
            double saldoInicial = m.getSaldoActual() - valorMovimiento;

            return new ReporteMovimientoDTO(
                m.getFecha(),
                m.getCuenta().getCliente().getNombre() + " " + m.getCuenta().getCliente().getApellido(),
                m.getCuenta().getNumeroCuenta(),
                m.getCuenta().getTipoCuenta(),
                saldoInicial,
                m.getCuenta().getEstado(),
                valorMovimiento,
                m.getSaldoActual() // Saldo Disponible
            );
        }).collect(Collectors.toList());
    }
    
    public EstadoCuentaDTO generarReporte(Long cuentaId, Movimiento mov) {
        Cuenta cuenta = cuentaRepo.findById(cuentaId).orElseThrow();
        
        // El saldo disponible es el saldo actual de la cuenta 
        // (que ya debería haberse actualizado con la lógica del movimiento)
        return new EstadoCuentaDTO(
            LocalDate.now().toString(),
            cuenta.getCliente().getNombre() + " " + cuenta.getCliente().getApellido(),
            cuenta.getNumeroCuenta(),
            cuenta.getTipoCuenta(), // "Corriente" o "Ahorros"
            cuenta.getSaldo() - mov.getMonto(), // Saldo antes del movimiento
            true,
            mov.getMonto(),
            cuenta.getSaldo() // Saldo después del movimiento
        );
    }
    
    public String generarReporteBase64(List<ReporteMovimientoDTO> reporte) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule()); // Para manejar LocalDateTime
            String json = mapper.writeValueAsString(reporte);
            return Base64.getEncoder().encodeToString(json.getBytes());
        } catch (Exception e) {
            throw new RuntimeException("Error al codificar el reporte a Base64");
        }
    }
}
