package com.javaspring.myproject.beans;

//数据库存储的user对象
public class User {
    private String username;
    private String password;
    private String email;
    private String tel;
    //用户头像，以url存储
    private String touxiang;
    private String qianming;
    private String age;
    private String sex;
    public User(String email) {
        this.email = email;
    }
    public User() {
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setTel(String tel)
    {
        this.tel=tel;
    }
    public void setTouxiang(String touxiang)
    {
        this.touxiang=touxiang;
    }
    public void setQianming(String qianming)
    {
        this.qianming=qianming;
    }
    public void setAge(String age)
    {
        this.age=age;
    }
    public void setSex(String sex)
    {
        this.sex=sex;
    }

    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public String getTel()
    {
        return tel;
    }
    public String getTouxiang()
    {
        return touxiang;
    }
    public String getQianming()
    {
        return qianming;
    }
    public String getAge()
    {
        return age;
    }
    public String getSex()
    {
        return sex;
    }
    public String getEmail() {
        return email;
    }

}
