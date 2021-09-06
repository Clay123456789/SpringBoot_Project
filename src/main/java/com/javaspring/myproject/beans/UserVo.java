package com.javaspring.myproject.beans;

//专用于接受表单提交的user对象，包含各种接口需要的专用属性
public class UserVo {
    private String username;
    private String password;
    private String email;
    private String tel;
    private String touxiang;
    private String qianming;
    private String age;
    private String sex;
    //    验证码
    private String code;


    //新信息
    private String newUsername;
    private String newEmail;
    private String newTel;
    private String newPassword;
    private String newTouxiang;
    private String newQianming;
    private String newAge;
    private String newSex;
    private String newPasswordRepeat;
    public UserVo() {
    }

    public UserVo(String username, String password, String email, String code) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.code = code;
    }
    public void setPassword(String password)
    {
        this.password=password;
    }
    public void setUsername(String username)
    {
        this.username=username;
    }
    public void setEmail(String email)
    {
        this.email=email;
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
    public void setCode(String code) {
        this.code = code;
    }

    public String getUsername()
    {
        return username;
    }
    public String getPassword()
    {
        return password;
    }
    public String getEmail()
    {
        return email;
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
    public String getCode() {
        return code;
    }

   
    public String getNewPasswordRepeat(){ return newPasswordRepeat;}
  

    public void setNewUsername(String newusername)
    {
        this.newUsername =newusername;
    }
    public void setNewEmail(String newemail)
    {
        this.newEmail =newemail;
    }
    public void setNewTel(String newtel)
    {
        this.newTel=newtel;
    }
    public void setNewTouxiang(String newtouxiang)
    {
        this.newTouxiang =newtouxiang;
    }
    public void setNewQianming(String newqianming)
    {
        this.newQianming =newqianming;
    }
    public void setNewAge(String newage)
    {
        this.newAge =newage;
    }
    public void setNewSex(String newsex)
    {
        this.newSex =newsex;
    }

    public void setNewPassword(String newPassword){ this.newPassword=newPassword;}
  
    public void setNewPasswordRepeat(String newPasswordRepeat){ this.newPasswordRepeat=newPasswordRepeat;}
  
    public String getNewUsername()
    {
        return newUsername;
    }
    public String getNewPassword()
    {
        return newPassword;
    }
    public String getNewEmail()
    {
        return newEmail;
    }
    public String getNewTel()
    {
        return newTel;
    }
    public String getNewTouxiang()
    {
        return newTouxiang;
    }
    public String getNewQianming()
    {
        return newQianming;
    }
    public String getNewAge()
    {
        return newAge;
    }
    public String getNewSex()
    {
        return newSex;
    }


}
