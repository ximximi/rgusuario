package com.edutech.rgusuario.service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.edutech.rgusuario.dto.RolDTO;
import com.edutech.rgusuario.dto.UsuarioDTO;
import com.edutech.rgusuario.model.Estado;
import com.edutech.rgusuario.model.Rol;
import com.edutech.rgusuario.model.Usuario;
import com.edutech.rgusuario.repository.RolRepository;
import com.edutech.rgusuario.repository.UsuarioRepository;

public class UsuarioServiceTest {
    @Mock //doble falso del repositorio, o sea que no se conecta a la bd
    private UsuarioRepository usuarioRepository;
    @Mock
    private RolRepository rolRepository;

    @Mock
    private RolService rolService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks //mock inyectado al servicio real
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this); // Inicializa los mocks y realiza la inyección de dependencias
    }

    private Usuario crearUsuarioValido() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("xime_dev");
        usuario.setEmail("xime@mail.com");
        usuario.setRut("12345678-9");
        usuario.setPrimerNomb("Ximena");
        usuario.setPrimerApell("Torres");
        usuario.setFechaNacimiento(LocalDate.of(1998, 5, 15));
        usuario.setContrasena("claveSegura123");
        usuario.setContrasenaHash("$2a$10$falsobcrypt");
        usuario.setFechaRegistro(Instant.now());
        usuario.setEstado(Estado.ACTIVO);
        //no hay para rol en beneficio del testing 
        return usuario;
    }


    //Test save()
    @Test
    void testSave(){//guardarUsuario
        Usuario usuario = new Usuario();//lo que se va a guardar
        Usuario usuarioGuardado = new Usuario();//simulación de lo devuelto por el repositorio
        usuarioGuardado.setId(1L);//1L porque el id se pone automático, y en realidad es lo único que saltaría como error
        //comportamiento simulado(mockeo) cuando llamen a save devuelve usuarioGuardado
        when(usuarioRepository.save(usuario)).thenReturn(usuarioGuardado);
        //se ejecuta el método real a testear
        Usuario resultado = usuarioService.save(usuario);
        //verificación
        assertThat(resultado.getId()).isEqualTo(1L);
        //confirmación de la llamada del método del repo con el objeto correcto
        verify(usuarioRepository).save(usuario);
    }

    //Test findall

    @Test
    void findAll_devuelveLista() {
        List<Usuario> usuarios = List.of(new Usuario(), new Usuario());
        when(usuarioRepository.findAll()).thenReturn(usuarios);

        List<Usuario> resultado = usuarioService.findAll();

        assertThat(resultado).hasSize(2);
        verify(usuarioRepository).findAll();
    }
    //Test FindById
    @Test
    void findById_existe() {
        Usuario usuario = new Usuario();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Optional<Usuario> resultado = usuarioService.findById(1L);

        assertThat(resultado).contains(usuario);
        verify(usuarioRepository).findById(1L);
    }
    @Test
    void findById_noExiste() {
        Long idBuscado = 99L;
        when(usuarioRepository.findById(idBuscado)).thenReturn(Optional.empty());

        Optional<Usuario> resultado = usuarioService.findById(idBuscado);

        assertThat(resultado).isEmpty();
        verify(usuarioRepository).findById(idBuscado);
    }
    //Test findByRut
    @Test
    void findByRut_Existe() {
        String rut = "12345678-9";
        Usuario usuario = new Usuario();
        when(usuarioRepository.findByRut(rut)).thenReturn(Optional.of(usuario));

        Optional<Usuario> resultado = usuarioService.findByRut(rut);

        assertThat(resultado).contains(usuario);
        verify(usuarioRepository).findByRut(rut);
    }

    @Test
    void findByRut_NoExiste() {
        String rut = "00000000-0";
        when(usuarioRepository.findByRut(rut)).thenReturn(Optional.empty());

        Optional<Usuario> resultado = usuarioService.findByRut(rut);

        assertThat(resultado).isEmpty();
        verify(usuarioRepository).findByRut(rut);
    }
    //Find Username
    @Test
    void findByUsername_existe() {
        String username = "xime_dev";
        Usuario usuario = new Usuario();
        when(usuarioRepository.findByUsername(username)).thenReturn(Optional.of(usuario));

        Optional<Usuario> resultado = usuarioService.findByUsername(username);

        assertThat(resultado).contains(usuario);
        verify(usuarioRepository).findByUsername(username);
    }

    @Test
    void findByUsername_noExiste() {
        String username = "fantasma";
        when(usuarioRepository.findByUsername(username)).thenReturn(Optional.empty());

        Optional<Usuario> resultado = usuarioService.findByUsername(username);

        assertThat(resultado).isEmpty();
        verify(usuarioRepository).findByUsername(username);
    }
    //Test FindByEstado. CUANDO HAY USUARIOS
    @Test
    void findByEstado_devuelveUsuariosConEstado() {
        Usuario u1 = new Usuario();
        Usuario u2 = new Usuario();
        when(usuarioRepository.findByEstado(Estado.ACTIVO)).thenReturn(List.of(u1, u2));

        List<Usuario> resultado = usuarioService.findByEstado(Estado.ACTIVO);

        assertThat(resultado).containsExactly(u1, u2);
        verify(usuarioRepository).findByEstado(Estado.ACTIVO);
    }
    //CUANDO NO HAY USUARIOS
    @Test
    void findByEstado_sinUsuarios() {//Bloqueado porque no hay nada allí
        when(usuarioRepository.findByEstado(Estado.BLOQUEADO)).thenReturn(List.of());

        List<Usuario> resultado = usuarioService.findByEstado(Estado.BLOQUEADO);

        assertThat(resultado).isEmpty();
        verify(usuarioRepository).findByEstado(Estado.BLOQUEADO);
    }
    //Find email
    @Test
    void findByEmail_Existe() {
        String email = "xime@mail.com";
        Usuario usuario = new Usuario();
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));

        Optional<Usuario> resultado = usuarioService.findByEmail(email);

        assertThat(resultado).contains(usuario);
        verify(usuarioRepository).findByEmail(email);
    }

    @Test
    void findByEmail_NoExiste() {
        String email = "inexistente@mail.com";
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.empty());

        Optional<Usuario> resultado = usuarioService.findByEmail(email);

        assertThat(resultado).isEmpty();
        verify(usuarioRepository).findByEmail(email);
    }    
    //Test DeleteById
    @Test
    void deleteById_eliminaCorrectamente() {
        Long id = 5L;

        usuarioService.deleteById(id);

        verify(usuarioRepository).deleteById(id);
    }

    //Test DesactivarById
    //spy
    @Test
    void desactivarUsuario_cambiaEstadoCorrectamente() {
        // Arrange
        Long id = 1L;
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setEstado(Estado.ACTIVO);

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class)))
            .thenAnswer(invoc -> invoc.getArgument(0));

        // Act
        usuarioService.desactivarUsuario(id);

        // Assert
        assertThat(usuario.getEstado()).isEqualTo(Estado.INACTIVO);
        verify(usuarioRepository).save(usuario);
    }

    //Test ExistById, true y False
    @Test
    void existsById_True() {
        when(usuarioRepository.existsById(7L)).thenReturn(true);

        boolean existe = usuarioService.existsById(7L);

        assertThat(existe).isTrue();
        verify(usuarioRepository).existsById(7L);
    }
    @Test
    void existsById_False() {
        when(usuarioRepository.existsById(7L)).thenReturn(false);

        boolean resultado = usuarioService.existsById(7L);

        assertThat(resultado).isFalse();
        verify(usuarioRepository).existsById(7L);
    }
    //Test ExistByRut, True y False
    @Test
    void existsByRut_True() {
        String rut = "12345678-9";
        when(usuarioRepository.existsByRut(rut)).thenReturn(true);

        boolean resultado = usuarioService.existsByRut(rut);

        assertThat(resultado).isTrue();
        verify(usuarioRepository).existsByRut(rut);
    }

    @Test
    void existsByRut_False() {
        String rut = "98765432-1";
        when(usuarioRepository.existsByRut(rut)).thenReturn(false);

        boolean resultado = usuarioService.existsByRut(rut);

        assertThat(resultado).isFalse();
        verify(usuarioRepository).existsByRut(rut);
    }

    //Test ExistsByUsername, True y False
    @Test
    void existsByUsername_True() {
        String username = "xime_dev";
        when(usuarioRepository.existsByUsername(username)).thenReturn(true);

        boolean resultado = usuarioService.existsByUsername(username);

        assertThat(resultado).isTrue();
        verify(usuarioRepository).existsByUsername(username);
    }

    @Test
    void existsByUsername_False() {
        String username = "devfantasma";
        when(usuarioRepository.existsByUsername(username)).thenReturn(false);

        boolean resultado = usuarioService.existsByUsername(username);

        assertThat(resultado).isFalse();
        verify(usuarioRepository).existsByUsername(username);
    }

    //Test ExistsByEmail, True y False
    @Test
    void existsByEmail_devuelveTrueCuandoExiste() {
        String email = "xime@mail.com";
        when(usuarioRepository.existsByEmail(email)).thenReturn(true);

        boolean resultado = usuarioService.existsByEmail(email);

        assertThat(resultado).isTrue();
        verify(usuarioRepository).existsByEmail(email);
    }

    @Test
    void existsByEmail_devuelveFalseCuandoNoExiste() {
        String email = "fantasma@mail.com";
        when(usuarioRepository.existsByEmail(email)).thenReturn(false);

        boolean resultado = usuarioService.existsByEmail(email);

        assertThat(resultado).isFalse();
        verify(usuarioRepository).existsByEmail(email);
    }



    //Test para crearUsuario()
    @Test
    //duplicado (username)
    void crearUsuario_excepcionSiUsernameYaExiste() {
        // Arrange
        Usuario usuario = crearUsuarioValido();
        usuario.setUsername("xime_dev"); // nombre en uso

        when(usuarioRepository.existsByUsername("xime_dev")).thenReturn(true);

        // Act + Assert
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> usuarioService.crearUsuario(usuario)
        );

        assertThat(ex.getMessage()).isEqualTo("El nombre de usuario ya está registrado.");

        verify(usuarioRepository).existsByUsername("xime_dev");
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }
        @Test
    // duplicado (email)
    void crearUsuario_excepcionSiEmailYaExiste() {
        // Arrange
        Usuario usuario = crearUsuarioValido();
        usuario.setEmail("xime@mail.com"); // email en uso

        when(usuarioRepository.existsByUsername(usuario.getUsername())).thenReturn(false);
        when(usuarioRepository.existsByEmail("xime@mail.com")).thenReturn(true);

        // Act + Assert
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> usuarioService.crearUsuario(usuario)
        );

        assertThat(ex.getMessage()).isEqualTo("El email ya está registrado.");

        verify(usuarioRepository).existsByUsername(usuario.getUsername());
        verify(usuarioRepository).existsByEmail("xime@mail.com");
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }
    @Test
    // duplicado (rut)
    void crearUsuario_excepcionSiRutYaExiste() {
        // Arrange
        Usuario usuario = crearUsuarioValido();
        usuario.setRut("12345678-9"); // rut en uso

        when(usuarioRepository.existsByUsername(usuario.getUsername())).thenReturn(false);
        when(usuarioRepository.existsByEmail(usuario.getEmail())).thenReturn(false);
        when(usuarioRepository.existsByRut("12345678-9")).thenReturn(true);

        // Act + Assert
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> usuarioService.crearUsuario(usuario)
        );

        assertThat(ex.getMessage()).isEqualTo("El RUT ingresado ya está registrado.");

        verify(usuarioRepository).existsByUsername(usuario.getUsername());
        verify(usuarioRepository).existsByEmail(usuario.getEmail());
        verify(usuarioRepository).existsByRut("12345678-9");
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }
    //crearUsuario ROL
    @Test
    // error de validación en roles
    void crearUsuario_excepcionSiRolNoExiste() {
        // Arrange
        Usuario usuario = crearUsuarioValido();

        Rol rolInvalido = new Rol();
        rolInvalido.setNombre("PIRATA");

        usuario.setRoles(List.of(rolInvalido));

        when(usuarioRepository.existsByUsername(usuario.getUsername())).thenReturn(false);
        when(usuarioRepository.existsByEmail(usuario.getEmail())).thenReturn(false);
        when(usuarioRepository.existsByRut(usuario.getRut())).thenReturn(false);

        when(rolService.findByNombre("PIRATA")).thenReturn(Optional.empty());

        // Act + Assert
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> usuarioService.crearUsuario(usuario)
        );

        assertThat(ex.getMessage()).isEqualTo("Rol no válido: PIRATA");

        verify(rolService).findByNombre("PIRATA");
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }
    @Test
    //en caso de que no venga con ningún rol se registra Cliente
    void crearUsuario_asignaRolClienteSiNoSeEnviaNinguno() {
        // 1. Arrange
        Usuario usuario = crearUsuarioValido();
        usuario.setRoles(null); // Simulamos que no viene con roles

        when(usuarioRepository.existsByUsername(usuario.getUsername())).thenReturn(false);
        when(usuarioRepository.existsByEmail(usuario.getEmail())).thenReturn(false);
        when(usuarioRepository.existsByRut(usuario.getRut())).thenReturn(false);

        Rol rolCliente = new Rol();
        rolCliente.setId(1L);
        rolCliente.setNombre("CLIENTE");

        when(rolService.obtenerRolCliente()).thenReturn(rolCliente);
        when(usuarioRepository.save(any(Usuario.class)))
            .thenAnswer(invocation -> invocation.getArgument(0)); // devuelve el usuario
            //InvocationOnMock
            //invocation para que no devuelva null, simula el save porque mock
            //invoca los argumentos que se le pasaron al método, index 0 porque solo fue un usuario
        // 2. Act
        Usuario resultado = usuarioService.crearUsuario(usuario);

        // 3. Assert
        assertThat(resultado.getRoles())
            .hasSize(1)
            .extracting("nombre")
            .containsExactly("CLIENTE");

        verify(rolService).obtenerRolCliente();
        verify(usuarioRepository).save(usuario);
    }

    @Test
    //en caso de poner algo en rol pero este no exista
    void crearUsuario_lanzaExcepcionSiRolNoExistePorNombre() {
        // Arrange
        Usuario usuario = crearUsuarioValido();
        Rol rolInexistente = new Rol();
        rolInexistente.setNombre("HACKER"); // rol inventado
        usuario.setRoles(List.of(rolInexistente));

        when(usuarioRepository.existsByUsername(usuario.getUsername())).thenReturn(false);
        when(usuarioRepository.existsByEmail(usuario.getEmail())).thenReturn(false);
        when(usuarioRepository.existsByRut(usuario.getRut())).thenReturn(false);

        when(rolService.findByNombre("HACKER")).thenReturn(Optional.empty()); // simulamos que no existe

        // Act + Assert
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> usuarioService.crearUsuario(usuario)
        );

        assertThat(ex.getMessage()).isEqualTo("Rol no válido: HACKER");

        verify(rolService).findByNombre("HACKER");
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    //que la contraseña en texto plano se encripte, antes no entregaba Null, ahora lo cambié
    void crearUsuario_encriptaContrasenaAntesDeGuardar() {
        // 1. Arrange
        Usuario usuario = crearUsuarioValido(); // incluye contraseña
        usuario.setRoles(null); // para que se asigne el rol por defecto

        when(usuarioRepository.existsByUsername(usuario.getUsername())).thenReturn(false);
        when(usuarioRepository.existsByEmail(usuario.getEmail())).thenReturn(false);
        when(usuarioRepository.existsByRut(usuario.getRut())).thenReturn(false);

        Rol rolCliente = new Rol();
        rolCliente.setId(1L);
        rolCliente.setNombre("CLIENTE");

        when(rolService.obtenerRolCliente()).thenReturn(rolCliente);

        when(usuarioRepository.save(any(Usuario.class)))
            .thenAnswer(invoc -> invoc.getArgument(0));

        // 2. Act
        Usuario resultado = usuarioService.crearUsuario(usuario);

        // 3. Assert
        assertThat(resultado.getContrasena()).isNull(); // campo en texto plano limpiado
        assertThat(resultado.getContrasenaHash()).isNotBlank();//contrasenaHash que no esté vacío
        assertThat(resultado.getContrasenaHash()).startsWith("$2a$"); // patrón típico de BCrypt
        assertThat(resultado.getContrasenaHash()).isNotEqualTo("claveSegura123"); //que no sea igual al original
    }

    @Test
    //que funcione completamente con un usuario valido
    void crearUsuario_usuarioValido() {
        // 1. Arrange
        Usuario usuario = crearUsuarioValido();
        usuario.setRoles(null); // simula que no envía roles

        when(usuarioRepository.existsByUsername(usuario.getUsername())).thenReturn(false);
        when(usuarioRepository.existsByEmail(usuario.getEmail())).thenReturn(false);
        when(usuarioRepository.existsByRut(usuario.getRut())).thenReturn(false);

        Rol rolCliente = new Rol();
        rolCliente.setId(1L);
        rolCliente.setNombre("CLIENTE");
        when(rolService.obtenerRolCliente()).thenReturn(rolCliente);

        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invoc -> invoc.getArgument(0));

        // 2. Act
        Usuario resultado = usuarioService.crearUsuario(usuario);

        // 3. Assert
        assertThat(resultado.getUsername()).isEqualTo("xime_dev");
        assertThat(resultado.getEmail()).isEqualTo("xime@mail.com");
        assertThat(resultado.getRut()).isEqualTo("12345678-9");

        assertThat(resultado.getEstado()).isEqualTo(Estado.ACTIVO);
        assertThat(resultado.getFechaRegistro()).isNotNull();

        assertThat(resultado.getRoles()).hasSize(1);
        assertThat(resultado.getRoles().get(0).getNombre()).isEqualTo("CLIENTE");

        assertThat(resultado.getContrasenaHash()).isNotBlank();
        assertThat(resultado.getContrasenaHash()).startsWith("$2a$");
        assertThat(resultado.getContrasena()).isNull(); //comprobar que la contrena de texto plano se vació

        verify(usuarioRepository).save(usuario);
        verify(rolService).obtenerRolCliente();
    }

    @Test
    //en caso de que se guardo un rol que SÍ exista y no se fuerce rol CLIENTE
    void crearUsuario_conRolAdmin_Exito() {
        // 1. Arrange
        Usuario usuario = crearUsuarioValido();
        Rol rolAdminEnviado = new Rol();
        rolAdminEnviado.setNombre("ADMIN");
        usuario.setRoles(List.of(rolAdminEnviado)); // explícitamente se envía el rol

        // Validaciones de duplicados
        when(usuarioRepository.existsByUsername(usuario.getUsername())).thenReturn(false);
        when(usuarioRepository.existsByEmail(usuario.getEmail())).thenReturn(false);
        when(usuarioRepository.existsByRut(usuario.getRut())).thenReturn(false);

        // Simula que el rol ADMIN existe en la base
        Rol rolAdminReal = new Rol();
        rolAdminReal.setId(2L);
        rolAdminReal.setNombre("ADMIN");
        when(rolService.findByNombre("ADMIN")).thenReturn(Optional.of(rolAdminReal));

        // Simula que guarda devolviendo el mismo usuario
        when(usuarioRepository.save(any(Usuario.class)))
            .thenAnswer(invoc -> invoc.getArgument(0));

        // 2. Act
        Usuario resultado = usuarioService.crearUsuario(usuario);

        // 3. Assert
        assertThat(resultado.getRoles()).hasSize(1);
        assertThat(resultado.getRoles().get(0).getNombre()).isEqualTo("ADMIN");

        assertThat(resultado.getContrasenaHash()).isNotBlank();
        assertThat(resultado.getContrasena()).isNull();
        assertThat(resultado.getEstado()).isEqualTo(Estado.ACTIVO);
        assertThat(resultado.getFechaRegistro()).isNotNull();

        verify(rolService).findByNombre("ADMIN");
        verify(rolService, never()).obtenerRolCliente(); // No debería usarse aquí
        verify(usuarioRepository).save(usuario);
    }

    //Test para registrarDesdeCliente()
    @Test
    // duplicado (username)
    void registrarDesdeCliente_excepcionSiUsernameExiste() {
        // Arrange
        Usuario usuario = crearUsuarioValido();
        usuario.setUsername("xime_dev");

        when(usuarioRepository.existsByUsername("xime_dev")).thenReturn(true);

        // Act + Assert
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> usuarioService.registrarDesdeCliente(usuario)
        );

        assertThat(ex.getMessage()).isEqualTo("El nombre de usuario ya está registrado.");

        verify(usuarioRepository).existsByUsername("xime_dev");
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }
    @Test
    // duplicado (email)
    void registrarDesdeCliente_excepcionSiEmailExiste() {
        // Arrange
        Usuario usuario = crearUsuarioValido();
        usuario.setEmail("xime@mail.com");

        when(usuarioRepository.existsByUsername(usuario.getUsername())).thenReturn(false);
        when(usuarioRepository.existsByEmail("xime@mail.com")).thenReturn(true);

        // Act + Assert
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> usuarioService.registrarDesdeCliente(usuario)
        );

        assertThat(ex.getMessage()).isEqualTo("El email ya está registrado.");

        verify(usuarioRepository).existsByUsername(usuario.getUsername());
        verify(usuarioRepository).existsByEmail("xime@mail.com");
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }
    @Test
    // duplicado (rut)
    void registrarDesdeCliente_excepcionSiRutExiste() {
        // Arrange
        Usuario usuario = crearUsuarioValido();
        usuario.setRut("12345678-9");

        when(usuarioRepository.existsByUsername(usuario.getUsername())).thenReturn(false);
        when(usuarioRepository.existsByEmail(usuario.getEmail())).thenReturn(false);
        when(usuarioRepository.existsByRut("12345678-9")).thenReturn(true);

        // Act + Assert
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> usuarioService.registrarDesdeCliente(usuario)
        );

        assertThat(ex.getMessage()).isEqualTo("El RUT ingresado ya está registrado.");

        verify(usuarioRepository).existsByUsername(usuario.getUsername());
        verify(usuarioRepository).existsByEmail(usuario.getEmail());
        verify(usuarioRepository).existsByRut("12345678-9");
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }
    @Test
    //valida que:
    //no haya datos duplicados, la contraseña se encripta, se asigne rol CLIENTE, se setee ACTIVO y su fecha, también de que se guarde bien (save())
    void registrarDesdeCliente_exito() {
        // 1. Arrange
        Usuario usuario = new Usuario();
        usuario.setUsername("xime_dev");
        usuario.setEmail("xime@mail.com");
        usuario.setRut("12345678-9");
        usuario.setPrimerNomb("Ximena");
        usuario.setPrimerApell("Torres");
        usuario.setFechaNacimiento(LocalDate.of(1998, 5, 15));
        usuario.setContrasena("clave123");

        when(usuarioRepository.existsByUsername("xime_dev")).thenReturn(false);
        when(usuarioRepository.existsByEmail("xime@mail.com")).thenReturn(false);
        when(usuarioRepository.existsByRut("12345678-9")).thenReturn(false);

        Rol rolCliente = new Rol();
        rolCliente.setId(1L);
        rolCliente.setNombre("CLIENTE");

        when(rolService.obtenerRolCliente()).thenReturn(rolCliente);
        when(usuarioRepository.save(any(Usuario.class)))
            .thenAnswer(invoc -> invoc.getArgument(0)); // devuelve el mismo usuario guardado

        // 2. Act
        Usuario resultado = usuarioService.registrarDesdeCliente(usuario);

        // 3. Assert
        assertThat(resultado.getUsername()).isEqualTo("xime_dev");
        assertThat(resultado.getEmail()).isEqualTo("xime@mail.com");
        assertThat(resultado.getRut()).isEqualTo("12345678-9");

        assertThat(resultado.getRoles())
            .hasSize(1)
            .extracting("nombre")
            .containsExactly("CLIENTE");

        assertThat(resultado.getContrasenaHash()).isNotBlank();
        assertThat(resultado.getContrasenaHash()).startsWith("$2a$");
        assertThat(resultado.getContrasena()).isNull();
        assertThat(resultado.getEstado()).isEqualTo(Estado.ACTIVO);
        assertThat(resultado.getFechaRegistro()).isNotNull();

        verify(usuarioRepository).save(any(Usuario.class));
        verify(rolService).obtenerRolCliente();
    }

    //Test para modificarInformacion()
    @Test
    //valida que: se busque bien el usuario por id, se apliquen las mods, que se guarde y que no lance excepcion 
    void modificarInformacion_funcionaCorrectamenteCuandoUsuarioExiste() {
        // Arrange
        Long id = 1L;
        Usuario usuarioExistente = crearUsuarioValido();

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuarioExistente));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invoc -> invoc.getArgument(0));

        // Datos a modificar
        String nuevoNombre = "XimeMod";
        String nuevoSegundoNombre = "Alejandra";
        String nuevoApellido = "TorresMod";
        String nuevoSegundoApellido = "Muñoz";
        String nuevoEmail = "nuevo@mail.com";

        // Act
        Usuario resultado = usuarioService.modificarInformacion(
            id,
            nuevoNombre,
            nuevoSegundoNombre,
            nuevoApellido,
            nuevoSegundoApellido,
            nuevoEmail
        );

        // Assert
        assertThat(resultado.getPrimerNomb()).isEqualTo(nuevoNombre);
        assertThat(resultado.getSegundoNomb()).isEqualTo(nuevoSegundoNombre);
        assertThat(resultado.getPrimerApell()).isEqualTo(nuevoApellido);
        assertThat(resultado.getSegundoApell()).isEqualTo(nuevoSegundoApellido);
        assertThat(resultado.getEmail()).isEqualTo(nuevoEmail);

        verify(usuarioRepository).findById(id);
        verify(usuarioRepository).save(usuarioExistente);
    }
    //Test para cambiarEstado()
    @Test
    //lo que dice el nombre del test
    void cambiarEstado_actualizaCorrectamenteCuandoUsuarioExiste() {
        // 1. Arrange
        Long id = 1L;
        Usuario usuario = crearUsuarioValido(); // Estado inicial: ACTIVO
        Estado nuevoEstado = Estado.INACTIVO;

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class)))
            .thenAnswer(invoc -> invoc.getArgument(0));

        // 2. Act
        Usuario resultado = usuarioService.cambiarEstado(id, nuevoEstado);

        // 3. Assert
        assertThat(resultado.getEstado()).isEqualTo(Estado.INACTIVO);

        verify(usuarioRepository).findById(id);
        verify(usuarioRepository).save(usuario);
    }
    //Test para agregarRol()
    @Test
    void agregarRol_agregaRolCorrectamenteCuandoUsuarioExiste() {
        // Arrange
        Long usuarioId = 1L;
        Usuario usuario = crearUsuarioValido();

        Rol nuevoRol = new Rol();
        nuevoRol.setId(2L);
        nuevoRol.setNombre("ADMIN");

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invoc -> invoc.getArgument(0));

        // Act
        Usuario resultado = usuarioService.agregarRol(usuarioId, nuevoRol);

        // Assert
        assertThat(resultado.getRoles()).contains(nuevoRol);
        verify(usuarioRepository).findById(usuarioId);
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void agregarRol_lanzaExcepcionCuandoUsuarioNoExiste() {
        // Arrange
        Long usuarioId = 99L;
        Rol rol = new Rol();
        rol.setId(1L);
        rol.setNombre("CLIENTE");

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());

        // Act + Assert
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> usuarioService.agregarRol(usuarioId, rol)
        );

        assertThat(ex.getMessage()).contains("Usuario no encontrado con ID: " + usuarioId);
        verify(usuarioRepository).findById(usuarioId);
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void agregarRol_lanzaExcepcionCuandoRolYaEstaAsignado() {
        // Arrange
        Long usuarioId = 1L;
        Rol rolDuplicado = new Rol();
        rolDuplicado.setId(2L);
        rolDuplicado.setNombre("ADMIN");

        Usuario usuario = crearUsuarioValido();
        usuario.setRoles(new ArrayList<>(List.of(rolDuplicado))); // ya lo tiene asignado

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));

        // Act + Assert
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> usuarioService.agregarRol(usuarioId, rolDuplicado)
        );

        assertThat(ex.getMessage()).isEqualTo("El rol ya está asignado al usuario.");
        verify(usuarioRepository).findById(usuarioId);
        verify(usuarioRepository, never()).save(any());
    }

    //Test para removerRol()
    @Test
    void removerRol_eliminaRolCuandoUsuarioExiste() {
        // Arrange
        Long usuarioId = 1L;
        Long rolIdAEliminar = 2L;

        Usuario usuario = crearUsuarioValido();
        Rol rol = new Rol();
        rol.setId(rolIdAEliminar);
        rol.setNombre("ADMIN");
        usuario.setRoles(new ArrayList<>(List.of(rol))); // asignamos rol ADMIN

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invoc -> invoc.getArgument(0));

        // Act
        Usuario resultado = usuarioService.removerRol(usuarioId, rolIdAEliminar);

        // Assert
        assertThat(resultado.getRoles()).doesNotContain(rol);
        verify(usuarioRepository).findById(usuarioId);
        verify(usuarioRepository).save(usuario);
    }
    //EXCEPTION
    @Test
    void removerRol_lanzaExcepcionCuandoUsuarioNoExiste() {
        // Arrange
        Long usuarioId = 99L;
        Long rolId = 2L;

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());

        // Act + Assert
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> usuarioService.removerRol(usuarioId, rolId)
        );

        assertThat(ex.getMessage()).contains("Usuario no encontrado");
        verify(usuarioRepository).findById(usuarioId);
        verify(usuarioRepository, never()).save(any());
    }
    @Test
    void removerRol_lanzaExcepcionCuandoRolNoEstaAsignado() {
        // Arrange
        Long usuarioId = 1L;
        Long rolIdInexistente = 3L;

        Usuario usuario = crearUsuarioValido();
        usuario.setRoles(new ArrayList<>()); // sin roles asignados

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));

        // Act + Assert
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> usuarioService.removerRol(usuarioId, rolIdInexistente)
        );

        assertThat(ex.getMessage()).isEqualTo("El usuario no tiene asignado el rol con ID: " + rolIdInexistente);
        verify(usuarioRepository).findById(usuarioId);
        verify(usuarioRepository, never()).save(any());
    }










    //DTO
    //Test para findUsuarioDTOById()
    @Test
    void findUsuarioDTOById_devuelveDTOCuandoUsuarioExiste() {
        // Arrange
        Long id = 1L;
        Usuario usuario = crearUsuarioValido(); 
        usuario.setId(id);
        Rol rol = new Rol();
        rol.setId(1L);
        rol.setNombre("CLIENTE");
        usuario.setRoles(List.of(rol));

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));

        // Act
        Optional<UsuarioDTO> resultado = usuarioService.findUsuarioDTOById(id);

        // Assert
        assertThat(resultado).isPresent();
        UsuarioDTO dto = resultado.get();
        assertThat(dto.getId()).isEqualTo(id);
        assertThat(dto.getUsername()).isEqualTo(usuario.getUsername());
        assertThat(dto.getEmail()).isEqualTo(usuario.getEmail());
        assertThat(dto.getEstado()).isEqualTo(usuario.getEstado());
        assertThat(dto.getRoles())
            .extracting(RolDTO::getNombre)
            .contains("CLIENTE");//por si hay más roles

        verify(usuarioRepository).findById(id);
    }

    @Test
    //Optional.empty
    void findUsuarioDTOById_devuelveEmptySiNoExiste() {
        // Arrange
        Long id = 99L;
        when(usuarioRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        Optional<UsuarioDTO> resultado = usuarioService.findUsuarioDTOById(id);

        // Assert
        assertThat(resultado).isEmpty();
        verify(usuarioRepository).findById(id);
    }
    //Test para findUsuarioDTOByEstado()
    @Test
    void findUsuariosDTOByEstado_devuelveListaDeDTOsConEstadoDado() {
        // Arrange
        Rol rol = new Rol();
        rol.setId(1L);
        rol.setNombre("CLIENTE");

        Usuario usuario1 = crearUsuarioValido();
        usuario1.setId(1L);
        usuario1.setUsername("xime_1");
        usuario1.setRoles(List.of(rol));

        Usuario usuario2 = crearUsuarioValido();
        usuario2.setId(2L);
        usuario2.setUsername("xime_2");
        usuario2.setRoles(List.of(rol));

        when(usuarioRepository.findByEstado(Estado.ACTIVO))
            .thenReturn(List.of(usuario1, usuario2));

        // Act
        List<UsuarioDTO> resultado = usuarioService.findUsuariosDTOByEstado(Estado.ACTIVO);

        // Assert
        assertThat(resultado).hasSize(2);
        assertThat(resultado)
            .extracting("username")
            .containsExactlyInAnyOrder("xime_1", "xime_2");

        assertThat(resultado.get(0).getRoles().get(0).getNombre()).isEqualTo("CLIENTE");

        verify(usuarioRepository).findByEstado(Estado.ACTIVO);
    }
}
