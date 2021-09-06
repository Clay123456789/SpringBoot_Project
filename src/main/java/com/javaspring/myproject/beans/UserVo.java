package com.javaspring.myproject.beans;

//专用于接受表单提交的user对象，包含各种接口需要的专用属性
public class UserVo {
    private String username;
    private String password;
    private String email;
    //    验证码
    private String code;
    //新信息
    private String newUserName;
    private String newEmail;
    private String newPassword;
    private String newPasswordRepeat;
    public UserVo() {
    }

    public UserVo(String username, String password, String email, String code) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.code = code;
    }
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getCode() {
        return code;
    }

    public String getNewPassword(){ return newPassword;}

    public String getNewPasswordRepeat(){ return newPasswordRepeat;}

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setNewUserName(String newUserName)
    {
        this.newUserName=newUserName;
    }
    public String getNewUserName(){
        return this.newUserName;
    }

    public void setNewEmail(String newEmail)
    {
        this.newEmail=newEmail;
    }
    public String getNewEmail()
    {
        return this.newEmail;
    }

    public void setNewPassword(String newPassword){ this.newPassword=newPassword;}

    public void setNewPasswordRepeat(String newPasswordRepeat){ this.newPasswordRepeat=newPasswordRepeat;}

}
