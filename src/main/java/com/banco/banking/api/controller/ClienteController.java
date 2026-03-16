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

/**
 * Controlador REST para la gestión de Clientes en el sistema bancario.
 * Proporciona endpoints para realizar operaciones CRUD y consultas especializadas.
 * @author Axell
 * @version 1.0
 */
@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "http://localhost:4200")
public class ClienteController {

    @Autowired 
    private ClienteService service;

    /**
     * Obtiene la lista completa de todos los clientes registrados.
     * * @return Lista de objetos {@link Cliente}.
     */
    @GetMapping
    public List<Cliente> getAll() { 
        return service.listarTodos(); 
    }

    /**
     * Busca un cliente específico mediante su identificador único.
     * * @param id Identificador único del cliente.
     * @return {@link ResponseEntity} con el objeto {@link Cliente} si existe, 
     * o un estado 404 (Not Found) si no se encuentra.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> getById(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Proporciona un listado simplificado de clientes formateado para la creación de usuarios.
     * Utiliza un DTO para exponer solo la información necesaria del cliente.
     * * @return {@link ResponseEntity} con la lista de {@link UsuarioDTO}.
     */
    @GetMapping("/listado-creacion")
    public ResponseEntity<List<UsuarioDTO>> obtenerListadoCreacion() {
        return ResponseEntity.ok(service.obtenerListadoParaCreacion());
    }

    /**
     * Registra un nuevo cliente en el sistema.
     * * @param cliente Objeto {@link Cliente} a guardar. Debe cumplir con las validaciones definidas.
     * @return El objeto {@link Cliente} guardado, incluyendo su ID generado.
     */
    @PostMapping
    public Cliente create(@Valid @RequestBody Cliente cliente) { 
        return service.guardar(cliente); 
    }

    /**
     * Actualiza la información completa de un cliente existente.
     * * @param id Identificador del cliente a actualizar.
     * @param nuevo Objeto {@link Cliente} con los nuevos datos.
     * @return El objeto {@link Cliente} actualizado.
     */
    @PutMapping("/{id}")
    public Cliente update(@PathVariable Long id, @Valid @RequestBody Cliente nuevo) {
        return service.actualizar(id, nuevo);
    }

    /**
     * Realiza una actualización parcial de los datos de un cliente.
     * * @param id Identificador del cliente.
     * @param updates Mapa que contiene los campos a actualizar (ej: "email").
     * @return El objeto {@link Cliente} tras aplicar los cambios parciales.
     */
    @PatchMapping("/{id}")
    public Cliente patch(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        return service.actualizacionParcial(id, updates);
    }

    /**
     * Elimina un cliente del sistema permanentemente.
     * * @param id Identificador del cliente a eliminar.
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { 
        service.eliminar(id); 
    }


}
