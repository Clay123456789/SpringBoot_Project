package com.javaspring.myproject.service.impl;

import com.javaspring.myproject.dao.IUserDao;
import com.javaspring.myproject.beans.User;
import com.javaspring.myproject.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private IUserDao userDao;

    @Override
    public void insertUser(User user) {
        userDao.insertUser(user);
    }

    @Override
    public void deleteUser(User user) {
        userDao.deleteUser(user);
    }

    @Override
    public void updateUser(User user) {
        userDao.updateUser(user);
    }

    @Override
    public User getUser(User user) {
        return userDao.getUser(user);
    }

    @Override
    public User getUserByEmail(String email) {
        return userDao.getUserByEmail(email);
    }

    @Override
    public boolean judgeByUserName(User user) {
        return userDao.judgeByUserName(user);
    }
    @Override
    public boolean judgeByEMail(User user) {
        return userDao.judgeByEMail(user);
    }
}