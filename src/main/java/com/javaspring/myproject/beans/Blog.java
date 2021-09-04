package com.javaspring.myproject.beans;

public class Blog {
    private String blogid;
    private String username;
    private String time;
    private String content;
    private String title;
    private String[] picture;

    public Blog() {
    }

    public Blog(String blogid, String username, String time, String title, String content, String[] picture) {
        this.blogid = blogid;
        this.username = username;
        this.time = time;
        this.content = content;
        this.title = title;
        this.picture = picture;
    }
    public String getBlogid() {
        return blogid;
    }

    public String getUsername() {
        return username;
    }

    public String getTime() {
        return time;
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

    public void setBlogid(String blogid) {
        this.blogid = blogid;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setTime(String time) {
        this.time = time;
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
}
