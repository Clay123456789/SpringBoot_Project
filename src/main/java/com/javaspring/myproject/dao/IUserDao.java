package com.javaspring.myproject.dao;

import com.javaspring.myproject.beans.User;

public interface IUserDao {
    //增删改查方法
    void insertUser(User user);
    void  deleteUser(User user);
    void updateUser(User user);
    User getUser(User user);
    //根据Username/EMail及密码判断用户是否存在
    boolean JudgeByUserName(User user);
    boolean JudgeByEMail(User user);
}
