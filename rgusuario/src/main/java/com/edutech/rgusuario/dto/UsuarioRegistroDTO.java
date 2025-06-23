package com.edutech.rgusuario.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRegistroDTO {
    @NotBlank
    private String rut;

    @NotBlank
    private String primerNomb;

    private String segundoNomb;

    @NotBlank
    private String primerApell;

    private String segundoApell;

    @NotNull
    private LocalDate fechaNacimiento;

    @NotBlank
    private String username;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String contrasena;
}