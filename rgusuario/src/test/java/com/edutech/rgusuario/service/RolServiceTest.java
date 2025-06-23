package com.edutech.rgusuario.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.edutech.rgusuario.model.Permiso;
import com.edutech.rgusuario.model.Rol;
import com.edutech.rgusuario.repository.RolRepository;

public class RolServiceTest {
    
    @Mock
    private RolRepository rolRepository;

    @InjectMocks //mock inyectado al servicio real
    private RolService rolService;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this); // Inicializa los mocks y realiza la inyección de dependencias
    }

    //Test findAll
    @Test
    void findAll_devuelveTodosLosRoles() {
        List<Rol> roles = List.of(new Rol(1L, "CLIENTE", null, Set.of()), new Rol(2L, "ADMIN", null, Set.of()));
        when(rolRepository.findAll()).thenReturn(roles);

        List<Rol> resultado = rolService.findAll();

        assertThat(resultado).hasSize(2);
        verify(rolRepository).findAll();
    }
    //Test para findById
    @Test
    void findById_devuelveRolSiExiste() {
        Rol rol = new Rol(1L, "CLIENTE", "Acceso básico", Set.of());
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rol));

        Optional<Rol> resultado = rolService.findById(1L);

        assertThat(resultado).contains(rol);
        verify(rolRepository).findById(1L);
    }

    //Test para findByNombre
    @Test
    void findByNombre_devuelveRolPorNombre() {
        Rol rol = new Rol(2L, "ADMIN", "Acceso total", Set.of());
        when(rolRepository.findByNombre("ADMIN")).thenReturn(Optional.of(rol));

        Optional<Rol> resultado = rolService.findByNombre("ADMIN");

        assertThat(resultado).contains(rol);
        verify(rolRepository).findByNombre("ADMIN");
    }

    //Test para save (guardar rol)
    @Test
    void save_persisteRolCorrectamente() {
        Rol rol = new Rol(null, "VETERINARIO", "Acceso clínico", Set.of());
        when(rolRepository.save(rol)).thenReturn(rol);

        Rol guardado = rolService.save(rol);

        assertThat(guardado).isEqualTo(rol);
        verify(rolRepository).save(rol);
    }
    //Test para deleteById
    @Test
    void deleteById_eliminaCorrectamente() {
        Long id = 5L;

        rolService.deleteById(id);

        verify(rolRepository).deleteById(id);
    }
    //Test para existsById
    @Test
    void existsById_retornaTrueCuandoExiste() {
        when(rolRepository.existsById(1L)).thenReturn(true);

        boolean existe = rolService.existsById(1L);

        assertThat(existe).isTrue();
        verify(rolRepository).existsById(1L);
    }
    //Test para existBYNombre
    @Test
    void existsByNombre_retornaTrueCuandoExiste() {
        when(rolRepository.existsByNombre("CLIENTE")).thenReturn(true);

        boolean existe = rolService.existsByNombre("CLIENTE");

        assertThat(existe).isTrue();
        verify(rolRepository).existsByNombre("CLIENTE");
    }
    //Test para agregarPermiso
    @Test
    void agregarPermiso_agregaYGuardaRol() {
        Rol rol = new Rol(3L, "GERENTE", "Acceso avanzado", new HashSet<>());
        Permiso permiso = Permiso.GESTIONAR_PERMISO;

        when(rolRepository.findById(3L)).thenReturn(Optional.of(rol));
        when(rolRepository.save(rol)).thenReturn(rol);

        Rol resultado = rolService.agregarPermiso(3L, permiso);

        assertThat(resultado.getPermiso()).contains(permiso);
        verify(rolRepository).save(rol);
    }
    //Test para removerPermiso
    @Test
    void removerPermiso_remueveYGuardaRol() {
        Permiso permiso = Permiso.ELIMINAR_USUARIO;
        Rol rol = new Rol(4L, "ADMIN", "Acceso total", new HashSet<>(Set.of(permiso)));

        when(rolRepository.findById(4L)).thenReturn(Optional.of(rol));
        when(rolRepository.save(rol)).thenReturn(rol);

        Rol resultado = rolService.removerPermiso(4L, permiso);

        assertThat(resultado.getPermiso()).doesNotContain(permiso);
        verify(rolRepository).save(rol);
    }

    //Test para obtenerRolCliente. Retorna rol CLIENTE para asignarlo automáticamente a nuevos usuarios al registrarse.
    //usado en UsuarioService en registro
    @Test
    void obtenerRolCliente_devuelveRolCliente() {
        Rol rolCliente = new Rol(1L, "CLIENTE", "Acceso de usuario final", Set.of());
        when(rolRepository.findByNombre("CLIENTE")).thenReturn(Optional.of(rolCliente));

        Rol resultado = rolService.obtenerRolCliente();

        assertThat(resultado.getNombre()).isEqualTo("CLIENTE");
        verify(rolRepository).findByNombre("CLIENTE");
    }
}
