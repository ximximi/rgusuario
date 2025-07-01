package com.edutech.rgusuario.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.edutech.rgusuario.dto.UsuarioDTO;
import com.edutech.rgusuario.model.ApiRespuesta;
import com.edutech.rgusuario.model.Estado;
import com.edutech.rgusuario.model.Rol;
import com.edutech.rgusuario.model.Usuario;
import com.edutech.rgusuario.service.RolService;
import com.edutech.rgusuario.service.UsuarioService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private RolService rolService;

    //Obtener la lista de usuarios
    @GetMapping
    public ResponseEntity<?> getUsuarios() {
        List<Usuario> lista = usuarioService.findAll();
        if (lista.isEmpty()) {
            return ResponseEntity.ok(new ApiRespuesta<>("No hay usuarios registrados", List.of()));
        }
        return ResponseEntity.ok(new ApiRespuesta<>("Lista de usuarios", lista));
    }
    
    //GETS POR ID, RUT, EMAIL, USERNAME, ESTADO
    //Usuario
    //obtener por usuario id
    @GetMapping("/{id}")
    public ResponseEntity<?> getUsuarioById(@PathVariable Long id) {
        Usuario usuario = usuarioService.findById(id)
            .orElseThrow(() -> new RuntimeException("No se encontró el usuario con ID: " + id));
        return ResponseEntity.ok(new ApiRespuesta<>("Usuario encontrado", usuario));
    }
    //UsuarioDTO
    //obtener usuario por id pero te pasa el usuarioDTO 
    @GetMapping("/info/{id}")
    public ResponseEntity<?> getUsuarioDTOById(@PathVariable Long id) {
        UsuarioDTO usuarioDTO = usuarioService.findUsuarioDTOById(id)
            .orElseThrow(() -> new RuntimeException("No se encontró el usuario con ID: " + id));
        return ResponseEntity.ok(new ApiRespuesta<>("Información básica del usuario", usuarioDTO));
    }
    //obtener por usuario rut
    @GetMapping("/rut/{rut}")
    public ResponseEntity<?> getUsuarioByRut(@PathVariable String rut) {
        Usuario usuario = usuarioService.findByRut(rut)
            .orElseThrow(() -> new RuntimeException("No se encontró el usuario con RUT: " + rut));
        return ResponseEntity.ok(new ApiRespuesta<>("Usuario con RUT encontrado", usuario));
    }
    //obtener por usuario email
    @GetMapping("/email/{email}")
    public ResponseEntity<?> getUsuarioByEmail(@PathVariable String email) {
        Usuario usuario = usuarioService.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("No se encontró el usuario con email: " + email));
        return ResponseEntity.ok(new ApiRespuesta<>("Usuario con email encontrado", usuario));
    }
    //obtener por usuario usurname
    @GetMapping("/username/{username}")
    public ResponseEntity<?> getUsuarioByUsername(@PathVariable String username) {
        Usuario usuario = usuarioService.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("No se encontró el usuario con username: " + username));
        return ResponseEntity.ok(new ApiRespuesta<>("Usuario con username encontrado", usuario));
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
        return ResponseEntity.ok(new ApiRespuesta<>("Usuarios con estado " + estado, usuarios));
    }

    //usuarioDTO
    //obtener lista de usuarios por estado, sólo información básica
    @GetMapping("/info/estado/{estado}")
    public ResponseEntity<?> getUsuariosDTOByEstado(@PathVariable Estado estado) {
        List<UsuarioDTO> usuarios = usuarioService.findUsuariosDTOByEstado(estado);
        if (usuarios.isEmpty()) {
            return ResponseEntity.ok(new ApiRespuesta<>("No hay usuarios con ese estado.", List.of()));
        }
        return ResponseEntity.ok(new ApiRespuesta<>("Usuarios DTO con estado " + estado, usuarios));
    }
    // Crear usuario como admin
    @PostMapping
    public ResponseEntity<?> createUsuario(@RequestBody Usuario usuario) {
        Usuario savedUsuario = usuarioService.crearUsuario(usuario);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new ApiRespuesta<>("Usuario creado correctamente", savedUsuario));
    }

    // Crear usuario desde la perspectiva del cliente
    @PostMapping("/registro")
    public ResponseEntity<?> registrarCliente(@Valid @RequestBody Usuario usuario) {
        Usuario nuevo = usuarioService.registrarDesdeCliente(usuario);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new ApiRespuesta<>("Usuario registrado correctamente", nuevo));
    }

    // Actualizar usuario (ADMIN)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUsuario(@PathVariable Long id, @RequestBody Usuario usuario) {
        Usuario actual = usuarioService.findById(id)
            .orElseThrow(() -> new RuntimeException("No se encontró el usuario con ID: " + id));

        if (!actual.getRut().equals(usuario.getRut()) &&
            usuarioService.existsByRut(usuario.getRut())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiRespuesta<>("El RUT ya está registrado", null));
        }

        if (!actual.getUsername().equals(usuario.getUsername()) &&
            usuarioService.existsByUsername(usuario.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiRespuesta<>("El username ya está registrado", null));
        }

        if (!actual.getEmail().equals(usuario.getEmail()) &&
            usuarioService.existsByEmail(usuario.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiRespuesta<>("El email ya está registrado", null));
        }

        actual.setRut(usuario.getRut());
        actual.setUsername(usuario.getUsername());
        actual.setFechaNacimiento(usuario.getFechaNacimiento());
        actual.setEstado(usuario.getEstado());
        actual.setRoles(usuario.getRoles());

        usuarioService.modificarInformacion(
            id,
            usuario.getPrimerNomb(),
            usuario.getSegundoNomb(),
            usuario.getPrimerApell(),
            usuario.getSegundoApell(),
            usuario.getEmail()
        );

        Usuario actualizado = usuarioService.save(actual);
        return ResponseEntity.ok(new ApiRespuesta<>("Usuario actualizado correctamente", actualizado));
    }

    // Modificación desde cliente
    @PutMapping("/{id}/modificar")
    public ResponseEntity<?> modificarInformacionCliente(
        @PathVariable Long id,
        @RequestParam String primerNomb,
        @RequestParam(required = false) String segundoNomb,
        @RequestParam String primerApell,
        @RequestParam(required = false) String segundoApell,
        @RequestParam String email) {

        Usuario actual = usuarioService.findById(id)
            .orElseThrow(() -> new RuntimeException("No se encontró el usuario con ID: " + id));

        if (!actual.getEmail().equals(email) && usuarioService.existsByEmail(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiRespuesta<>("El email ya está registrado por otro usuario", null));
        }

        Usuario actualizado = usuarioService.modificarInformacion(
            id, primerNomb, segundoNomb, primerApell, segundoApell, email);

        return ResponseEntity.ok(new ApiRespuesta<>("Datos actualizados correctamente", actualizado));
    }

    // Agregar rol a usuario
    @PostMapping("/{usuarioId}/roles/{rolId}")
    public ResponseEntity<?> agregarRol(@PathVariable Long usuarioId, @PathVariable Long rolId) {
        Rol rol = rolService.findById(rolId)
            .orElseThrow(() -> new RuntimeException("No se encontró el rol con ID: " + rolId));

        Usuario actualizado = usuarioService.agregarRol(usuarioId, rol);
        return ResponseEntity.ok(new ApiRespuesta<>("Rol agregado correctamente", actualizado));
    }

    // Remover rol de usuario
    @DeleteMapping("/{usuarioId}/roles/{rolId}")
    public ResponseEntity<?> removerRol(@PathVariable Long usuarioId, @PathVariable Long rolId) {
        Usuario actualizado = usuarioService.removerRol(usuarioId, rolId);
        return ResponseEntity.ok(new ApiRespuesta<>("Rol removido correctamente", actualizado));
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