package com.banco.banking.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banco.banking.api.model.Cuenta;
import com.banco.banking.api.repository.ClienteRepository;
import com.banco.banking.api.repository.CuentaRepository;

@RestController
@RequestMapping("/api/cuentas")
@CrossOrigin(origins = "*")
public class CuentaController {

    @Autowired private CuentaRepository cuentaRepo;
    @Autowired private ClienteRepository clienteRepo;

    @GetMapping
    public List<Cuenta> getAll() { return cuentaRepo.findAll(); }

    @PostMapping("/cliente/{clienteId}")
    public ResponseEntity<Cuenta> create(@PathVariable Long clienteId, @RequestBody Cuenta cuenta) {
        return clienteRepo.findById(clienteId).map(cliente -> {
            cuenta.setCliente(cliente);
            // Si el JSON no trae estado, lo ponemos en true por defecto
            if (!cuenta.isEstado()) cuenta.setEstado(true); 
            return ResponseEntity.ok(cuentaRepo.save(cuenta));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cuenta> update(@PathVariable Long id, @RequestBody Cuenta nueva) {
        return cuentaRepo.findById(id).map(c -> {
            c.setNumeroCuenta(nueva.getNumeroCuenta());
            c.setTipoCuenta(nueva.getTipoCuenta());
            c.setSaldo(nueva.getSaldo());
            c.setEstado(nueva.isEstado()); // <-- Ahora actualizamos el estado
            return ResponseEntity.ok(cuentaRepo.save(c));
        }).orElse(ResponseEntity.notFound().build());
    }
}
