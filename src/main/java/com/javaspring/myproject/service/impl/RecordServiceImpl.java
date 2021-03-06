package com.javaspring.myproject.service.impl;

import com.javaspring.myproject.beans.Record;
import com.javaspring.myproject.dao.IRecordDao;
import com.javaspring.myproject.service.IRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecordServiceImpl implements IRecordService {

    @Autowired
    private IRecordDao recordDao;
    @Override
    public void insertRecord(Record record) {
        recordDao.insertRecord(record);
    }

    @Override
    public boolean deleteRecord(Record record) {
        return recordDao.deleteRecord(record);
    }

    @Override
    public void updateRecord(Record record) {
        recordDao.updateRecord(record);
    }

    @Override
    public List<Record> getRecord(Record record) {
        return recordDao.getRecord(record);
    }

    @Override
    public Record getRecordin(Record record) {
        return recordDao.getRecordin(record);
    }
}
