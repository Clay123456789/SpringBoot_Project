package com.javaspring.myproject.dao;

import com.javaspring.myproject.beans.Blog;

import java.util.List;

public interface IBlogDao {
    //增删改查
    void insertBlog (Blog blog);
    boolean deleteBlog(Blog blog);
    void updateBlog (Blog blog);
    Blog getBlog (Blog blog);
    //以发布时间从新到旧获取个人博客
    List<Blog> getAllBlogs(Blog blog);
    //以发布时间从新到旧获取所有公开博客
    List<Blog> getPublicBlogs();
    List<Blog> getAllHotBlogs(Blog blog);
    List<Blog> getPublicHotBlogs();
}
