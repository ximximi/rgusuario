package com.edutech.rgusuario.controller;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.edutech.rgusuario.config.SecurityConfig;
import com.edutech.rgusuario.model.Permiso;
import com.edutech.rgusuario.model.Rol;
import com.edutech.rgusuario.service.RolService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Import(SecurityConfig.class)
@WebMvcTest(RolController.class)
//@ActiveProfiles("test")
@WithMockUser(username = "xime", roles = {"ADMIN"})
class RolControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RolService rolService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getRoles_losRoles_existentes_devuelveLista() throws Exception {
        Rol admin = new Rol(1L, "ADMIN", "Administrador", Set.of(Permiso.VER_USUARIO));
        Rol cliente = new Rol(2L, "CLIENTE", "Usuario final", Set.of(Permiso.VER_USUARIO));

        when(rolService.findAll()).thenReturn(List.of(admin, cliente));

        mockMvc.perform(get("/api/v1/roles"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].nombre").value("ADMIN"))
            .andExpect(jsonPath("$[1].nombre").value("CLIENTE"));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void getRoles_listaVacia_devuelveMensaje() throws Exception {
        when(rolService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/roles"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.mensaje").value("No hay roles registrados"))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getRolById_existente_devuelveRol() throws Exception {
        Rol rol = new Rol(1L, "ADMIN", "Administrador del sistema", Set.of(Permiso.GESTIONAR_PERMISO));

        when(rolService.findById(1L)).thenReturn(Optional.of(rol));

        mockMvc.perform(get("/api/v1/roles/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.nombre").value("ADMIN"))
            .andExpect(jsonPath("$.data.descripcion").value("Administrador del sistema"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getRolById_inexistente_devuelve404() throws Exception {
        when(rolService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/roles/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.mensaje").value("No se encontró el rol con ID: 99"))
            .andExpect(jsonPath("$.data").doesNotExist());
    }

    //createRol
    @Test
    @WithMockUser(roles = "ADMIN")
    void createRol_valido_devuelve201ConRolCreado() throws Exception {
        Rol nuevoRol = new Rol(1L, "SOPORTE", "Soporte técnico", Set.of(Permiso.VER_USUARIO));

        when(rolService.existsByNombre("SOPORTE")).thenReturn(false);
        when(rolService.save(any(Rol.class))).thenReturn(nuevoRol);

        String json = """
        {
        "nombre": "SOPORTE",
        "descripcion": "Soporte técnico",
        "permiso": ["VER_USUARIO"]
        }
        """;
        mockMvc.perform(post("/api/v1/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.nombre").value("SOPORTE"))
            .andExpect(jsonPath("$.data.descripcion").value("Soporte técnico"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createRol_duplicado_devuelve409ConMensaje() throws Exception {
        when(rolService.existsByNombre("ADMIN")).thenReturn(true);

        String json = """
        {
        "nombre": "ADMIN",
        "descripcion": "Intento duplicado",
        "permisos": ["GESTIONAR_USUARIO"]
        }
        """;

        mockMvc.perform(post("/api/v1/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.mensaje").value("Ya existe un rol con el nombre: ADMIN"))
            .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateRol_existente_devuelve200ConRolActualizado() throws Exception {
        Long rolId = 1L;
        Rol original = new Rol(rolId, "ADMIN", "Administrador", Set.of(Permiso.VER_USUARIO));
        Rol actualizado = new Rol(rolId, "GESTOR", "Gestor de usuarios", Set.of(Permiso.VER_USUARIO));

        when(rolService.findById(rolId)).thenReturn(Optional.of(original));
        when(rolService.existsByNombre("GESTOR")).thenReturn(false);
        when(rolService.save(any(Rol.class))).thenReturn(actualizado);

        String json = """
        {
        "nombre": "GESTOR",
        "descripcion": "Gestor de usuarios",
        "permisos": ["VER_USUARIO"]
        }
        """;
        mockMvc.perform(put("/api/v1/roles/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.mensaje").value("Rol actualizado correctamente"))
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.nombre").value("GESTOR"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void actualizarRol_inexistente_devuelve404() throws Exception {
        when(rolService.findById(99L)).thenReturn(Optional.empty());

        String json = """
        {
        "nombre": "ADMIN",
        "descripcion": "Rol de admin",
        "permisos": ["GESTIONAR_USUARIO"]
        }
        """;

        mockMvc.perform(put("/api/v1/roles/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.mensaje").value("No se encontró el rol con ID: 99"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addPermiso_aRolExistente_devuelve200ConRolActualizado() throws Exception {
        Long rolId = 1L;
        Permiso permiso = Permiso.GESTIONAR_PERMISO;
        Rol rolConPermiso = new Rol(rolId, "ADMIN", "Administrador", Set.of(permiso));

        when(rolService.agregarPermiso(rolId, permiso)).thenReturn(rolConPermiso);

        mockMvc.perform(post("/api/v1/roles/1/permisos/GESTIONAR_PERMISO"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.permiso[0]").value("GESTIONAR_PERMISO"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addPermiso_aRolInexistente_devuelve404ConMensaje() throws Exception {
        Long rolId = 99L;
        Permiso permiso = Permiso.VER_USUARIO;

        when(rolService.agregarPermiso(rolId, permiso))
            .thenThrow(new RuntimeException("No se encontró el rol."));

        mockMvc.perform(post("/api/v1/roles/99/permisos/VER_USUARIO"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.mensaje").value("No se encontró el rol."));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void removePermiso_existente_devuelve200() throws Exception {
        Long rolId = 1L;
        Permiso permiso = Permiso.VER_USUARIO;
        Rol actualizado = new Rol(rolId, "ADMIN", "Administrador", Set.of());

        when(rolService.removerPermiso(rolId, permiso)).thenReturn(actualizado);

        mockMvc.perform(delete("/api/v1/roles/1/permisos/VER_USUARIO"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.permiso").isEmpty()); // ajustá esto si tu campo se llama permisos
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void removePermiso_deRolInexistente_devuelve404() throws Exception {
        when(rolService.removerPermiso(99L, Permiso.GESTIONAR_PERMISO))
            .thenThrow(new RuntimeException("No se encontró el rol."));

        mockMvc.perform(delete("/api/v1/roles/99/permisos/GESTIONAR_PERMISO"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.mensaje").value("No se encontró el rol."));
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteRol_existente_devuelve204() throws Exception {
        when(rolService.existsById(1L)).thenReturn(true);
        doNothing().when(rolService).deleteById(1L);
        mockMvc.perform(delete("/api/v1/roles/1"))
            .andExpect(status().isNoContent());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteRol_inexistente_devuelve404ConMensaje() throws Exception {
        doThrow(new RuntimeException("No se encontró el rol con ID: 99"))
            .when(rolService).deleteById(99L);

        mockMvc.perform(delete("/api/v1/roles/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.mensaje").value("No se encontró el rol con ID: 99"));
    }
}