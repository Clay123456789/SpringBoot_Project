package com.javaspring.myproject.beans;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class Blog {
    private String blogid;
    private String username;
    private String time_;
    private String content;
    private String title;
    private String[] picture;
    private String visible;
    private int count;


    public Blog() {
    }

    public Blog(String blogid, String username, String time, String title, String content, String[] picture,String visible) {
        this.blogid = blogid;
        this.username = username;
        this.time_ = time;
        this.content = content;
        this.title = title;
        this.picture = picture;
        this.visible = visible;
        this.count=0;
    }
    public String getBlogid() {
        return blogid;
    }

    public String getUsername() {
        return username;
    }

    public String getTime_() {
        return time_;
    }
    //获取该博客创建日期对应的时间数字，用作后续比较
    public long getTimeValue(String time_) throws ParseException {
        //自定义simpleDateFormat格式要与数据库存储格式相同
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd/HH:mm:ss");
        Date date = format.parse(time_);
        //getTime会返回日期的毫秒数值，对该数值可以继续处理得到秒数，分钟数等不同精度的时间差
        return date.getTime();
    }
    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    public String[] getPicture() {
        return picture;
    }

    public String getVisible() {
        return visible;
    }

    public int getCount() {return count;}


    public void setVisible(String visiable) {
        this.visible = visiable;
    }
    public void setBlogid(String blogid) {
        this.blogid = blogid;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setTime_(String time_) {
        this.time_ = time_;
    }



    public void setContent(String content) {
        this.content = content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPicture(String[] picture) {
        this.picture = picture;
    }
    //一般不允许手动修改获赞数
    public void setCount(int count){
        this.count=count;
    }


    @Override
    public String toString() {
        return "Blog{" +
                "blogid='" + blogid + '\'' +
                ", username='" + username + '\'' +
                ", time_='" + time_ + '\'' +
                ", content='" + content + '\'' +
                ", title='" + title + '\'' +
                ", picture=" + Arrays.toString(picture) +
                ", visible='" + visible + '\'' +
                '}';
    }
}
