package com.edutech.rgusuario.service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.edutech.rgusuario.dto.RolDTO;
import com.edutech.rgusuario.dto.UsuarioDTO;
import com.edutech.rgusuario.model.Estado;
import com.edutech.rgusuario.model.Rol;
import com.edutech.rgusuario.model.Usuario;
import com.edutech.rgusuario.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service

public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private RolService rolService;
    //Transacción completa
    //Si ocurre una exception, los cambios se revierten
    @Transactional
    public Usuario crearUsuario(Usuario usuario){
        //Por si se repiten los datos ya registrados
        if (usuarioRepository.existsByUsername(usuario.getUsername())){
            throw new IllegalArgumentException("El nombre de usuario ya está registrado.");
        }
        if (usuarioRepository.existsByEmail(usuario.getEmail())){
            throw new IllegalArgumentException("El email ya está registrado.");
        }
        if (usuarioRepository.existsByRut(usuario.getRut())){
            throw new IllegalArgumentException("El RUT ingresado ya está registrado.");
        }
        // Validación de roles
        if (usuario.getRoles() == null || usuario.getRoles().isEmpty()) {
            //se asigna rol cliente si no se especificó
            Rol rolCliente = rolService.obtenerRolCliente();
            usuario.setRoles(Collections.singletonList(rolCliente));
        } else { //el rol asignado tiene que existir
            List<Rol> rolesValidos = usuario.getRoles().stream()//recorre cada rol adjunto a Usuario
                .map(r -> rolService.findByNombre(r.getNombre())//verifica que exista, busca el rol por nombre
                    .orElseThrow(() -> new IllegalArgumentException("Rol no válido: " + r.getNombre())))
                .collect(Collectors.toList());//se juntan los Rol válidos encontrados en una lista
            usuario.setRoles(rolesValidos);//se reemplaza por esta lista verificada
        }
        //se da los datos por defecto: fechaRegistro y estado
        usuario.crearCuenta();
        //Ahora, encriptación de contraseña
        String hash = BCrypt.hashpw(usuario.getContrasena(), BCrypt.gensalt());
        usuario.setContrasenaHash(hash);
        usuario.setContrasena(null); 
        //guarda usuario en la bd
        return usuarioRepository.save(usuario);
    }
    //registro DESDE PERSPECTIVA CLIENTE
    @Transactional
    public Usuario registrarDesdeCliente(Usuario usuario) {
        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya está registrado.");
        }
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado.");
        }
        if (usuarioRepository.existsByRut(usuario.getRut())) {
            throw new IllegalArgumentException("El RUT ingresado ya está registrado.");
        }

        // Encriptar contraseña
        String hash = BCrypt.hashpw(usuario.getContrasena(), BCrypt.gensalt());
        usuario.setContrasenaHash(hash);
        usuario.setContrasena(null); // cambiado por el testing

        // Asignar rol por defecto
        Rol rolCliente = rolService.obtenerRolCliente();
        usuario.setRoles(Collections.singletonList(rolCliente));

        // Setear estado y fecha
        usuario.crearCuenta();

        return usuarioRepository.save(usuario);
    }

    //Lista de usuarios
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }
    
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }
    
    public Optional<Usuario> findByRut(String rut) {
        return usuarioRepository.findByRut(rut);
    }
    
    public Optional<Usuario> findByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }
    //Optional porque busca uno sólo que debería ser único
    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }
    //List porque devuelve una lista que puede tener uno, cero o muchos
    //se busca varios, muchos usarios comparten el mimso estado
    public List<Usuario> findByEstado(Estado estado) {
        return usuarioRepository.findByEstado(estado);
    }
    
    public Usuario save(Usuario usuario) {
        //en caso de que ya tenga una fecha asignada (por migración o testing)
        if (usuario.getFechaRegistro() == null) {
            usuario.setFechaRegistro(Instant.now());
        }
        return usuarioRepository.save(usuario);
    }
    
    //borra la cuenta
    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }
    //sof delete, cambia estado a INACTIVO
    public void desactivarUsuario(Long id) {
        cambiarEstado(id, Estado.INACTIVO);
    }

    public boolean existsById(Long id) {
        return usuarioRepository.existsById(id);
    }
    
    public boolean existsByRut(String rut) {
        return usuarioRepository.existsByRut(rut);
    }
    
    public boolean existsByUsername(String username) {
        return usuarioRepository.existsByUsername(username);
    }
    
    public boolean existsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }
    


    public Usuario modificarInformacion(Long id, String primerNomb, String segundoNomb, String primerApell, String segundoApell, String email) {
        Usuario usuario = usuarioRepository.findById(id)
            //illegalArgument es sólo una subclase de runtime, es más específica.
            .orElseThrow(()-> new IllegalArgumentException("No se encontró el usuario con Id: "+ id));
        //si se encuentra el usuario entonces se modifica la información 
        usuario.modificarInformacion(primerNomb, segundoNomb, primerApell, segundoApell, email);
        return usuarioRepository.save(usuario);
    }
    

    public Usuario cambiarEstado(Long id, Estado estado) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(()-> new IllegalArgumentException("No se encontró el usuario con Id: "+ id));
        usuario.cambiarEstado(estado);
        return usuarioRepository.save(usuario);
    }
    
    public Usuario agregarRol(Long usuarioId, Rol rol) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + usuarioId));

        usuario.agregarRol(rol);
        return usuarioRepository.save(usuario);
    }
    
    public Usuario removerRol(Long usuarioId, Long rolId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        usuario.removerRolPorId(rolId);
        return usuarioRepository.save(usuario);
    }

    //USUARIO DTO
    //encuentra el id y devuelve un optional
    public Optional<UsuarioDTO> findUsuarioDTOById(Long id) {
        return usuarioRepository.findById(id)
            //si se encuentra entonces con map se transforma el usuario en un usuarioDTO
            .map(usuario -> new UsuarioDTO(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getEmail(),
                usuario.getEstado(),
                usuario.getRoles().stream()//recorre la lista
                    //se pasa rol a rolDTO, sólo con la información necesaria
                    .map(rol -> new RolDTO(rol.getId(), rol.getNombre()))
                    .collect(Collectors.toList())//collect para reunirlo en una lista (parámetro de usuarioDTO)
            ));
        }
    //encuentra los usuarios dependiendo del estado(filtra)
    public List<UsuarioDTO> findUsuariosDTOByEstado(Estado estado) {
        return usuarioRepository.findByEstado(estado).stream()//devuelve una lista
            .map(usuario -> new UsuarioDTO(//crea un dto de usuario
                usuario.getId(),
                usuario.getUsername(),
                usuario.getEmail(),
                usuario.getEstado(),
                usuario.getRoles().stream()
                    .map(rol -> new RolDTO(rol.getId(), rol.getNombre()))
                    .collect(Collectors.toList())
            ))
            .collect(Collectors.toList());
    }




}
