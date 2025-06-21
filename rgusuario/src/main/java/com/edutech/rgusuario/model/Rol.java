package com.edutech.rgusuario.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "rol")
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false, unique = true)
    private String nombre;

    @Column(length = 255)
    private String descripcion;
    //Colección de elemnetos, un enumPermisos en este caso
    @ElementCollection(targetClass= Permiso.class)
    //Nombre de la tabla y cómo se relaciona con la principal
    @CollectionTable(name = "rol_permisos", joinColumns = @JoinColumn(name = "rol_id"))
    //Lo valores entregados se guadarán como string en la bd
    @Enumerated(EnumType.STRING)
    @Column(name = "permiso")

    private Set<Permiso> permiso = new HashSet<>();
    
    public void agregarPermiso (Permiso permiso){
        this.permiso.add(permiso);
    }

    public void removerPermiso(Permiso permiso){
        this.permiso.remove(permiso);
    }

}