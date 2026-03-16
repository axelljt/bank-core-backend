package com.banco.banking.api.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banco.banking.api.dto.CuentaClienteDTO;
import com.banco.banking.api.model.Cuenta;
import com.banco.banking.api.repository.ClienteRepository;
import com.banco.banking.api.repository.CuentaRepository;
import com.banco.banking.api.service.CuentaService;

@Service
public class CuentaServiceImpl implements CuentaService {

    @Autowired private CuentaRepository cuentaRepo;
    @Autowired private ClienteRepository clienteRepo;

    @Override
    @Transactional(readOnly = true)
    public List<Cuenta> listarTodas() {
        return cuentaRepo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cuenta> buscarPorId(Long id) {
        return cuentaRepo.findById(id);
    }

    @Override
    @Transactional
    public Cuenta crearCuenta(Long clienteId, Cuenta cuenta) {
        return clienteRepo.findById(clienteId).map(cliente -> {
            cuenta.setCliente(cliente);
            if (cuenta.getEstado() == null) cuenta.setEstado(true);
            return cuentaRepo.save(cuenta);
        }).orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + clienteId));
    }

    @Override
    @Transactional
    public CuentaClienteDTO crearCuentaCorriente(Long clienteId, Cuenta nuevaCuenta) {
        return clienteRepo.findById(clienteId).map(cliente -> {
            nuevaCuenta.setTipoCuenta("Corriente");
            nuevaCuenta.setCliente(cliente);
            if (nuevaCuenta.getEstado() == null) nuevaCuenta.setEstado(true);

            Cuenta cuentaGuardada = cuentaRepo.save(nuevaCuenta);

            return new CuentaClienteDTO(
                cuentaGuardada.getNumeroCuenta(),
                cuentaGuardada.getTipoCuenta(),
                cuentaGuardada.getSaldo(),
                cuentaGuardada.getEstado(),
                cliente.getNombre() + " " + cliente.getApellido()
            );
        }).orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + clienteId));
    }

    @Override
    @Transactional
    public Cuenta actualizar(Long id, Cuenta nueva) {
        return cuentaRepo.findById(id).map(c -> {
            c.setNumeroCuenta(nueva.getNumeroCuenta());
            c.setTipoCuenta(nueva.getTipoCuenta());
            c.setSaldo(nueva.getSaldo());
            c.setEstado(nueva.getEstado());
            return cuentaRepo.save(c);
        }).orElseThrow(() -> new RuntimeException("Cuenta no encontrada con ID: " + id));
    }

    @Override
    @Transactional
    public Cuenta actualizacionParcial(Long id, Map<String, Object> updates) {
        Cuenta c = cuentaRepo.findById(id).orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));
        
        // Lógica de PATCH: Solo actualizamos lo que viene en el mapa
        if (updates.containsKey("saldo")) c.setSaldo(Double.parseDouble(updates.get("saldo").toString()));
        if (updates.containsKey("estado")) c.setEstado((Boolean) updates.get("estado"));
        if (updates.containsKey("tipoCuenta")) c.setTipoCuenta((String) updates.get("tipoCuenta"));
        
        return cuentaRepo.save(c);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        cuentaRepo.deleteById(id);
    }
}
