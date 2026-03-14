package com.banco.banking.api.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.banco.banking.api.model.Cliente;

class ClienteTest {

    @Test
    void testClienteProperties() {
        Cliente cliente = new Cliente();
        cliente.setNombre("Juan");
        cliente.setApellido("Perez");
        cliente.setId(1L);
        cliente.setPassword("1234");
        cliente.setEstado(true);

        assertEquals("Juan", cliente.getNombre());
        assertEquals(1L, cliente.getId());
        assertTrue(cliente.getEstado());
    }
}
