package com.banco.banking.api.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Cliente extends Persona {
    
    private String email;
    private String password;
    private boolean estado;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Cuenta> cuentas;
}