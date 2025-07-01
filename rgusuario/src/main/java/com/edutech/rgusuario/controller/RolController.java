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
import org.springframework.web.bind.annotation.RestController;

import com.edutech.rgusuario.model.ApiRespuesta;
import com.edutech.rgusuario.model.Permiso;
import com.edutech.rgusuario.model.Rol;
import com.edutech.rgusuario.service.RolService;

import jakarta.validation.Valid;




@RestController
@RequestMapping("/api/v1/roles")
public class RolController {
    @Autowired
    private RolService rolService;

    //Obtener lista de roles
    @GetMapping
    //ResponseEntity para poder personalizar la respuesta y el código de estado http (httpstatus)
    public ResponseEntity<?> getRoles(){
        List<Rol> rolesLista = rolService.findAll();//obtiene todos los roles
        if (rolesLista.isEmpty()) {
            return ResponseEntity.ok(new ApiRespuesta<>("No hay roles registrados", List.of()));
        }
        return ResponseEntity.ok(rolesLista);
    }

    // Obtener por id (rol)
    @GetMapping("/{id}")
    public ResponseEntity<?> getRolById(@PathVariable Long id) {
        Rol rol = rolService.findById(id)
            .orElseThrow(() -> new RuntimeException("No se encontró el rol con ID: " + id));

        return ResponseEntity.ok(new ApiRespuesta<>("Rol encontrado", rol));
    }
    // Postear un nuevo rol
    @PostMapping
    public ResponseEntity<?> createRol(@RequestBody @Valid Rol rol) {
        if (rolService.existsByNombre(rol.getNombre())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiRespuesta<>("Ya existe un rol con el nombre: " + rol.getNombre(), null));
        }

        Rol guardado = rolService.save(rol);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new ApiRespuesta<>("Rol creado correctamente", guardado));
    }

    // Actualiza un rol
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRol(@PathVariable Long id, @RequestBody @Valid Rol rol) {
        Rol existente = rolService.findById(id)
            .orElseThrow(() -> new RuntimeException("No se encontró el rol con ID: " + id));

        if (!existente.getNombre().equals(rol.getNombre()) && rolService.existsByNombre(rol.getNombre())) {
            throw new RuntimeException("Ya existe el rol con el nombre: " + rol.getNombre());
        }

        existente.setNombre(rol.getNombre());
        existente.setDescripcion(rol.getDescripcion());
        existente.setPermiso(rol.getPermiso());

        Rol actualizado = rolService.save(existente);
        return ResponseEntity.ok(new ApiRespuesta<>("Rol actualizado correctamente", actualizado));
    }
        //Elimina Rol
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRol(@PathVariable Long id) {
        if (!rolService.existsById(id)) {
            throw new RuntimeException("No se encontró el rol con ID: " + id);
        }

        rolService.deleteById(id);
        return ResponseEntity.noContent().build(); // HTTP 204 sin body
    }
    
    //PERMISOS
    // Postear permiso a un rol en específico
    @PostMapping("/{id}/permisos/{permiso}")
    public ResponseEntity<?> addPermiso(@PathVariable Long id, @PathVariable Permiso permiso) {
        Rol actualizado = rolService.agregarPermiso(id, permiso);
        return ResponseEntity.ok(new ApiRespuesta<>("Permiso agregado correctamente", actualizado));
    }
    // Eliminar permiso
    @DeleteMapping("/{id}/permisos/{permiso}")
    public ResponseEntity<?> removePermiso(@PathVariable Long id, @PathVariable Permiso permiso) {
        Rol actualizado = rolService.removerPermiso(id, permiso);
        return ResponseEntity.ok(new ApiRespuesta<>("Permiso eliminado correctamente", actualizado));
    }
}
