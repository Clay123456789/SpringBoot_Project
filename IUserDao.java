package com.javaspring.myproject.dao;

import com.javaspring.myproject.beans.User;

import java.sql.SQLException;

public interface IUserDao {
    void  addUser(User user);
    User getUser(String name);
    void  deleteUser(User user);
    boolean JudgeByUserName(User user);
    boolean JudgeByEMail(User user);
    void insertUser(User user);
    //更改邮箱
    boolean updateEmail(User user);
    //更改用户名
    boolean updateUserName(User user);
}
