package com.javaspring.myproject.beans;

public class UserChangePW {
    private String email;
    private String oldPassword;
    private String newPassword;
    private String newPasswordRepeat;

    UserChangePW(String email,String oldPassword,String newPassword,String newPasswordRepeat){
        this.email=email;
        this.newPassword=newPassword;
        this.newPasswordRepeat=newPasswordRepeat;
        this.oldPassword=oldPassword;
    }

    public String getEmail(){ return email;}
    public String getOldPassword(){ return oldPassword;}
    public String getNewPassword(){ return newPassword;}
    public String getNewPasswordRepeat(){ return newPasswordRepeat;}
    public void setEmail(String email){ this.email=email;}
    public void setOldPassword(String oldPassword){ this.oldPassword=oldPassword;}
    public void setNewPassword(String newPassword){ this.newPassword=newPassword;}
    public void setNewPasswordRepeat(String newPasswordRepeat){ this.newPasswordRepeat=newPasswordRepeat;}

}