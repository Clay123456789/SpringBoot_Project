package com.javaspring.myproject.service.impl;

import com.javaspring.myproject.beans.UserLike;
import com.javaspring.myproject.dao.IUserLikeDao;
import com.javaspring.myproject.dao.impl.UserLikeDaoImpl;
import com.javaspring.myproject.service.IUserLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.soap.SOAPBinding;
@Service
public class UserLikeServiceImpl implements IUserLikeService {
    @Autowired
    private IUserLikeDao userLikeDao;

    @Override
    public UserLike find(UserLike userLike) {
        return userLikeDao.find(userLike.getBlogid(),userLike.getUsername());
    }

    @Override
    public boolean giveALike(UserLike userLike) {
        return userLikeDao.like(userLike);
    }
}
