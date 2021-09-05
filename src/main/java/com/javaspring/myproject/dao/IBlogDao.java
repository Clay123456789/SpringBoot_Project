package com.javaspring.myproject.dao;

import com.javaspring.myproject.beans.Blog;

import java.util.List;

public interface IBlogDao {
    //增删改查
    void insertBlog (Blog blog);
    void deleteBlog (Blog blog);
    void updateBlog (Blog blog);
    Blog getBlog (Blog blog);

    List<Blog> getAllBlogs(Blog blog);
    List<Blog> getPublicBlogs();

}
