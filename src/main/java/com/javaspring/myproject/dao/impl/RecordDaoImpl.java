package com.javaspring.myproject.dao.impl;

import com.javaspring.myproject.beans.Record;
import com.javaspring.myproject.dao.IRecordDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class RecordDaoImpl implements IRecordDao {

   @Autowired
   private JdbcTemplate jdbcTemplate;

    @Override
    public void insertRecord(Record record) {
        String sql="insert into record(username,context,date_) values(?,?,?)";
        jdbcTemplate.update(sql,record.getUsername(),record.getContext(),record.getDate_());
    }

    @Override
    public boolean deleteRecord(Record record) {
        String sql="delete from record where username=? and date_=?";
        int result=jdbcTemplate.update(sql,record.getUsername(),record.getDate_());
        if(result!=0){
            return true;
        }
        return false;
    }

    @Override
    public void updateRecord(Record record) {
        String sql="update record set context=? where username=? and date_=?";
        jdbcTemplate.update(sql,record.getContext(),record.getUsername(),record.getDate_());
    }

    @Override
    public Record getRecord(Record record) {
        String sql="select * from record where username=? and date_=?";
        RowMapper<Record> rowMapper=new BeanPropertyRowMapper<Record>(Record.class);
        Object result=null;
        try{
            result=jdbcTemplate.query(sql,rowMapper,record.getUsername(),record.getDate_());
        }catch (EmptyResultDataAccessException e){
            return null;
        }
        return (Record) result;
    }
}
