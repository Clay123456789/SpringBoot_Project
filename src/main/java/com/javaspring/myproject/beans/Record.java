package com.javaspring.myproject.beans;

public class Record {
    private String username;
    private String context;
    private String date_;

    Record(){
    }
    Record(String username,String context,String date_){
        this.username=username;
        this.context=context;
        this.date_=date_;
    }

    public String getUsername(){
        return username;
    }
    public String getContext(){
        return context;
    }
    public String getDate_(){
        return date_;
    }
    public void setUsername(String username){
        this.username=username;
    }
    public void setContext(String context){
        this.context=context;
    }
    public void setDate_(String date_){
        this.date_=date_;
    }

}
