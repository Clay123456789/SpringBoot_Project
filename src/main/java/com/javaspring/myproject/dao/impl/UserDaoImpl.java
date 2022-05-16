package com.javaspring.myproject.dao.impl;


import com.javaspring.myproject.beans.User;
import com.javaspring.myproject.dao.IUserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;


@Repository
public class UserDaoImpl implements IUserDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    //理论上所有用username锁定对象的函数都可以使用email，但数据库的主键为username，故推荐使用username
    @Override
    public int insertUser(User user) {
        //返回影响行数，为1表示修改成功
        return jdbcTemplate.update("insert into user(username,tel,password,email,touxiang,qianming,age,sex) values(?,?,?,?,?,?,?,?)",
                user.getUsername(),user.getTel(),user.getPassword(),user.getEmail(),
                user.getTouxiang(),user.getQianming(),user.getAge(),user.getSex());

    }

    @Override
    public int deleteUser(String username) {
        return jdbcTemplate.update("delete from user where username = ?",username);
    }

    @Override
    public User getUser(String username) {
        RowMapper<User> rowMapper = new BeanPropertyRowMapper<User>(User.class);
        Object object1 = null;
        //queryForObject会抛出非检查性异常DataAccessException，同时对返回值进行requiredSingleResult操作
        //requiredSingleResult会在查询结果为空的时候抛出EmptyResultDataAccessException异常，需要捕获后进行处理
        try {
            object1 = jdbcTemplate.queryForObject("select * from user where username = ?",rowMapper,username);
        } catch (EmptyResultDataAccessException e1) {
            //查询结果为空，返回null
            return null;
        }
        return (User) object1;
    }

    @Override
    public User getUserByEmail(String email) {
        RowMapper<User> rowMapper = new BeanPropertyRowMapper<User>(User.class);
        //如果使用该email查询不到或者查询到多条（后者不会发生），会抛出查询异常
        try {
            User user = jdbcTemplate.queryForObject("select * from user where email = ?" , rowMapper, email);
            return user;
        } catch (EmptyResultDataAccessException e){
            e.printStackTrace();
            System.out.println("wrong email: "+email+" please check your email and try again! ");
            return null;
        }
    }
    //单独返回头像的url值，其实是个冗余方法
    @Override
    public String getUserTouxiang(String username) {
        RowMapper<User> rowMapper = new BeanPropertyRowMapper<User>(User.class);
        //如果使用该email查询不到或者查询到多条（后者不会发生），会抛出查询异常
        try {
            User user = jdbcTemplate.queryForObject("select * from user where username = ?" , rowMapper, username);
            return user.getTouxiang();
        } catch (EmptyResultDataAccessException e){
            e.printStackTrace();
            System.out.println("wrong username: "+username+"or he/she does not have a touxiang, please check out and try again! ");
            return null;
        }
    }

    @Override
    public List<User> getAllUsers() {
        RowMapper<User> rowMapper = new BeanPropertyRowMapper<User>(User.class);
        List<User> userList = jdbcTemplate.query("select * from user order by username DESC ",rowMapper);
        return userList;
    }

    @Override
    public boolean judgeByUserName(User user) {
        RowMapper<User> rowMapper = new BeanPropertyRowMapper<User>(User.class);
        Object object = null;
        try {
            object = jdbcTemplate.queryForObject("select * from user where username = ? and password = ? ",rowMapper,user.getUsername(),user.getPassword());
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
            return true;
    }

    @Override
    public boolean judgeByEMail(User user) {
        RowMapper<User> rowMapper = new BeanPropertyRowMapper<User>(User.class);
        Object object = null;
        try {
            object = jdbcTemplate.queryForObject("select * from user where email = ? and password = ? ",rowMapper,user.getEmail(),user.getPassword());
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
        return true;
    }

    @Override
    public int updatePassword(String password, String email) {
        String sql="update user set password=? where email=?";
        int result=jdbcTemplate.update(sql,password,email);
        return result;
    }

    @Override
    public boolean updateEmail(User user, String newEmail) {
        try {
            if(user.getEmail()!=null) {
                //执行sql语句
                jdbcTemplate.update("update user set email = ? where email = ?", newEmail, user.getEmail());
            }
            else {
                jdbcTemplate.update("update user set email = ? where username = ?", newEmail, user.getUsername());
            }
            //经查询，jdbcTemplate不会直接抛出SQL异常，而是InvalidResultSet异常和DataAccess异常
            //但是，我的初衷是告诉用户经典的SQLErrorCodes，所以找办法得到SQL异常
        } catch (DataAccessException de){
            SQLException se = (SQLException) de.getCause();
            if(se!=null)
            {
                System.out.println("Error in update database, error code: " + se.getErrorCode());
            }
            else
            {
                System.out.println("Error in jdbcTemplate");
            }
            System.out.println("failed to change email");
            return false;

        }
        System.out.println("your email has changed into: "+newEmail);
        return true;
    }

    @Override
    public boolean updateUserName(User user, String newUsername) {
        try {
            if(user.getEmail()!=null) {
                //执行sql语句
                jdbcTemplate.update("update user set username = ? where email = ?", newUsername, user.getEmail());
            }
            else {
                jdbcTemplate.update("update user set username = ? where username = ?", newUsername, user.getUsername());
            }
            //经查询，jdbcTemplate不会直接抛出SQL异常，而是InvalidResultSet异常和DataAccess异常
            //但是，我的初衷是告诉用户经典的SQLErrorCodes，所以找办法得到SQL异常
        } catch (DataAccessException de){
            SQLException se = (SQLException) de.getCause();
            if(se!=null)
            {
                System.out.println("Error in update database(SQLException), error code: " + se.getErrorCode());
            }
            else
            {
                System.out.println("Error in jdbcTemplate(DataAccessException): "+de.getMessage());
            }
            System.out.println("failed to change name");
            return false;
        }
        System.out.println("your name has changed into: "+newUsername);
        return true;
    }
    @Override
    public boolean updateUser(User User, User newUser) {
        //暴力更新，直接先delete后insert
        if(deleteUser(User.getUsername())==1 && insertUser(newUser)==1)
            return true;
        return false;
    }

   
}

