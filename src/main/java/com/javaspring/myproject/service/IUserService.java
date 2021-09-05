package com.javaspring.myproject.service;

import com.javaspring.myproject.beans.User;

public interface IUserService {
    //增删改查
    void insertUser(User user);
    void deleteUser(User user);
    void updateUser(User user);
    User getUser(User user);
    User getUserByEmail(String email);
    //通过用户名验证用户身份是否合法
    boolean judgeByUserName(User user);
    //通过邮箱验证用户身份是否合法
    boolean judgeByEMail(User user);
}