package com.javaspring.myproject.service.impl;


import com.javaspring.myproject.beans.File;
import com.javaspring.myproject.dao.IFileDao;
import com.javaspring.myproject.service.IFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;

@Service
public class FileServiceImpl implements IFileService {
    @Autowired
    private IFileDao fileDao;
    @Override
    public void insertFile(File file) {
        fileDao.insertFile(file);
    }

    @Override
    public void deleteFile(File file) {
        fileDao.deleteFile(file);
    }

    @Override
    public void updateFile(File file) {
        fileDao.updateFile(file);
    }

    @Override
    public File getFile(File file) {
        return fileDao.getFile(file);
    }

    @Override
    public String getFileSize(long size) {
            StringBuffer bytes = new StringBuffer();
            DecimalFormat format = new DecimalFormat("###.0");
            if (size >= 1024 * 1024 * 1024) {
                double i = (size / (1024.0 * 1024.0 * 1024.0));
                bytes.append(format.format(i)).append("GB");
            }
            else if (size >= 1024 * 1024) {
                double i = (size / (1024.0 * 1024.0));
                bytes.append(format.format(i)).append("MB");
            }
            else if (size >= 1024) {
                double i = (size / (1024.0));
                bytes.append(format.format(i)).append("KB");
            }
            else if (size < 1024) {
                if (size <= 0) {
                    bytes.append("0B");
                }
                else {
                    bytes.append((int) size).append("B");
                }
            }
            return bytes.toString();

    }
}
