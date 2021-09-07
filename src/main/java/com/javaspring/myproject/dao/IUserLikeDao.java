package com.javaspring.myproject.dao;

import com.javaspring.myproject.beans.UserLike;

public interface IUserLikeDao {
    UserLike find(String blogid,String username);
    boolean like(UserLike userLike);
}
