package com.toan.spring.project.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.toan.spring.project.common.BookStatus;
import com.toan.spring.project.dto.BorrowingDetailDto;
import com.toan.spring.project.dto.CheckoutDetailDto;
import com.toan.spring.project.exception.ResourceNotFoundException;
import com.toan.spring.project.models.Book;
import com.toan.spring.project.models.BorrowingDetail;
import com.toan.spring.project.models.Role;
import com.toan.spring.project.models.User;
import com.toan.spring.project.repository.RoleRepository;
import com.toan.spring.project.repository.UserRepository;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@EnableCaching

public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BookService bookService;

    @Autowired
    private BorrowingDetailService borrowingDetailService;

    // @Cacheable("users")
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

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

        return userRepository.save(user);
    }

    // @CacheEvict(value = "user", key = "#user.id")
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

        return userRepository.save(user);
    }

    public void deleteUser(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("user not delete with id:" + id));
        userRepository.delete(user);
    }

    public List<User> getUsersByRoleId(Long roleId) {
        return userRepository.findUsersByRoleId(roleId);
    }

    // borrow
    public BorrowingDetailDto borrowBook(Long userid, Long bookid, long expectedReturn) {
        User user = userRepository.findById(userid)
                .orElseThrow(() -> new ResourceNotFoundException("Unavailable user"));
        Book book = bookService.findById(bookid);
        BorrowingDetail borrowingDetail = borrowingDetailService.createNewBorrowDetail(user, book, expectedReturn);
        if (book.getStatus() == BookStatus.AVAILABLE) {
            book.setStatus(BookStatus.NOT_AVAILABLE);
            bookService.save(book);
        }
        return new BorrowingDetailDto(borrowingDetail);
    }

    // return
    public CheckoutDetailDto returnBook(Long userid, Long bookid) {
        BorrowingDetail borrowingDetail = borrowingDetailService.checkOutBorrowDetail(userid, bookid);
        return new CheckoutDetailDto(borrowingDetail, System.currentTimeMillis());
    }

    private void doLongRunningTask() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void changeUserRoleToBanned(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Set<Role> newRoles = new HashSet<>();
        Role bannedRole = roleRepository.findByName("ROLE_BANNED")
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: ROLE_BANNED"));
        newRoles.add(bannedRole);

        user.setRoles(newRoles);
        userRepository.save(user);
    }
}
