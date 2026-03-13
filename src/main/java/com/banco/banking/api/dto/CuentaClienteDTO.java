package com.banco.banking.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CuentaClienteDTO {
	private String numeroCuenta;
	private String tipoCuenta;
	private Double saldoInicial;
	private boolean estadoCuenta;
	private String nombreCliente;
}
