package com.javaspring.myproject.service;

import com.javaspring.myproject.beans.User;

public interface IUserService {
    //增删改查
    void insertUser(User user);
    void deleteUser(User user);
    void updateUser(User user);
    User getUser(User user);

    //通过用户名验证用户身份是否合法
    boolean JudgeByUserName(User user);
    //通过邮箱验证用户身份是否合法
    boolean JudgeByEMail(User user);
}