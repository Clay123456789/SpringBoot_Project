package com.javaspring.myproject.service.impl;

import com.javaspring.myproject.beans.Blog;
import com.javaspring.myproject.dao.IBlogDao;
import com.javaspring.myproject.service.IBlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BlogServiceImpl implements IBlogService {
    @Autowired
    private IBlogDao blogDao;
    @Override
    public void insertBlog(Blog blog) {
        blogDao.insertBlog(blog);
    }

    @Override
    public void deleteBlog(Blog blog) {
        blogDao.deleteBlog(blog);
    }

    @Override
    public void updateBlog(Blog blog) {
        blogDao.updateBlog(blog);
    }

    @Override
    public Blog getBlog(Blog blog) {
        return blogDao.getBlog(blog);
    }
}
