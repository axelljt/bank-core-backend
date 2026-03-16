package com.banco.banking.api.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movimiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDateTime fecha;

    @NotBlank(message = "El tipo de movimiento (Retiro/Deposito) es obligatorio")
    @Pattern(regexp = "^(Retiro|Deposito)$", message = "El tipo debe ser 'Retiro' o 'Deposito'")
    private String tipo;

    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser un valor positivo")
    private Double monto;

    private Double saldoActual;

    @ManyToOne
    @JoinColumn(name = "cuenta_id", nullable = false)
    @JsonBackReference
    private Cuenta cuenta;
}
