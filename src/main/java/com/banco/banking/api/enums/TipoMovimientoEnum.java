package com.banco.banking.api.enums;

import lombok.Getter;

@Getter
public enum TipoMovimientoEnum {
    RETIRO("Retiro", -1),
    DEPOSITO("Deposito", 1);

    private final String descripcion;
    private final int factor; // -1 para restar, 1 para sumar

    TipoMovimientoEnum(String descripcion, int factor) {
        this.descripcion = descripcion;
        this.factor = factor;
    }
}
