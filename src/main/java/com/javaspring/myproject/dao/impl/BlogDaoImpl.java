package com.javaspring.myproject.dao.impl;

import com.alibaba.fastjson.JSON;
import com.javaspring.myproject.beans.Blog;
import com.javaspring.myproject.dao.IBlogDao;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class BlogDaoImpl implements IBlogDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Override
    public void insertBlog(Blog blog) {
        jdbcTemplate.update("insert into blog(blogid,username,time_,content,title,picture,visiable) values(?,?,?,?,?,?,?)",
                blog.getBlogid(),blog.getUsername(),blog.getTime_(),blog.getContent(),blog.getTitle(), JSON.toJSONString(blog.getPicture()),blog.getVisiable());

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
        Object object = null;
        try {
            object = jdbcTemplate.queryForObject("select * from blog where blogid = ?",rowMapper,blog.getBlogid());
        } catch (EmptyResultDataAccessException e) {
         return null;
        }
        return (Blog)object;
    }

    @Override
    public List<Blog> getAllBlogs(Blog blog) {

        RowMapper<Blog> rowMapper = new BeanPropertyRowMapper<Blog>(Blog.class);
        List<Blog> blogList = jdbcTemplate.query("select * from blog where username = ? order by time_ DESC ",rowMapper,blog.getUsername());

        return blogList;
    }

    @Override
    public List<Blog> getPublicBlogs() {
        RowMapper<Blog> rowMapper = new BeanPropertyRowMapper<Blog>(Blog.class);
        List<Blog> blogList = jdbcTemplate.query("select * from blog where visiable = '1' order by time_ DESC ",rowMapper);

        return blogList;
    }
}
