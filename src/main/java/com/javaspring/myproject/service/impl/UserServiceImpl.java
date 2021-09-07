package com.javaspring.myproject.service.impl;

import com.javaspring.myproject.beans.UserVo;
import com.javaspring.myproject.dao.IUserDao;
import com.javaspring.myproject.beans.User;
import com.javaspring.myproject.dao.impl.UserVoToUser;
import com.javaspring.myproject.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private IUserDao userDao;


    @Override
    public int insertUser(UserVo userVo) {
        return userDao.insertUser(UserVoToUser.toUser(userVo));
    }

    @Override
    public int deleteUser(String username) {
        return userDao.deleteUser(username);
    }

    @Override
    public User getUser(String username) {
        return userDao.getUser(username);
    }

    @Override
    public User getUserByEmail(String email) {
        return userDao.getUserByEmail(email);
    }

    @Override
    public String getUserTouxiang(String username) {

        return userDao.getUserTouxiang(username);
    }

    @Override
    public boolean judgeByUserName(UserVo userVo) {
        return userDao.judgeByUserName(UserVoToUser.toUser(userVo));
    }

    @Override
    public boolean judgeByEMail(UserVo userVo) {
        return userDao.judgeByEMail(UserVoToUser.toUser(userVo));
    }

    @Override
    public int updatePassword(String password, String email) {
        return userDao.updatePassword(password,email);
    }

    @Override
    public boolean updateEmail(UserVo userVo) {
        return userDao.updateEmail(UserVoToUser.toUser(userVo),userVo.getNewEmail());
    }

    @Override
    public boolean updateUserName(UserVo userVo) {
        return userDao.updateUserName(UserVoToUser.toUser(userVo),userVo.getNewUsername());
    }

    @Override
    public boolean updateUser(UserVo userVo) {
        //获取需要更改的user的完整信息
        User user = userDao.getUser(userVo.getUsername());

        //创建新的user，覆盖需要更改的user
        User newUser = UserVoToUser.toNewUser(userVo);
        //逐一检查不需要修改的属性，保持现有的值
        if(newUser.getUsername()==null)
        {
            newUser.setUsername(user.getUsername());
        }
        if(newUser.getPassword()==null)
        {
            newUser.setPassword(user.getPassword());
        }
        if(newUser.getEmail()==null)
        {
            newUser.setEmail(user.getEmail());
        }
        if(newUser.getTel()==null)
        {
            newUser.setTel(user.getTel());
        }
        if(newUser.getTouxiang()==null)
        {
            newUser.setTouxiang(user.getTouxiang());
        }
        if(newUser.getQianming()==null)
        {
            newUser.setQianming(user.getQianming());
        }
        if(newUser.getAge()==null)
        {
            newUser.setAge(user.getAge());
        }
        if(newUser.getSex()==null)
        {
            newUser.setSex(user.getSex());
        }

        return userDao.updateUser(user,newUser);
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }
}