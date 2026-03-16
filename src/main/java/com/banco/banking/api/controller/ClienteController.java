package com.banco.banking.api.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banco.banking.api.dto.UsuarioDTO;
import com.banco.banking.api.model.Cliente;
import com.banco.banking.api.service.ClienteService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "http://localhost:4200")
public class ClienteController {

	@Autowired 
    private ClienteService service;

    @GetMapping
    public List<Cliente> getAll() { 
        return service.listarTodos(); 
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> getById(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/listado-creacion")
    public ResponseEntity<List<UsuarioDTO>> obtenerListadoCreacion() {
        return ResponseEntity.ok(service.obtenerListadoParaCreacion());
    }

    @PostMapping
    public Cliente create(@Valid @RequestBody Cliente cliente) { 
        return service.guardar(cliente); 
    }

    @PutMapping("/{id}")
    public Cliente update(@PathVariable Long id, @Valid @RequestBody Cliente nuevo) {
        return service.actualizar(id, nuevo);
    }

    @PatchMapping("/{id}")
    public Cliente patch(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        return service.actualizacionParcial(id, updates);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { 
        service.eliminar(id); 
    }

}
