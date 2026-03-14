package com.banco.banking.api.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import com.banco.banking.api.repository.ClienteRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "http://localhost:4200")
public class ClienteController {

    @Autowired private ClienteRepository repo;

    // GET: Obtener todos
    @GetMapping
    public List<Cliente> getAll() { return repo.findAll(); }

    // GET: Obtener uno por ID
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> getById(@PathVariable Long id) {
        return repo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/listado-creacion")
    public ResponseEntity<List<UsuarioDTO>> obtenerListadoCreacion() {
        
        List<Cliente> clientes = repo.findAll();

        List<UsuarioDTO> listado = clientes.stream().map(c -> 
            new UsuarioDTO(
                c.getNombre()+" "+c.getApellido(),
                c.getDireccion(),
                c.getTelefono(),
                c.getPassword(),
                c.getEstado()
            )
        ).collect(Collectors.toList());

        return ResponseEntity.ok(listado);
    }
    
    // POST: Crear nuevo
    @PostMapping
    public Cliente create(@Valid @RequestBody Cliente cliente) { return repo.save(cliente); }

    // PUT: Actualizar completo
    @PutMapping("/{id}")
    public Cliente update(@PathVariable Long id,@Valid @RequestBody Cliente nuevo) {
        return repo.findById(id).map(c -> {
            c.setNombre(nuevo.getNombre());
            c.setApellido(nuevo.getApellido());
            c.setEmail(nuevo.getEmail());
            return repo.save(c);
        }).orElseThrow();
    }

    // PATCH: Actualización parcial (Ej: solo el email)
    @PatchMapping("/{id}")
    public Cliente patch(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Cliente c = repo.findById(id).orElseThrow();
        if(updates.containsKey("email")) c.setEmail((String) updates.get("email"));
        return repo.save(c);
    }

    // DELETE: Eliminar
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { repo.deleteById(id); }
}
