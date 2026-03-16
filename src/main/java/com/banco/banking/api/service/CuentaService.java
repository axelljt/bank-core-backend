package com.banco.banking.api.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.banco.banking.api.dto.CuentaClienteDTO;
import com.banco.banking.api.model.Cuenta;

public interface CuentaService {

	List<Cuenta> listarTodas();
    Optional<Cuenta> buscarPorId(Long id);
    Cuenta crearCuenta(Long clienteId, Cuenta cuenta);
    CuentaClienteDTO crearCuentaCorriente(Long clienteId, Cuenta nuevaCuenta);
    Cuenta actualizar(Long id, Cuenta nueva);
    Cuenta actualizacionParcial(Long id, Map<String, Object> updates);
    void eliminar(Long id);
}
