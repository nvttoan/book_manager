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

public interface UserService {
    public List<User> getAllUser();

    public User createUser(User user);

    public User getUserById(long id);

    public User updateUser(long id, User userDetails);

    public void deleteUser(long id);

    public List<User> getUsersByRoleId(Long roleId);

    // // borrow
    // public BorrowingDetailDto borrowBook(Long userid, Long bookid, long
    // expectedReturn);

    // // return
    // public CheckoutDetailDto returnBook(Long userid, Long bookid);

    public void changeUserRoleToBanned(long userId);
}
