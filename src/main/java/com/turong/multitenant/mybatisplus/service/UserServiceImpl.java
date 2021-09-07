package com.turong.multitenant.mybatisplus.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.turong.multitenant.mybatisplus.entity.User;
import com.turong.multitenant.mybatisplus.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    public UserServiceImpl(@Autowired final UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public User getUser(String id) {
        Optional<User> user = userMapper.findUser(id);
        if (!user.isPresent()) {
            return null;
        }
        return user.get();
    }

    @Override
    public User create(User userToCreate) {
        final int count = userMapper.insert(userToCreate);
        if (count == 0) {
            return null;
        }
        Optional<User> user = userMapper.findUserByEmail(userToCreate.getEmail());
        if (!user.isPresent()) {
            return null;
        }
        return user.get();
    }

    @Override
    public int deleteByEmail(String email) {
        return userMapper.deleteByEmail(email);
    }

    @Override
    public int deleteById(String id) {
        return userMapper.deleteById(id);
    }

    @Override
    public List<User> getAll() {
        QueryWrapper<User> query = Wrappers.query();
        return userMapper.selectList(query);
    }

}
