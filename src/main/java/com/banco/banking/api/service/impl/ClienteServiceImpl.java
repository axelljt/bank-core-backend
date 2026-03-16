package com.banco.banking.api.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.banco.banking.api.dto.UsuarioDTO;
import com.banco.banking.api.model.Cliente;
import com.banco.banking.api.repository.ClienteRepository;
import com.banco.banking.api.service.ClienteService;

@Service
public class ClienteServiceImpl implements ClienteService {

    @Autowired 
    private ClienteRepository repo;

    @Override
    public List<Cliente> listarTodos() {
        return repo.findAll();
    }

    @Override
    public Optional<Cliente> buscarPorId(Long id) {
        return repo.findById(id);
    }

    @Override
    public List<UsuarioDTO> obtenerListadoParaCreacion() {
        return repo.findAll().stream().map(c -> 
            new UsuarioDTO(
                c.getNombre() + " " + c.getApellido(),
                c.getDireccion(),
                c.getTelefono(),
                c.getPassword(),
                c.getEstado()
            )
        ).collect(Collectors.toList());
    }

    @Override
    public Cliente guardar(Cliente cliente) {
        return repo.save(cliente);
    }

    @Override
    public Cliente actualizar(Long id, Cliente nuevo) {
        return repo.findById(id).map(c -> {
            c.setNombre(nuevo.getNombre());
            c.setApellido(nuevo.getApellido());
            c.setEmail(nuevo.getEmail());
            return repo.save(c);
        }).orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + id));
    }

    @Override
    public Cliente actualizacionParcial(Long id, Map<String, Object> updates) {
        Cliente c = repo.findById(id).orElseThrow();
        if(updates.containsKey("email")) c.setEmail((String) updates.get("email"));
        // Aquí podrías agregar lógica para otros campos si fuera necesario
        return repo.save(c);
    }

    @Override
    public void eliminar(Long id) {
        repo.deleteById(id);
    }
}