package com.javaspring.myproject.dao.impl;

import com.alibaba.fastjson.JSON;
import com.javaspring.myproject.beans.Blog;
import com.javaspring.myproject.dao.IBlogDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class BlogDaoImpl implements IBlogDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;
/**
 *    private String blogid;
 *     private String username;
 *     private String time;
 *     private String content;
 *     private String title;
 *     private String[] picture;
 */

    @Override
    public void insertBlog(Blog blog) {
        jdbcTemplate.update("insert into blog(blogid,username,time_,content,title,picture) values(?,?,?,?,?,?)",
                blog.getBlogid(),blog.getUsername(),blog.getTime(),blog.getContent(),blog.getTitle(), JSON.toJSONString(blog.getPicture()));

    }

    @Override
    public void deleteBlog(Blog blog) {
        jdbcTemplate.update("delete from blog where blogid= ?",blog.getBlogid());

    }

    @Override
    public void updateBlog(Blog blog) {
        /*
         * 待实现
         *
         *
         *
         * */
    }

    @Override
    public Blog getBlog(Blog blog) {
        RowMapper<Blog> rowMapper = new BeanPropertyRowMapper<Blog>(Blog.class);
        Object object1 = null;
        try {
            object1 = jdbcTemplate.queryForObject("select * from blog where blogid = ?",rowMapper,blog.getBlogid());
        } catch (EmptyResultDataAccessException e1) {
         return null;
        }
        return (Blog)object1;
    }
}
