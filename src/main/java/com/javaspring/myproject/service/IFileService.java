package com.javaspring.myproject.service;

import com.javaspring.myproject.beans.File;

import java.util.List;

public interface IFileService {
    //增删改查
    void insertFile(File file);
    boolean deleteFile(File file);
    void updateFile(File file);

    File getFile(File file);
    String getFileSize(long size);


    List<File> getAllFiles(File file);

}
