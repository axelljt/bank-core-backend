package com.banco.banking.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    @GetMapping("/{id}")
    public ResponseEntity<Cuenta> getById(@PathVariable Long id) {
        return cuentaRepo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // POST: Crear cuenta asociada a un cliente
    @PostMapping("/cliente/{clienteId}")
    public ResponseEntity<Cuenta> create(@PathVariable Long clienteId, @RequestBody Cuenta nuevaCuenta) {
        return clienteRepo.findById(clienteId).map(cliente -> {
            nuevaCuenta.setCliente(cliente);
            return ResponseEntity.ok(cuentaRepo.save(nuevaCuenta));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { cuentaRepo.deleteById(id); }
}
