package com.javaspring.myproject.dao;

import com.javaspring.myproject.beans.Blog;

public interface IBlogDao {
    //增删改查
    void insertBlog (Blog blog);
    void deleteBlog (Blog blog);
    void updateBlog (Blog blog);
    Blog getBlog (Blog blog);


}
