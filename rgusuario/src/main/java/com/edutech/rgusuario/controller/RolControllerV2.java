package com.edutech.rgusuario.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.edutech.rgusuario.assembler.RolModelAssembler;
import com.edutech.rgusuario.model.Rol;
import com.edutech.rgusuario.service.RolService;

@RestController
@RequestMapping("/api/v2/roles")
public class RolControllerV2 {

    @Autowired
    private RolService rolService;

    @Autowired
    private RolModelAssembler assembler;

    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<Rol>> getAllRoles() {
        List<EntityModel<Rol>> roles = rolService.findAll().stream()
            .map(assembler::toModel)
            .toList();

        return CollectionModel.of(roles,
            linkTo(methodOn(RolControllerV2.class).getAllRoles()).withSelfRel());
    }

    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public EntityModel<Rol> getRolById(@PathVariable Long id) {
        Rol rol = rolService.findById(id)
            .orElseThrow(() -> new RuntimeException("No se encontró el rol con ID: " + id));
        return assembler.toModel(rol);
    }
}