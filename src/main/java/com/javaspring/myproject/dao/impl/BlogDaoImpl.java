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

import java.text.ParseException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Repository
public class BlogDaoImpl implements IBlogDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Override
    public void insertBlog(Blog blog) {
        jdbcTemplate.update("insert into blog(blogid,username,time_,content,title,picture,visible) values(?,?,?,?,?,?,?)",
                blog.getBlogid(),blog.getUsername(),blog.getTime_(),blog.getContent(),blog.getTitle(), JSON.toJSONString(blog.getPicture()),blog.getVisible());

    }

    @Override
    public boolean deleteBlog(Blog blog) {
       int  number=   jdbcTemplate.update("delete from blog where blogid = ? and username = ?",blog.getBlogid(),blog.getUsername());
        if(number!=0){
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public void updateBlog(Blog blog) {
        String sql="update blog set time_=?,title=?,content=?,picture=?,visible=? where blogid=?";
        jdbcTemplate.update(sql,blog.getTime_(),blog.getTitle(),blog.getContent(),blog.getPicture(),blog.getVisible(),blog.getBlogid());
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
        List<Blog> blogList = jdbcTemplate.query("select * from blog where visible = '1' order by time_ DESC ",rowMapper);

        return blogList;
    }
    //按自定义的热度对自己的博客进行排序输出
    @Override
    public List<Blog> getAllHotBlogs(Blog blog) {
        RowMapper<Blog> rowMapper = new BeanPropertyRowMapper<Blog>(Blog.class);
        List<Blog> blogList = jdbcTemplate.query("select * from blog where username = ? order by time_ DESC ",rowMapper,blog.getUsername());
        //外加的比较方法,也可以选择在Bolg类中实现Compare接口而使用内部比较方法
        Collections.sort(blogList, new Comparator<Blog>() {
            @Override
            public int compare(Blog o1, Blog o2) {
                //t为时间热度，c为点赞热度，二者共同构成最终热度hot
                long t1=0,t2=0;
                try {
                    t1 = o1.getTimeValue(o1.getTime_());
                    t2 = o2.getTimeValue(o2.getTime_());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                int centPerLike = 144;
                long c1=o1.getCount()*centPerLike;
                long c2=o2.getCount()*centPerLike;
                // 除1000除60得到分钟数，进行一定的加权得到热度值hot
                return (int) -((t1-t2)*0.1/1000/60+(c1-c2)*0.9);
            }
        });
        return blogList;
    }
    //按自定义的热度对公开的博客进行排序输出
    @Override
    public List<Blog> getPublicHotBlogs() {
        RowMapper<Blog> rowMapper = new BeanPropertyRowMapper<Blog>(Blog.class);
        List<Blog> blogList = jdbcTemplate.query("select * from blog where visible = '1' order by time_ DESC ",rowMapper);
        Collections.sort(blogList, new Comparator<Blog>() {
            @Override
            public int compare(Blog o1, Blog o2) {
                long t1=0,t2=0;
                try {
                    t1 = o1.getTimeValue(o1.getTime_());
                    t2 = o2.getTimeValue(o2.getTime_());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                int centPerLike = 144;
                long c1=o1.getCount()*centPerLike;
                long c2=o2.getCount()*centPerLike;
                return (int) -((t1-t2)*0.1/1000/60+(c1-c2)*0.9);
            }
        });
        return blogList;
    }


}
