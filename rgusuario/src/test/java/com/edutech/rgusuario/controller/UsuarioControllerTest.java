package com.edutech.rgusuario.controller;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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
import com.edutech.rgusuario.model.Estado;
import com.edutech.rgusuario.model.Rol;
import com.edutech.rgusuario.model.Usuario;
import com.edutech.rgusuario.service.RolService;
import com.edutech.rgusuario.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;


@WebMvcTest(controllers = UsuarioController.class)
@Import(SecurityConfig.class) // solo si tenés lógica de seguridad custom
//@ActiveProfiles("test")
@WithMockUser(username = "xime", roles = {"ADMIN"})
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private RolService rolService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @WithMockUser(roles = "ADMIN")
    void obtenerUsuarios_existentes_devuelve200ConLista() throws Exception {
        List<Usuario> lista = List.of(new Usuario(), new Usuario());
        when(usuarioService.findAll()).thenReturn(lista);

        mockMvc.perform(get("/api/v1/usuarios"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.mensaje").value("Lista de usuarios"))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void obtenerUsuarios_vacio_devuelve200ConMensaje() throws Exception {
        when(usuarioService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/usuarios"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.mensaje").value("No hay usuarios registrados"))
            .andExpect(jsonPath("$.data").isEmpty());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void obtenerUsuarioPorId_existente_devuelve200() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        when(usuarioService.findById(1L)).thenReturn(Optional.of(usuario));

        mockMvc.perform(get("/api/v1/usuarios/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.mensaje").value("Usuario encontrado"))
            .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void obtenerUsuarioPorId_inexistente_devuelve404() throws Exception {
        when(usuarioService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/usuarios/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.mensaje").value("No se encontró el usuario con ID: 99"));
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void obtenerUsuarioPorRut_existente_devuelve200() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setRut("12345678-9");
        when(usuarioService.findByRut("12345678-9")).thenReturn(Optional.of(usuario));

        mockMvc.perform(get("/api/v1/usuarios/rut/12345678-9"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.mensaje").value("Usuario con RUT encontrado"))
            .andExpect(jsonPath("$.data.rut").value("12345678-9"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void obtenerUsuarioPorRut_inexistente_devuelve404() throws Exception {
        when(usuarioService.findByRut("99999999-9")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/usuarios/rut/99999999-9"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.mensaje").value("No se encontró el usuario con RUT: 99999999-9"));
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void obtenerUsuarioPorEmail_existente_devuelve200() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setEmail("user@example.com");
        when(usuarioService.findByEmail("user@example.com")).thenReturn(Optional.of(usuario));

        mockMvc.perform(get("/api/v1/usuarios/email/user@example.com"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.mensaje").value("Usuario con email encontrado"))
            .andExpect(jsonPath("$.data.email").value("user@example.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void obtenerUsuarioPorEmail_inexistente_devuelve404() throws Exception {
        when(usuarioService.findByEmail("desconocido@example.com")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/usuarios/email/desconocido@example.com"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.mensaje").value("No se encontró el usuario con email: desconocido@example.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void obtenerUsuarioPorUsername_existente_devuelve200() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setUsername("xime");

        when(usuarioService.findByUsername("xime")).thenReturn(Optional.of(usuario));

        mockMvc.perform(get("/api/v1/usuarios/username/xime"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.mensaje").value("Usuario con username encontrado"))
            .andExpect(jsonPath("$.data.username").value("xime"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void obtenerUsuarioPorUsername_inexistente_devuelve404() throws Exception {
        when(usuarioService.findByUsername("noexiste")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/usuarios/username/noexiste"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.mensaje").value("No se encontró el usuario con username: noexiste"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void obtenerUsuariosPorEstado_existente_devuelve200() throws Exception {
        Usuario usuario1 = new Usuario();
        usuario1.setId(1L);

        List<Usuario> activos = List.of(usuario1);

        when(usuarioService.findByEstado(Estado.ACTIVO)).thenReturn(activos);

        mockMvc.perform(get("/api/v1/usuarios/estado/ACTIVO"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.mensaje").value("Usuarios con estado ACTIVO"))
            .andExpect(jsonPath("$.data[0].id").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void obtenerUsuariosPorEstado_sinCoincidencias_devuelve200ConMensaje() throws Exception {
        when(usuarioService.findByEstado(Estado.BLOQUEADO)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/usuarios/estado/BLOQUEADO"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.mensaje").value("No hay usuarios con ese estado."))
            .andExpect(jsonPath("$.data").isEmpty());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void crearUsuario_valido_devuelve201() throws Exception {
        Usuario nuevo = new Usuario();
        nuevo.setId(1L);
        nuevo.setUsername("admin");

        when(usuarioService.crearUsuario(any(Usuario.class))).thenReturn(nuevo);

        String json = """
        {
        "username": "admin",
        "rut": "11111111-1",
        "email": "admin@example.com",
        "primerNomb": "Admin",
        "primerApell": "Ejemplo",
        "fechaNacimiento": "1990-01-01"
        }
        """;

        mockMvc.perform(post("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.mensaje").value("Usuario creado correctamente"))
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.username").value("admin"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void actualizarUsuario_existente_devuelve200() throws Exception {
        Long id = 1L;

        Usuario existente = new Usuario();
        existente.setId(id);
        existente.setRut("12345678-9");
        existente.setUsername("user");
        existente.setEmail("user@correo.com");

        when(usuarioService.findById(id)).thenReturn(Optional.of(existente));
        when(usuarioService.existsByRut("12345678-9")).thenReturn(false);
        when(usuarioService.existsByUsername("user")).thenReturn(false);
        when(usuarioService.existsByEmail("user@correo.com")).thenReturn(false);
        when(usuarioService.save(any(Usuario.class))).thenReturn(existente);

        String json = """
        {
        "rut": "12345678-9",
        "username": "user",
        "email": "user@correo.com",
        "primerNomb": "Nuevo",
        "primerApell": "Nombre",
        "fechaNacimiento": "1990-01-01",
        "estado": "ACTIVO",
        "roles": []
        }
        """;

        mockMvc.perform(put("/api/v1/usuarios/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.mensaje").value("Usuario actualizado correctamente"))
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.username").value("user"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void agregarRol_aUsuario_existente_devuelve200() throws Exception {
        Long usuarioId = 1L;
        Long rolId = 2L;

        Rol rol = new Rol();
        rol.setId(rolId);
        Usuario actualizado = new Usuario();
        actualizado.setId(usuarioId);
        actualizado.setRoles(List.of(rol)); 
        when(rolService.findById(rolId)).thenReturn(Optional.of(rol));
        when(usuarioService.agregarRol(usuarioId, rol)).thenReturn(actualizado);

        mockMvc.perform(post("/api/v1/usuarios/1/roles/2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.mensaje").value("Rol agregado correctamente"))
            .andExpect(jsonPath("$.data.roles[0].id").value(2));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void removerRol_deUsuario_existente_devuelve200() throws Exception {
        Long usuarioId = 1L;
        Long rolId = 2L;

        Usuario actualizado = new Usuario();
        actualizado.setId(usuarioId);
        actualizado.setRoles(List.of()); 
        when(usuarioService.removerRol(usuarioId, rolId)).thenReturn(actualizado);

        mockMvc.perform(delete("/api/v1/usuarios/1/roles/2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.mensaje").value("Rol removido correctamente"))
            .andExpect(jsonPath("$.data.roles").isEmpty());
    }

    @Test
    void registrarCliente_valido_devuelve201() throws Exception {
        Usuario nuevo = new Usuario();
        nuevo.setId(1L);
        nuevo.setUsername("cliente1");

        when(usuarioService.registrarDesdeCliente(any(Usuario.class))).thenReturn(nuevo);

        String json = """
        {
        "username": "cliente1",
        "rut": "22222222-2",
        "email": "cliente1@example.com",
        "primerNomb": "Nombre",
        "primerApell": "Apellido",
        "fechaNacimiento": "2000-01-01"
        }
        """;

        mockMvc.perform(post("/api/v1/usuarios/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.mensaje").value("Usuario registrado correctamente"))
            .andExpect(jsonPath("$.data.username").value("cliente1"));
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void modificarInformacion_clienteValido_devuelve200() throws Exception {
        Long id = 1L;

        Usuario actualizado = new Usuario();
        actualizado.setId(id);
        actualizado.setEmail("nuevo@correo.com");

        when(usuarioService.findById(id)).thenReturn(Optional.of(actualizado));
        when(usuarioService.existsByEmail("nuevo@correo.com")).thenReturn(false);
        when(usuarioService.modificarInformacion(eq(id), anyString(), any(), anyString(), any(), anyString()))
            .thenReturn(actualizado);

        mockMvc.perform(put("/api/v1/usuarios/1/modificar")
                .param("primerNomb", "Nuevo")
                .param("primerApell", "Cliente")
                .param("email", "nuevo@correo.com"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.mensaje").value("Datos actualizados correctamente"))
            .andExpect(jsonPath("$.data.email").value("nuevo@correo.com"));
    }
    @Test
    @WithMockUser(roles = "CLIENTE")
    void modificarInformacion_emailDuplicado_devuelve409() throws Exception {
        Long id = 1L;
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setEmail("original@correo.com");

        when(usuarioService.findById(id)).thenReturn(Optional.of(usuario));
        when(usuarioService.existsByEmail("duplicado@correo.com")).thenReturn(true);

        mockMvc.perform(put("/api/v1/usuarios/1/modificar")
                .param("primerNomb", "Dup")
                .param("primerApell", "Licado")
                .param("email", "duplicado@correo.com"))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.mensaje").value("El email ya está registrado por otro usuario"));
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void actualizarUsuario_conRutDuplicado_devuelve409() throws Exception {
        Usuario existente = new Usuario();
        existente.setId(1L);
        existente.setRut("11111111-1");

        when(usuarioService.findById(1L)).thenReturn(Optional.of(existente));
        when(usuarioService.existsByRut("22222222-2")).thenReturn(true);

        String json = """
        {
        "rut": "22222222-2",
        "username": "user",
        "email": "user@correo.com",
        "primerNomb": "Nombre",
        "primerApell": "Apellido",
        "fechaNacimiento": "1990-01-01",
        "estado": "ACTIVO",
        "roles": []
        }
        """;

        mockMvc.perform(put("/api/v1/usuarios/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.mensaje").value("El RUT ya está registrado"));
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void actualizarUsuario_conUsernameDuplicado_devuelve409() throws Exception {
    Usuario existente = new Usuario();
    existente.setId(1L);
    existente.setRut("11111111-1");
    existente.setUsername("original");
    existente.setEmail("correo@ejemplo.com");

        when(usuarioService.findById(1L)).thenReturn(Optional.of(existente));
        when(usuarioService.existsByUsername("duplicado")).thenReturn(true);

        String json = """
        {
        "rut": "11111111-1",
        "username": "duplicado",
        "email": "correo@ejemplo.com",
        "primerNomb": "Nombre",
        "primerApell": "Apellido",
        "fechaNacimiento": "1990-01-01",
        "estado": "ACTIVO",
        "roles": []
        }
        """;

        mockMvc.perform(put("/api/v1/usuarios/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.mensaje").value("El username ya está registrado"));
    }
}