package com.javaspring.myproject.beans;
//用户对某博客的表态记录，主键为blogid和username
public class UserLike {
    private String blogid;
    private String username;
    //表态值，默认只有赞，后续可以追加踩，亦或其他举动
    private int type;

    public String getUsername() {
        return username;
    }
    public String getBlogid() {
        return blogid;
    }
    public int getType() {return type;}

    public UserLike() {
    }

    public UserLike(String blogid, String username) {
        this.blogid = blogid;
        this.username = username;
    }

    public void setBlogid(String blogid) {
        this.blogid = blogid;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setType(int type) {
        this.type=type;
    }
}
