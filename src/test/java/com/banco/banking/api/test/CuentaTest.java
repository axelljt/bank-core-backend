package com.banco.banking.api.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.banco.banking.api.model.Cuenta;

class CuentaTest {

    @Test
    void testCuentaSaldo() {
        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta("225487");
        cuenta.setSaldo(100.0);
        cuenta.setEstado(true);

        assertNotNull(cuenta.getNumeroCuenta());
        assertEquals(100.0, cuenta.getSaldo());
    }
}
