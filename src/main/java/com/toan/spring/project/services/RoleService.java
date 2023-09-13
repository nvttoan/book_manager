package com.toan.spring.project.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.toan.spring.project.exception.ResourceNotFoundException;
import com.toan.spring.project.models.Role;
import com.toan.spring.project.repository.RoleRepository;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role getRoleById(long id) {
        return roleRepository.findById(id).orElse(null);
    }

    public Role addRole(Role role) {
        return roleRepository.save(role);
    }

    public Role updateRole(long id, Role roleDetails) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("role not exit with id:" + id));

        role.setName(roleDetails.getName());
        role.setCode(roleDetails.getCode());
        role.setDescription(roleDetails.getDescription());

        return roleRepository.save(role);
    }

    public void deleteRole(long id) {
        roleRepository.deleteById(id);
    }

    // phân trang
    public Page<Role> getPaginatedRoles(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return roleRepository.findAll(pageable);
    }

    // // search
    // public List<Role> findByNameOrCode(String name, String code) {
    // return roleRepository.findByNameOrCode(name, code);
    // }
}
