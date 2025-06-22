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
            return ResponseEntity.ok(new ApiRespuesta<>("No hay roles registradoos", List.of()));
        }
        return ResponseEntity.ok(rolesLista);
    }

    //Obtener por id (rol)
    @GetMapping("/{id}")
    //ResponseEntity<?> para poder devolverle cualquier tipo de objeto
    public ResponseEntity<?> getRolById(@PathVariable Long id) {//extrae el id y lo pasa a argumento
        Optional<Rol> rolOpt = rolService.findById(id);
        if (rolOpt.isPresent()){
            return ResponseEntity.ok(rolOpt.get());//devuelve el rol encontrado con el id
        } else {
            //Se crea un mapa (JSON) para dar un mensaje
            //Map<String, Object> respuesta = new HashMap<>();
            //respuesta.put("mensaje", "No se encontró el rol con ID: "+ id);//put() necesita clave y valor
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiRespuesta<Rol>("No se encontró el rol con ID: "+ id, null));
        }
    }

    //Postear un nuevo rol
    @PostMapping
    public ResponseEntity<?> createRol(@RequestBody @Valid Rol rol) {//REquest toma los datos (de un json) y los transforma en Rol (objeto)
        if (rolService.existsByNombre(rol.getNombre())){//para evitar roles duplicados
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiRespuesta<Rol>("Ya existe un rol con el nombre: "+ rol.getNombre(), null));
        } 
        try{
            //GUARDA ROL
            Rol guardado = rolService.save(rol);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(guardado);//intenta guardar rol en bd
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiRespuesta<Rol>("Error al crear el rol. "+ e.getMessage(),null));
        }
    }

    //Actualiza un rol 
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRol(@PathVariable Long id, @RequestBody @Valid Rol rol) {
        Optional<Rol> rolOpt = rolService.findById(id);

        if(rolOpt.isPresent()){ 
            //Verifica si el nuevo nombre ya existe y no es el mismo que el actual
            if (!rolOpt.get().getNombre().equals(rol.getNombre()) && rolService.existsByNombre(rol.getNombre())){
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ApiRespuesta<Rol>("Ya existe el rol con el nombre: "+rol.getNombre(), null));
                }
                //ACTUALIZAR CAMPOS DE ROL
                Rol existente = rolOpt.get();
                existente.setNombre(rol.getNombre());
                existente.setDescripcion(rol.getDescripcion());
                existente.setPermiso(rol.getPermiso());

                Rol actualizado = rolService.save(existente);
                return ResponseEntity.ok(actualizado); 
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiRespuesta<Rol>("No se encontró el rol. ", null));
        }
    }

    //Elimina Rol
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRol(@PathVariable Long id) {
        try {
            //por si no existe
            if (!rolService.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiRespuesta<Void>("No se encontró el rol con ID: " + id, null));
            }
            //ELIMINA por id (completamente)
            rolService.deleteById(id);
            return ResponseEntity.ok(new ApiRespuesta<Void>("Rol eliminado correctamente. ", null));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiRespuesta<Void>("Error al eliminar el rol: " + e.getMessage(), null));
        }
    }


    
    //PERMISOS
    //Postear permiso a un rol en específico
    @PostMapping("/{id}/permisos/{permiso}")
    //id del rol que se va a modificar y permiso será el valor del enum de Permiso
    public ResponseEntity<?> addPermiso(@PathVariable Long id, @PathVariable Permiso permiso) { 
        //POST permiso
        Rol rolConPermiso = rolService.agregarPermiso(id, permiso);

        if (rolConPermiso != null) {//verificar si se modificó y no esta nulo
            return ResponseEntity.ok(rolConPermiso);
        } else {//Si no se encuentra el rol
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiRespuesta<Rol>("No se pudo agregar el permiso. Rol con ID " + id + " no encontrado", null));
        }
    }

    //Eliminar permiso
    @DeleteMapping("/{id}/permisos/{permiso}")
    public ResponseEntity<?> removePermiso(@PathVariable Long id, @PathVariable Permiso permiso) {
        //Delete, devuelve modificado (elminado) o null (no se encontró id)
        Rol rolSinPermiso = rolService.removerPermiso(id, permiso);

        if (rolSinPermiso != null) {
            return ResponseEntity.ok(new ApiRespuesta<>("Permiso eliminado correctamente", rolSinPermiso));
        } else {//rol no encontrado
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiRespuesta<Rol>("No se pudo eliminar el permiso. Rol con ID " + id + " no encontrado", null));
        }
    }  
}
