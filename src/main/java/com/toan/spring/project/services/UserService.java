package com.toan.spring.project.services;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

import com.toan.spring.project.models.User;
import java.util.List;

@Service
@EnableCaching

public interface UserService {
    public List<User> getAllUser();

    public User createUser(User user);

    public User save(User user);

    public User getUserById(long id);

    public User updateUser(long id, User userDetails);

    public void deleteUser(long id);

    public List<User> getUsersByRoleId(Long roleId);

    public void changeUserRoleToBanned(long userId);
}
