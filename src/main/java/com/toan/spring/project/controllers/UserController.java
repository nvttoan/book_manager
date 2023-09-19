package com.toan.spring.project.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.toan.spring.project.models.User;
import com.toan.spring.project.payload.response.CodeResponse;
import com.toan.spring.project.services.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")

public class UserController {
    @Autowired
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/all")
    @PreAuthorize(" hasRole('ADMIN')")
    public ResponseEntity<Object> getAllUsers() {
        try {
            List<User> users = userService.getAllUser();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CodeResponse(1, "Thực hiện thất bại: " + e.getMessage()));
        }
    }

    // @PostMapping("/user")
    // @PreAuthorize(" hasRole('ADMIN')")
    // public User createUser(@RequestBody User user) {
    // return userService.createUser(user);
    // }

    @GetMapping("/user/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getUserById(@PathVariable long id) {
        try {
            User user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CodeResponse(1, "Thực hiện thất bại: " + e.getMessage()));
        }
    }

    @PutMapping("/user/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable long id, @RequestBody User userDetails) {
        try {
            User user = userService.updateUser(id, userDetails);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CodeResponse(1, "Thực hiện thất bại: " + e.getMessage()));
        }
    }

    @DeleteMapping("/user/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable long id) {
        try {
            userService.deleteUser(id);
            Map<String, Boolean> response = new HashMap<>();
            response.put("delete", Boolean.TRUE);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CodeResponse(1, "Thực hiện thất bại: " + e.getMessage()));
        }

    }

    @PutMapping("/user/banned/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> changeUserRoleToBanned(@PathVariable long id) {
        try {
            userService.changeUserRoleToBanned(id);

            return ResponseEntity.ok("Đã cấm người dùng thành công.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CodeResponse(1, "Thực hiện thất bại: " + e.getMessage()));
        }

    }

    @GetMapping("/user/role/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUsersByRoleId(@PathVariable long roleId) {
        try {
            List<User> users = userService.getUsersByRoleId(roleId);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CodeResponse(1, "Thực hiện thất bại: " + e.getMessage()));
        }

    }

}
