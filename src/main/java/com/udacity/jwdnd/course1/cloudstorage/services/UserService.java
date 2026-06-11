package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mapper.UserMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

@Service
public class UserService {
    private final UserMapper userMapper;
    private final HashService hashService;
    private final SecureRandom secureRandom = new SecureRandom();

    public UserService(UserMapper userMapper, HashService hashService) {
        this.userMapper = userMapper;
        this.hashService = hashService;
    }

    public User getUser(String username) {
        return userMapper.getUser(username);
    }

    public Integer getUserId(String username) {
        User user = getUser(username);
        return user == null ? null : user.getUserId();
    }

    public boolean isUsernameAvailable(String username) {
        return getUser(username) == null;
    }

    public boolean createUser(User user) {
        if (!isUsernameAvailable(user.getUsername())) {
            return false;
        }

        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);
        String encodedSalt = Base64.getEncoder().encodeToString(salt);
        user.setSalt(encodedSalt);
        user.setPassword(hashService.getHashedValue(user.getPassword(), encodedSalt));
        return userMapper.insert(user) > 0;
    }
}
