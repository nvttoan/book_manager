package com.toan.spring.project.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.toan.spring.project.models.Role;
import com.toan.spring.project.services.RoleService;

@RestController
@RequestMapping("/api")

public class RoleController {

    @Autowired
    private RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/rolelist")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Role> getAllRoles() {
        return roleService.getAllRoles();
    }

    @GetMapping("/getrole/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Role> getRoleById(@PathVariable long id) {
        Role role = roleService.getRoleById(id);
        return ResponseEntity.ok(role);
    }

    @PostMapping("/addrole")
    @PreAuthorize("hasRole('ADMIN')")
    public Role addRole(@RequestBody Role role) {
        return roleService.addRole(role);
    }

    @PutMapping("/updaterole/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Role> updateRole(@PathVariable long id, @RequestBody Role roleDetails) {
        Role role = roleService.updateRole(id, roleDetails);
        return ResponseEntity.ok(role);
    }

    @DeleteMapping("/deleterole/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteRole(@PathVariable Integer id) {
        roleService.deleteRole(id);
    }
}
