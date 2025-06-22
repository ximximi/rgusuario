package com.edutech.rgusuario.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edutech.rgusuario.model.Estado;
import com.edutech.rgusuario.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
    @Override
    List<Usuario> findAll();

    //En este caso será usado apra recuperación de cuenta, los optional
    //optional porque puede o no existir con ese rut, username, etc.
    Optional<Usuario> findByRut(String rut);
    
    Optional<Usuario> findByUsername(String username);
    
    Optional<Usuario> findByEmail(String email);
    
    List<Usuario> findByEstado(Estado estado);
    
    //verifica si existe al menos un usuario 
    boolean existsByRut(String rut);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
}
