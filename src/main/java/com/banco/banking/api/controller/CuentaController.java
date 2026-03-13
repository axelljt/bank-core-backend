package com.banco.banking.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banco.banking.api.dto.CuentaClienteDTO;
import com.banco.banking.api.model.Cuenta;
import com.banco.banking.api.repository.ClienteRepository;
import com.banco.banking.api.repository.CuentaRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cuentas")
@CrossOrigin(origins = "*")
public class CuentaController {

    @Autowired private CuentaRepository cuentaRepo;
    @Autowired private ClienteRepository clienteRepo;

    @GetMapping
    public List<Cuenta> getAll() { return cuentaRepo.findAll(); }

    @PostMapping("/cliente/{clienteId}")
    public ResponseEntity<Cuenta> create(@PathVariable Long clienteId,@Valid @RequestBody Cuenta cuenta) {
        return clienteRepo.findById(clienteId).map(cliente -> {
            cuenta.setCliente(cliente);
            // Si el JSON no trae estado, lo ponemos en true por defecto
            if (!cuenta.getEstado()) cuenta.setEstado(true); 
            return ResponseEntity.ok(cuentaRepo.save(cuenta));
        }).orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/corriente/cliente/{clienteId}")
    public ResponseEntity<CuentaClienteDTO> crearCuentaCorriente(
            @PathVariable Long clienteId, 
            @RequestBody Cuenta nuevaCuenta) {

        return clienteRepo.findById(clienteId).map(cliente -> {
            // 1. Forzamos que sea cuenta Corriente y configuramos el cliente
            nuevaCuenta.setTipoCuenta("Corriente");
            nuevaCuenta.setCliente(cliente);
            
            // 2. Si el estado no viene en el JSON, la activamos por defecto
            nuevaCuenta.setEstado(true); 

            // 3. Guardamos en la base de datos
            Cuenta cuentaGuardada = cuentaRepo.save(nuevaCuenta);

            // 4. Mapeamos manualmente al DTO CuentaClienteDTO
            CuentaClienteDTO dto = new CuentaClienteDTO(
                cuentaGuardada.getNumeroCuenta(),
                cuentaGuardada.getTipoCuenta(),
                cuentaGuardada.getSaldo(), // En la creación, el saldo inicial es el saldo de apertura
                cuentaGuardada.getEstado(),
                cliente.getNombre() + " " + cliente.getApellido()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
            
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cuenta> update(@PathVariable Long id, @Valid @RequestBody Cuenta nueva) {
        return cuentaRepo.findById(id).map(c -> {
            c.setNumeroCuenta(nueva.getNumeroCuenta());
            c.setTipoCuenta(nueva.getTipoCuenta());
            c.setSaldo(nueva.getSaldo());
            c.setEstado(nueva.getEstado()); // <-- Ahora actualizamos el estado
            return ResponseEntity.ok(cuentaRepo.save(c));
        }).orElse(ResponseEntity.notFound().build());
    }
}
