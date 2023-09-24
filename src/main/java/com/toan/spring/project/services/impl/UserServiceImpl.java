package com.toan.spring.project.services.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.toan.spring.project.exception.ResourceNotFoundException;
import com.toan.spring.project.models.Book;
import com.toan.spring.project.models.Role;
import com.toan.spring.project.models.User;
import com.toan.spring.project.repository.RoleRepository;
import com.toan.spring.project.repository.UserRepository;
import com.toan.spring.project.services.UserService;

@Service
@EnableCaching
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    @Override
    public User createUser(User user) {
        // kiểm tra xem tên người dùng hoặc email đã tồn tại trong repository
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new ResourceNotFoundException("Username already exists: " + user.getUsername());
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ResourceNotFoundException("Email already exists: " + user.getEmail());
        }

        // mã hóa password BCryptPasswordEncoder
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        Set<Role> roles = user.getRoles();
        if (roles != null && !roles.isEmpty()) {
            Set<Role> assignedRoles = new HashSet<>();
            for (Role role : roles) {
                Role existingRole = roleRepository.findByName(role.getName())
                        .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + role.getName()));
                assignedRoles.add(existingRole);
            }
            user.setRoles(assignedRoles);
        } else {
            throw new IllegalArgumentException("User must have at least one role.");
        }
        User savedUser = save(user);
        return savedUser;
    }

    @Override
    public User getUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("user not exit with id:" + id));
    }

    public User updateUser(long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(userDetails.getPassword());
        user.setPassword(encodedPassword);

        Set<Role> roles = userDetails.getRoles();
        if (roles != null) {
            // Lấy danh sách vai trò từ cơ sở dữ liệu dựa trên tên vai trò
            Set<Role> updatedRoles = new HashSet<>();
            for (Role role : roles) {
                Role existingRole = roleRepository.findByName(role.getName())
                        .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + role.getName()));
                updatedRoles.add(existingRole);
            }

            user.setRoles(updatedRoles);
        }

        User savedUser = save(user);
        return savedUser;
    }

    @Override
    public void deleteUser(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("user not delete with id:" + id));
        userRepository.delete(user);
    }

    public List<User> getUsersByRoleId(Long roleId) {
        return userRepository.findUsersByRoleId(roleId);
    }

    @Override
    public void changeUserRoleToBanned(long id) {
        User user = getUserById(id);

        Set<Role> newRoles = new HashSet<>();
        newRoles.add(new Role("ROLE_USER_BANNED"));
        user.setRoles(newRoles);

        updateUser(id, user);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

}
