package com.banco.banking.api.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor 
@AllArgsConstructor 
@Data
public class Cuenta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El número de cuenta es obligatorio")
    @Size(min = 6, max = 10, message = "El número de cuenta debe tener entre 6 y 10 dígitos")
    @Column(unique = true, nullable = false)
    private String numeroCuenta;

    @NotNull(message = "El saldo inicial no puede estar vacío")
    @Min(value = 0, message = "El saldo inicial no puede ser negativo")
    private Double saldo;

    @NotBlank(message = "El tipo de cuenta (Ahorros/Corriente) es obligatorio")
    private String tipoCuenta;

    @NotNull(message = "El estado de la cuenta es obligatorio")
    private Boolean estado; // Cambiado a Boolean para validar @NotNull

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    @JsonBackReference
    @NotNull(message = "La cuenta debe estar asociada a un cliente")
    private Cliente cliente;
    
    @OneToMany(mappedBy = "cuenta", cascade = CascadeType.ALL)
    private List<Movimiento> movimientos;
}