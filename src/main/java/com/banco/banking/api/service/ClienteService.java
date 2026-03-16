package com.banco.banking.api.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.banco.banking.api.dto.UsuarioDTO;
import com.banco.banking.api.model.Cliente;

public interface ClienteService {

	List<Cliente> listarTodos();
    Optional<Cliente> buscarPorId(Long id);
    List<UsuarioDTO> obtenerListadoParaCreacion();
    Cliente guardar(Cliente cliente);
    Cliente actualizar(Long id, Cliente nuevo);
    Cliente actualizacionParcial(Long id, Map<String, Object> updates);
    void eliminar(Long id);
}
