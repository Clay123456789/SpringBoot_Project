package com.javaspring.myproject.service;

import com.javaspring.myproject.beans.Record;

import java.util.List;

public interface IRecordService {
    //增删改查
    void insertRecord(Record record);
    boolean deleteRecord(Record record);
    void updateRecord(Record record);
    List<Record> getRecord(Record record);
    Record getRecordin(Record record);

}
