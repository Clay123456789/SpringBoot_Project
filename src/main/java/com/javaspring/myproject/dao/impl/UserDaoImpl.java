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

    @Override
    public void insertUser(User user) {
        jdbcTemplate.update("insert into user(username,password,email) values(?,?,?)",user.getUsername(),user.getPassword(),user.getEmail());

    }

    @Override
    public  void  deleteUser(User user) {
        jdbcTemplate.update("delete from user where username= ? and password = ?",user.getUsername(),user.getPassword());
    }

    @Override
    public void updateUser(User user) {
        /*
         * 待实现
         *
         *
         *
         * */
    }

    @Override
    public User getUser(User user){
        RowMapper<User> rowMapper = new BeanPropertyRowMapper<User>(User.class);
        Object object1 = null,object2=null;
        try {
            object1 = jdbcTemplate.queryForObject("select * from user where username = ?",rowMapper,user.getUsername());
        } catch (EmptyResultDataAccessException e1) {
            try {
                object2 = jdbcTemplate.queryForObject("select * from user where email = ?",rowMapper,user.getEmail());
            } catch (EmptyResultDataAccessException e2) {
                return null;
            }
            return (User)object2;
        }
       return (User)object1;
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
            //执行sql语句
            jdbcTemplate.update("update user set email = ? where email = ?" , newEmail, user.getEmail());
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
    public boolean updateUserName(User user, String newUserName) {
        try {
            //执行sql语句
            jdbcTemplate.update(
                    "update user set username = ? where email = ? " , newUserName, user.getEmail()
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
            System.out.println("failed to change name");
            return false;
        }
        System.out.println("your name has changed into: "+newUserName);
        return true;
    }

}

