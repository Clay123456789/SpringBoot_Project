package com.javaspring.myproject.service;

import com.javaspring.myproject.beans.User;
import com.javaspring.myproject.beans.UserVo;

import java.util.List;

public interface IUserService {
    //增删改查方法
    int insertUser(UserVo userVo);
    int deleteUser(String username);
    User getUser(String username);
    User getUserByEmail(String email);
    String getUserTouxiang(String username);
    //根据Username/EMail及密码判断用户是否存在
    boolean judgeByUserName(UserVo userVo);
    boolean judgeByEMail(UserVo userVo);
    int updatePassword(String password,String email);
    //更改邮箱
    boolean updateEmail(UserVo userVo);
    //更改用户名
    boolean updateUserName(UserVo userVo);
    boolean updateUser(UserVo userVo);

    List<User> getAllUsers();
}