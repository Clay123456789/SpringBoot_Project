package com.javaspring.myproject.service;

import com.javaspring.myproject.beans.Record;

public interface IRecordService {
    //增删改查
    void insertRecord(Record record);
    boolean deleteRecord(Record record);
    void updateRecord(Record record);
    Record getRecord(Record record);

}
