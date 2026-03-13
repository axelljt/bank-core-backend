package com.banco.banking.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsuarioDTO {
	private String nombre;
	private String direccion;
	private String telefono;
	private String password;
	private boolean estado;
}
