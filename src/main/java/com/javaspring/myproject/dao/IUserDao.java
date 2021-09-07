package com.javaspring.myproject.dao;

import com.javaspring.myproject.beans.User;

import java.util.List;

public interface IUserDao {
    //增删改查方法
    int insertUser(User user);
    int deleteUser(String username);
    User getUser(String username);
    //以邮箱锁定到操作对象
    User getUserByEmail(String email);
    //获取头像url
    String getUserTouxiang(String username);
    //根据Username/EMail及密码判断用户是否存在
    boolean judgeByUserName(User user);
    boolean judgeByEMail(User user);
    int updatePassword(String password,String email);
    //更改邮箱
    boolean updateEmail(User user,String newEmail);
    //更改用户名
    boolean updateUserName(User user,String newUserName);
    //更改用户信息
    boolean updateUser(User User,User newUser);

    List<User> getAllUsers();
}
