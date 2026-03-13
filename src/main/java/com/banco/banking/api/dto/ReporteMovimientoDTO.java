package com.banco.banking.api.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReporteMovimientoDTO {
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDateTime fecha;
    private String cliente;      // Nombre + Apellido
    private String numeroCuenta;
    private String tipo;
    private Double monto;
    private Double saldoActual;
}
