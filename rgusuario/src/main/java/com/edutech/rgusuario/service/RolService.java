package com.edutech.rgusuario.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.edutech.rgusuario.model.Permiso;
import com.edutech.rgusuario.model.Rol;
import com.edutech.rgusuario.repository.RolRepository;

@Service
public class RolService {
    @Autowired
    private RolRepository rolRepository;
    
    public List<Rol> findAll(){
        return rolRepository.findAll();
    }
    
    public Optional<Rol> findById(Long id) {
        return rolRepository.findById(id);
    }
    
    public Optional<Rol> findByNombre(String nombre) {
        return rolRepository.findByNombre(nombre);
    }
    
    public Rol save(Rol rol) {
        return rolRepository.save(rol);
    }
    
    public void deleteById(Long id) {
        rolRepository.deleteById(id);
    }
    
    public boolean existsById(Long id) {
        return rolRepository.existsById(id);
    }
    
    public boolean existsByNombre(String nombre) {
        return rolRepository.existsByNombre(nombre);
    }
    
    public Rol agregarPermiso(Long rolId, Permiso permiso) {
        Rol rol = rolRepository.findById(rolId)
            .orElseThrow(()-> new RuntimeException("No se encontró el rol."));
        rol.agregarPermiso(permiso);
        return rolRepository.save(rol);
    }
    
    public Rol removerPermiso(Long rolId, Permiso permiso) {
        Rol rol = rolRepository.findById(rolId)
            .orElseThrow(()-> new RuntimeException("No se encontró el rol."));
        rol.removerPermiso(permiso);
        return rolRepository.save(rol);
    }

    // Retorna rol CLIENTE para asignarlo automáticamente a nuevos usuarios al registrarse.
    //usado en UsuarioService en registro
    public Rol obtenerRolCliente(){
        return rolRepository.findByNombre("CLIENTE")
        .orElseThrow(()-> new RuntimeException("El rol CLIENTE no está registrado."));
        
    }
}
