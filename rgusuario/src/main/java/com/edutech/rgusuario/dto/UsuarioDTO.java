package com.edutech.rgusuario.dto;

import java.util.List;

import com.edutech.rgusuario.model.Estado;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private Long id;
    private String username;
    private String email;
    private Estado estado;
    private List<RolDTO> roles;
}