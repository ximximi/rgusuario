package com.edutech.rgusuario.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.edutech.rgusuario.controller.UsuarioControllerV2;
import com.edutech.rgusuario.model.Usuario;

@Component
public class UsuarioModelAssembler implements RepresentationModelAssembler<Usuario, EntityModel<Usuario>> {

    @Override
    public EntityModel<Usuario> toModel(Usuario usuario) {
        return EntityModel.of(usuario,
            linkTo(methodOn(UsuarioControllerV2.class).getUsuarioById(usuario.getId())).withSelfRel(),
            linkTo(methodOn(UsuarioControllerV2.class).getAllUsuarios()).withRel("usuarios"),
            linkTo(methodOn(UsuarioControllerV2.class).updateUsuario(usuario.getId(), usuario)).withRel("update"),
            linkTo(methodOn(UsuarioControllerV2.class).deleteUsuario(usuario.getId())).withRel("delete")
        );
    }
}