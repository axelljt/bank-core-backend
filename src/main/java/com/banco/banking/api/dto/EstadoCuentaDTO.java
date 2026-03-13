package com.banco.banking.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EstadoCuentaDTO {
    private String numeroCuenta;
    private String tipoCuenta;
    private Double saldoInicial; // Saldo antes del movimiento
    private boolean estadoCuenta;
    private String descMovimiento;   // El monto (positivo o negativo)
}
