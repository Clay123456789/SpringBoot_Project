package com.javaspring.myproject.dao;

import com.javaspring.myproject.beans.User;

public interface IUserDao {
    //增删改查方法
    void insertUser(User user);
    void  deleteUser(User user);
    void updateUser(User user);
    User getUser(User user);
    User getUserByEmail(String email);
    //根据Username/EMail及密码判断用户是否存在
    boolean judgeByUserName(User user);
    boolean judgeByEMail(User user);
    int updatePassword(String password,String email);
    //更改邮箱
    boolean updateEmail(User user,String newEmail);
    //更改用户名
    boolean updateUserName(User user,String newUserName);
}
