package com.javaspring.myproject.dao.impl;

import com.javaspring.myproject.beans.File;
import com.javaspring.myproject.dao.IFileDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class FileDaoImpl implements IFileDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void insertFile(File file) {
        jdbcTemplate.update("insert into file(filename,username,url,date_,size_) values(?,?,?,?,?)",
                file.getFilename(),file.getUsername(),file.getUrl(),file.getDate_(),file.getSize_());
    }

    @Override
    public void deleteFile(File file) {
        jdbcTemplate.update("delete from file where filename= ? and username = ? and date_ =?",
                file.getFilename(),file.getUsername(),file.getDate_());

    }

    @Override
    public void updateFile(File file) {
        /*

        待实现
         */
    }

    @Override
    public File getFile(File file) {
        RowMapper<File> rowMapper = new BeanPropertyRowMapper<File>(File.class);
        Object object = null;
        try {
            object = jdbcTemplate.queryForObject("select * from file where filename= ? and username = ? and date_ =?",
                    rowMapper,file.getFilename(),file.getUsername(),file.getDate_());
           for (int j = 0; j < 100; j++) {
               System.out.println(object.toString());
           }
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
        return (File)object;
    }
}
