package com.javaspring.myproject.dao.impl;


import com.javaspring.myproject.beans.User;
import com.javaspring.myproject.dao.IUserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;


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
    public User queryByEmail(String email) {

        RowMapper<User> rowMapper = new BeanPropertyRowMapper<User>(User.class);Object object = null;
        try {
            object = jdbcTemplate.queryForObject("select * from user where email = ?",rowMapper,email);
        } catch (EmptyResultDataAccessException e) {
            return new User();
        }
        return (User) object;
    }
}

