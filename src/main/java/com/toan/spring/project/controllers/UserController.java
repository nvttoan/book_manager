package com.toan.spring.project.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.toan.spring.project.models.Role;
import com.toan.spring.project.models.User;
import com.toan.spring.project.payload.response.MessageResponse;
import com.toan.spring.project.services.UserService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api")

public class UserController {
    @Autowired
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/all")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<User> getAllUsers() {
        return userService.getAllUser();
    }

    @PostMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @GetMapping("/user/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getUserById(@PathVariable long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/user/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<User> updateUser(@PathVariable long id, @RequestBody User userDetails) {
        User user = userService.updateUser(id, userDetails);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/user/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Boolean>> deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
        Map<String, Boolean> response = new HashMap<>();
        response.put("delete", Boolean.TRUE);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/user/banned/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> changeUserRoleToBanned(@PathVariable long id) {
        try {
            userService.changeUserRoleToBanned(id);

            return ResponseEntity.ok("Đã cấm người dùng thành công.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Bạn không có quyền cấm người dùng"));
        }

    }

    @GetMapping("/user/role/{roleId}")
    @PreAuthorize("hasRole('ADMIN')") // Yêu cầu quyền ADMIN để xem danh sách người dùng có role_id
    public List<User> getUsersByRoleId(@PathVariable long roleId) {
        List<User> users = userService.getUsersByRoleId(roleId);

        return users;
    }

}
