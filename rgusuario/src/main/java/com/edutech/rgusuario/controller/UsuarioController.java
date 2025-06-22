package com.edutech.rgusuario.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edutech.rgusuario.dto.UsuarioDTO;
import com.edutech.rgusuario.model.ApiRespuesta;
import com.edutech.rgusuario.model.Estado;
import com.edutech.rgusuario.model.Usuario;
import com.edutech.rgusuario.service.RolService;
import com.edutech.rgusuario.service.UsuarioService;


@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private RolService rolService;

    //Obtener la lista de usuarios
    @GetMapping
    //ResponseEntity para poder personalizar la respuesta y el código de estado http (httpstatus)
    public ResponseEntity<?> getUsuarios() {
        List<Usuario> usuarioLista = usuarioService.findAll();
        if (usuarioLista.isEmpty()){
            return ResponseEntity.ok(new ApiRespuesta<>("No hay usuarios registrados", List.of()));

        }
        return ResponseEntity.ok(usuarioLista);
    }
    
    //GETS POR ID, RUT, EMAIL, USERNAME, ESTADO
    //Usuario
    //obtener por usuario id
    @GetMapping("/{id}")
    public ResponseEntity<?> getUsuarioById(@PathVariable Long id) {
        Optional<Usuario> usuariOpt = usuarioService.findById(id);
        if (usuariOpt.isPresent()) {
            return ResponseEntity.ok(usuariOpt.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No se encontró el usuario con ID: " + id);
        }
    }
    //UsuarioDTO
    @GetMapping("/info/{id}")
    public ResponseEntity<?> getUsuarioDTOById(@PathVariable Long id) {
        Optional<UsuarioDTO> usuarioDTO = usuarioService.findUsuarioDTOById(id);
        if (usuarioDTO.isPresent()) {
            return ResponseEntity.ok(usuarioDTO.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No se encontró el usuario con ID: " + id);
        }
    }

    //obtener por usuario rut
    @GetMapping("/rut/{rut}")
    public ResponseEntity<?> getUsuarioByRut(@PathVariable String rut) {
        Optional<Usuario> usuariOpt = usuarioService.findByRut(rut);
        if (usuariOpt.isPresent()) {
            return ResponseEntity.ok(usuariOpt.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No se encontró el usuario con RUT: " + rut);
        }
    }

    //obtener por usuario email
    @GetMapping("/email/{email}")
    public ResponseEntity<?> getUsuarioByEmail(@PathVariable String email) {
        Optional<Usuario> usuarioData = usuarioService.findByEmail(email);
        if (usuarioData.isPresent()) {
            return ResponseEntity.ok(usuarioData.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No se encontró el usuario con email: " + email);
        }
    }
    
    //obtener por usuario usurname
    @GetMapping("/username/{username}")
    public ResponseEntity<?> getUsuarioByUsername(@PathVariable String username) {
        Optional<Usuario> usuarioData = usuarioService.findByUsername(username);
        if (usuarioData.isPresent()) {
            return ResponseEntity.ok(usuarioData.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No se encontró el usuario con username: " + username);
        }
    }

    //GET POR ESTADO USUARIO Y USUARIODTO
    //obtener lista de usuarios por estado
    //devuelve TODOS los atributos del usuario
    @GetMapping("/estado/{estado}")
    public ResponseEntity<?> getUsuariosByEstado(@PathVariable Estado estado) {
        List<Usuario> usuarios = usuarioService.findByEstado(estado);
        if (usuarios.isEmpty()) {
            return ResponseEntity.ok(new ApiRespuesta<>("No hay usuarios con ese estado.", List.of()));
        }
        return ResponseEntity.ok(usuarios);
    }

    //usuarioDTO
    //obtener lista de usuarios por estado, sólo información básica
    @GetMapping("/info/estado/{estado}")
    public ResponseEntity<?> getUsuariosDTOByEstado(@PathVariable Estado estado) {
        List<UsuarioDTO> usuarios = usuarioService.findUsuariosDTOByEstado(estado);
        if (usuarios.isEmpty()) {
            return ResponseEntity.ok("No hay usuarios con estado: " + estado);
        }
        return ResponseEntity.ok(usuarios);
    }
}

/*
    @GetMapping("/{id}")
    public ResponseEntity<?> getUsuarioById(@PathVariable Long id) {
        return usuarioService.findById(id)
            .map(usuario -> ResponseEntity.ok(new ApiRespuesta<>("Usuario encontrado", usuario)))
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiRespuesta<Usuario>("No se encontró el usuario con ID: " + id, null)));
    }
    Versión más compacta de un if else
            /.map() trasnforma el valor dentro de un Optional SOLO si está presente (si no está vacío)
            /.orElseGet si no encuentra nada
            /lo mismo que el if(isPresent()){get()}else{}
 */