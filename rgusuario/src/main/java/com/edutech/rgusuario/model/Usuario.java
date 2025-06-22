package com.edutech.rgusuario.model;

import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //notblank para que se envíen string vacíos desde la api
    //column evita que la bd acepte null
    //pattern para darle formato de rut
    @Pattern(regexp="^[0-9]+-[0-9kK]{1}$", message="Formato de RUT no válido")
    //^ y $ inicio y final, no debe haber más texto fuera de ese formato
    //[0-9]+ uno o más digitos
    //- guion del rut
    //[0-9kK]{1} un solo caracter, este pueder número o k 
    @NotBlank(message = "El RUT no puede estar vacío")
    @Column(length = 12, unique = true, nullable = false)
    private String rut;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Column(length = 50, nullable = false)
    private String primerNomb;

    
    @Column(length = 50, nullable = true)
    private String segundoNomb;

    @NotBlank(message = "El apellido no puede estar vacío")
    @Column(length = 50, nullable = false)
    private String primerApell;

    @Column(length = 50, nullable = true)
    private String segundoApell;   

    @NotNull(message = "La fecha no puede estar vacía")
    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date fechaNacimiento;
    
    @NotBlank(message = "El username no puede estar vacío")
    @Column(length = 50, nullable = false)
    private String username;

    @Email
    @NotBlank(message = "El email no puede estar vacío")
    @Column(length = 100, nullable = false)
    private String email;

    //contraseña temporal
    @Size(min = 8, message="La contraseña debe tener al menos 8 caracteres.")
    @jakarta.persistence.Transient
    private String contrasena;
    //esta sí se guarda en la bd
    @Column(length = 30, nullable = false)
    private String contrasenaHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado;

    @Column(nullable = false)
    private Instant fechaRegistro;

    //Rol
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST})//se quitó cascade
    //Relación de muchos a muchos, usuario_roles es la tabla intermedia, une a usuario y rol (por medio de id)
    @JoinTable(
        name = "usuario_roles",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    private List<Rol> roles = new ArrayList<>();


    //Se crea la cuenta con el estado ACTIVO
    public void crearCuenta(){
        this.fechaRegistro = Instant.now();
        this.estado = Estado.ACTIVO;
    }
    
    public void modificarInformacion(String primerNomb, String segundoNomb, String primerApell, String segundoApell, String email) {
        this.primerNomb = primerNomb;
        this.segundoNomb = segundoNomb;
        this.primerApell = primerApell;
        this.segundoApell = segundoApell;
        this.email = email;
    }

    public boolean verificarCredenciales(String username, String contrasena) {
        return this.username.equals(username) && this.contrasena.equals(contrasena);
    }

    public void cambiarEstado(Estado estado) {
        this.estado = estado;
    }
    public void agregarRol(Rol nuevoRol) {
        boolean yaAsignado = roles.stream() //stream para recorre la lista de roles
            .anyMatch(rol -> rol.getId().equals(nuevoRol.getId()));//anymatch devuelve true si hay un rolid que sea igual al id del rol nuevo

        if (yaAsignado) {
            throw new IllegalArgumentException("El rol ya está asignado al usuario.");
        }

        roles.add(nuevoRol);//se agrega a la lista
    }

    public void removerRolPorId(Long rolId) {
        boolean removido = roles.removeIf(rol -> rol.getId().equals(rolId));//removeIf elimina todos los roles id que coincidan con el rolID
        if (!removido) {
            throw new IllegalArgumentException("El usuario no tiene asignado el rol con ID: " + rolId);
        }
    }    
}

