package com.edutech.rgusuario.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.edutech.rgusuario.controller.RolControllerV2;
import com.edutech.rgusuario.model.Rol;

@Component
public class RolModelAssembler implements RepresentationModelAssembler<Rol, EntityModel<Rol>> {

    @Override
    public EntityModel<Rol> toModel(Rol rol) {
        return EntityModel.of(rol,
            linkTo(methodOn(RolControllerV2.class).getRolById(rol.getId())).withSelfRel(),
            linkTo(methodOn(RolControllerV2.class).getAllRoles()).withRel("roles")
        );
    }
}