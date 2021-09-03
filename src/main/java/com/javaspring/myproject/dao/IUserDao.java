package com.javaspring.myproject.dao;

import com.javaspring.myproject.beans.User;

public interface IUserDao {
    void  addUser(User user);
    User getUser(String name);
    void  deleteUser(User user);
    boolean JudgeByUserName(User user);
    boolean JudgeByEMail(User user);
    void insertUser(User user);
    User queryByEmail(String email);
}
