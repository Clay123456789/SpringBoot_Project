package com.javaspring.myproject.dao;

import com.javaspring.myproject.beans.File;

import java.util.List;

public interface IFileDao {

    //增删改查方法
    void insertFile(File file);
    void  deleteFile(File file);
    void updateFile(File file);
    File getFile(File file);
}
