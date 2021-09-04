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


@Repository
public class UserDaoImpl implements IUserDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public  void  addUser(User user){
        jdbcTemplate.update("insert into user(username,password) values(?,?)",user.getUsername(),user.getPassword());
    }

    public User getUser(String name){
        RowMapper<User> rowMapper = new BeanPropertyRowMapper<User>(User.class);
        User user = jdbcTemplate.queryForObject("select * from user where username = ?",rowMapper,name);
        return user;
    }
    public  void  deleteUser(User user) {
        jdbcTemplate.update("delete from user where username= ? and password = ?",user.getUsername(),user.getPassword());
    }

    @Override
    public boolean JudgeByUserName(User user) {
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
    public boolean JudgeByEMail(User user) {
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
    public void insertUser(User user) {
        jdbcTemplate.update("insert into user(username,password,email) values(?,?,?)",user.getUsername(),user.getPassword(),user.getEmail());

    }

    @Override
    public boolean updateEmail(User user){
        try {
            //执行sql语句
            jdbcTemplate.update(
                    "update user" +
                            "set email=?" +
                            "where email = ?"
                    , user.getEmail(), user.getEmail()
            );
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
            System.out.println("failed to change "+user.getEmail()+"'s email");
            return false;

        }
        System.out.println("email has changed into: "+user.getEmail());
        return true;
    }

    @Override
    public boolean updateUserName(User user) {
        try {
            //执行sql语句
            jdbcTemplate.update(
                    "update user" +
                            "set username=?" +
                            "where email = ?"
                    , user.getUsername(), user.getEmail()
            );
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
            System.out.println("failed to change "+user.getUsername()+"'s name");
            return false;
        }
        System.out.println("Username has changed into: "+user.getUsername());
        return true;
    }


}

