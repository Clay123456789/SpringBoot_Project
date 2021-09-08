package com.javaspring.myproject.dao.impl;

import com.javaspring.myproject.beans.UserLike;
import com.javaspring.myproject.dao.IUserLikeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class UserLikeDaoImpl implements IUserLikeDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    //找到某条表态记录是否在数据库中，若存在则返回该记录的实例
    @Override
    public UserLike find(String blogid,String username) {
        RowMapper<UserLike> rowMapper = new BeanPropertyRowMapper<UserLike>(UserLike.class);
        Object object = null;
        try {
            object = jdbcTemplate.queryForObject("select * from userlike where blogid = ? and username = ?",rowMapper, blogid, username);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
        return (UserLike)object;
    }

    @Override
    public boolean like(UserLike userLike) {
        UserLike existingUserLike = find(userLike.getBlogid(),userLike.getUsername());
        int num=0;
        if(existingUserLike == null)
        {
            //记录不存在，即用户没有对该博客点赞，增加点赞记录。博客获赞数+1，同步到blog数据库
            num = jdbcTemplate.update("insert into userlike(blogid,username) values(?,?)", userLike.getBlogid(), userLike.getUsername());
            if(num==1) {
                jdbcTemplate.update("update blog set count = count+1 where blogid=?",userLike.getBlogid());
                //如果需要，这里可以再加一重判断，如果更新blog库失败，进行回滚
                return true;
            }
        }
        else
        {
            //记录存在，即用户选择撤销之前的点赞，删除点赞记录。博客获赞数-1，同步到blog数据库
            num = jdbcTemplate.update("delete from userlike where blogid = ? and username = ?",userLike.getBlogid(),userLike.getUsername());
            if(num==1) {
                jdbcTemplate.update("update blog set count = count-1 where blogid=?",userLike.getBlogid());
                //如果需要，这里可以再加一重判断，如果更新blog库失败，进行回滚
                return true;
            }
        }
        return false;
    }
}
