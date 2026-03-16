package com.banco.banking.api.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import com.banco.banking.api.service.MovimientoService;
import com.banco.banking.api.utils.BancoMessages;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.transaction.Transactional;
@Service
public class MovimientoServiceImpl implements MovimientoService {

    @Autowired private MovimientoRepository movRepo;
    @Autowired private CuentaRepository cuentaRepo;

    @Override
    public List<Movimiento> listarTodos() {
        return movRepo.findAll();
    }

    @Override
    public Optional<Movimiento> buscarPorId(Long id) {
        return movRepo.findById(id);
    }

    @Override
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

    @Override
    @Transactional
    public void eliminar(Long id) {
        Movimiento mov = movRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado"));
        
        // Reversión de saldo antes de eliminar
        Cuenta cuenta = mov.getCuenta();
        TipoMovimientoEnum tipo = TipoMovimientoEnum.valueOf(mov.getTipo().toUpperCase());
        double saldoRevertido = cuenta.getSaldo() - (mov.getMonto() * tipo.getFactor());
        
        cuenta.setSaldo(saldoRevertido);
        cuentaRepo.save(cuenta);
        movRepo.deleteById(id);
    }

    @Override
    @Transactional
    public Movimiento actualizarMovimiento(Long id, Map<String, Object> updates) {
        Movimiento mov = movRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado"));
        
        Cuenta cuenta = mov.getCuenta();
        
        TipoMovimientoEnum tipoActual = TipoMovimientoEnum.valueOf(mov.getTipo().toUpperCase());
        double saldoBase = cuenta.getSaldo() - (mov.getMonto() * tipoActual.getFactor());

        if (updates.containsKey("monto")) {
            mov.setMonto(Double.parseDouble(updates.get("monto").toString()));
        }
        
        if (updates.containsKey("tipo")) {
            mov.setTipo(updates.get("tipo").toString());
        }

        TipoMovimientoEnum nuevoTipo = TipoMovimientoEnum.valueOf(mov.getTipo().toUpperCase());
        double nuevoSaldo = saldoBase + (mov.getMonto() * nuevoTipo.getFactor());

        cuenta.setSaldo(nuevoSaldo);
        mov.setSaldoActual(nuevoSaldo);

        cuentaRepo.save(cuenta);
        return movRepo.save(mov);
    }

    @Override
    public List<ReporteMovimientoDTO> generarReporte(Long clienteId, LocalDateTime inicio, LocalDateTime fin) {
        return movRepo.findByClienteAndFechas(clienteId, inicio, fin).stream().map(m -> {
            TipoMovimientoEnum tipo = TipoMovimientoEnum.valueOf(m.getTipo().toUpperCase());
            double valorMov = m.getMonto() * tipo.getFactor();
            return new ReporteMovimientoDTO(
                m.getFecha(),
                m.getCuenta().getCliente().getNombre() + " " + m.getCuenta().getCliente().getApellido(),
                m.getCuenta().getNumeroCuenta(),
                m.getCuenta().getTipoCuenta(),
                m.getSaldoActual() - valorMov,
                m.getCuenta().getEstado(),
                valorMov,
                m.getSaldoActual()
            );
        }).collect(Collectors.toList());
    }

    @Override
    public EstadoCuentaDTO generarReporteEstado(Long cuentaId, Movimiento mov) {
        Cuenta cuenta = cuentaRepo.findById(cuentaId).orElseThrow();
        return new EstadoCuentaDTO(
            LocalDate.now().toString(),
            cuenta.getCliente().getNombre() + " " + cuenta.getCliente().getApellido(),
            cuenta.getNumeroCuenta(),
            cuenta.getTipoCuenta(),
            cuenta.getSaldo() - mov.getMonto(),
            true,
            mov.getMonto(),
            cuenta.getSaldo()
        );
    }

    @Override
    public String generarReporteBase64(List<ReporteMovimientoDTO> reporte) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            return Base64.getEncoder().encodeToString(mapper.writeValueAsBytes(reporte));
        } catch (Exception e) {
            throw new RuntimeException("Error Base64");
        }
    }

    private void validarLimitesRetiro(Cuenta cuenta, Double monto) {
        if (cuenta.getSaldo() < monto) throw new SaldoInsuficienteException(BancoMessages.SALDO_NO_DISPONIBLE);
        Double totalHoy = movRepo.sumMontoByCuentaAndTipoAndFecha(cuenta.getId(), "RETIRO", LocalDate.now().atStartOfDay(), LocalDate.now().atTime(LocalTime.MAX));
        if ((Optional.ofNullable(totalHoy).orElse(0.0) + monto) > BancoMessages.LIMITE_DIARIO) throw new RuntimeException(BancoMessages.CUPO_EXCEDIDO);
    }
}