package com.edutech.rgusuario.controller;

import java.util.List;
import java.util.Optional;

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
    //obtener usuario por id pero te pasa el usuarioDTO 
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

    //crear usuario como admin
    @PostMapping
    public ResponseEntity<?> createUsuario(@RequestBody Usuario usuario) {
        try {
            Usuario savedUsuario = usuarioService.crearUsuario(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUsuario);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al crear el usuario: " + e.getMessage());
        }
    }
    // crear usuario PERSPECTIVA CLIENTE (sin DTO)
    @PostMapping("/registro")
    public ResponseEntity<?> registrarCliente(@Valid @RequestBody Usuario usuario) {
        try {
            Usuario nuevo = usuarioService.registrarDesdeCliente(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al registrar usuario: " + e.getMessage());
        }
    }

    //Actualizar usuario, desde ADMIN
    //aplica lo mismo que al crear
    //no se puede repetir rut, username o email
    @PutMapping("/{id}")
    //@pathvariable es el id en la url y @RequestBdy es para el nuevo contenido del usuario
    public ResponseEntity<?> updateUsuario(@PathVariable Long id, @RequestBody Usuario usuario) {
        Optional<Usuario> actualOpt = usuarioService.findById(id);
        //por si no se encuentra
        if (actualOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiRespuesta<>("No se encontró el usuario con ID: " + id, null));
        }
        //se recupera porque sí existe
        Usuario actual = actualOpt.get();
        //validaciones de duplicados
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
        //actualización
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
        return ResponseEntity.ok(actualizado);
    }
    //Modificar DESDE LA PERSPECTIVA DEL CLIENTE
    @PutMapping("/{id}/modificar")
    public ResponseEntity<?> modificarInformacion(
        
            @PathVariable Long id,
            //captura los parámetros enviados en la url
            //porque RequestBody sería aplicable con un nuevo dto
            @RequestParam String primerNomb,
            @RequestParam(required = false) String segundoNomb,
            @RequestParam String primerApell,
            @RequestParam(required = false) String segundoApell,
            @RequestParam String email) {
        Optional<Usuario> usuarioOpt = usuarioService.findById(id);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No se encontró el usuario con ID: " + id);
        }
        Usuario actual = usuarioOpt.get();
        if (!actual.getEmail().equals(email) && usuarioService.existsByEmail(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("El email ya está registrado por otro usuario");
        }

        Usuario actualizado = usuarioService.modificarInformacion(
            id, primerNomb, segundoNomb, primerApell, segundoApell, email
        );

        return ResponseEntity.ok(actualizado);
    }

    @PostMapping("/{usuarioId}/roles/{rolId}")
    public ResponseEntity<?> agregarRol(
            @PathVariable Long usuarioId,
            @PathVariable Long rolId) {

        Optional<Rol> rolOpt = rolService.findById(rolId);
        if (rolOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiRespuesta<>("No se encontró el rol con ID: " + rolId, null));
        }
        try {
            Usuario actualizado = usuarioService.agregarRol(usuarioId, rolOpt.get());
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiRespuesta<>(e.getMessage(),null));
        }
    }
    @DeleteMapping("/{usuarioId}/roles/{rolId}")
    public ResponseEntity<?> removerRol(
            @PathVariable Long usuarioId,
            @PathVariable Long rolId) {

        try {
            Usuario actualizado = usuarioService.removerRol(usuarioId, rolId);
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiRespuesta<>(e.getMessage(),null));
        }
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