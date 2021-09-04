package com.javaspring.myproject.service;

import com.javaspring.myproject.beans.File;

public interface IFileService {
    //增删改查
    void insertFile(File file);
    void deleteFile(File file);
    void updateFile(File file);
    File getFile(File file);
    String getFileSize(long size);

}
