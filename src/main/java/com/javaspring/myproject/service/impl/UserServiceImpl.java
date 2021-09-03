package com.javaspring.myproject.service.impl;

import com.javaspring.myproject.dao.IUserDao;
import com.javaspring.myproject.beans.User;
import com.javaspring.myproject.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private IUserDao dao;
    @Override
    public void addUser(User user) {
        dao.addUser(user);
    }
    @Override
    public User getUser(String name) {
        return dao.getUser(name);
    }

    @Override
    public void deleteUser(User user) {
        dao.deleteUser(user);
    }

    @Override
    public boolean JudgeByUserName(User user) {
        return dao.JudgeByUserName(user);
    }
    @Override
    public boolean JudgeByEMail(User user) {
        return dao.JudgeByEMail(user);
    }
}