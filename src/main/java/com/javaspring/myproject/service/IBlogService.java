package com.javaspring.myproject.service;

import com.javaspring.myproject.beans.Blog;

import java.util.List;

public interface IBlogService {
    //增删改查
    void insertBlog (Blog blog);
    boolean deleteBlog (Blog blog);
    void updateBlog (Blog blog);
    Blog getBlog (Blog blog);
    List<Blog> getAllBlogs(Blog blog);
    List<Blog> getPublicBlogs();
    List<Blog> getAllHotBlogs(Blog blog);
    List<Blog> getPublicHotBlogs();

}
