package com.javaspring.myproject.dao;

import com.javaspring.myproject.beans.Record;

public interface IRecordDao {
    //增删改查功能
    void insertRecord(Record record);
    boolean deleteRecord(Record record);
    void updateRecord(Record record);
    Record getRecord(Record record);

}
