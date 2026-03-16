package com.banco.banking.api.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.banco.banking.api.dto.EstadoCuentaDTO;
import com.banco.banking.api.dto.ReporteMovimientoDTO;
import com.banco.banking.api.model.Movimiento;

public interface MovimientoService {
    List<Movimiento> listarTodos();
    Optional<Movimiento> buscarPorId(Long id);
    Movimiento registrarMovimiento(Long cuentaId, Movimiento mov);
    Movimiento actualizarMovimiento(Long id, Map<String, Object> updates);
    void eliminar(Long id);

    List<ReporteMovimientoDTO> generarReporte(Long clienteId, LocalDateTime inicio, LocalDateTime fin);
    EstadoCuentaDTO generarReporteEstado(Long cuentaId, Movimiento mov);
    String generarReporteBase64(List<ReporteMovimientoDTO> reporte);

}
