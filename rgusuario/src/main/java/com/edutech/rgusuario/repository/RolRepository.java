package com.edutech.rgusuario.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edutech.rgusuario.model.Rol;

@Repository

//La extensión es que JPA generará lo necesario para acceder a la bd
public interface RolRepository extends JpaRepository<Rol, Long>{
    //jpa maneja save, count, findById, existsById, findAll, deleteById, deleteAll 
    
    
    @Override //por si se personaliza    
    List<Rol> findAll();

    //optional porque puede o no existir con ese nombre
    Optional<Rol> findByNombre(String nombre);
    //verifica si existe al menos un rol
    boolean existsByNombre(String nombre);
}
