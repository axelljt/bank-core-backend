package com.banco.banking.api.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.banco.banking.api.model.Cuenta;
import com.banco.banking.api.repository.CuentaRepository;

@DataJpaTest
public class CuentaRepositoryTest {

    @Autowired
    private CuentaRepository cuentaRepository;

    @Test
    void debeGuardarUnaCuenta() {
        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta("999999");
        cuenta.setSaldo(100.0);
        cuenta.setEstado(true);
        cuenta.setTipoCuenta("Ahorros");

        Cuenta guardada = cuentaRepository.save(cuenta);

        assertNotNull(guardada.getId());
        assertEquals("999999", guardada.getNumeroCuenta());
    }
}