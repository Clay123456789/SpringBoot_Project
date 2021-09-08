package com.javaspring.myproject.service;

import com.javaspring.myproject.beans.UserLike;

public interface IUserLikeService {
    UserLike find(UserLike userLike);
    boolean giveALike(UserLike userLike);
}
