package com.javaspring.myproject.service;

import com.javaspring.myproject.beans.UserVo;

import javax.servlet.http.HttpSession;

public interface IEMailService {

    /**
     * 给前端输入的邮箱，发送验证码
     */
    public boolean sendMimeMail(String email, HttpSession session);

    /**
     * 随机生成6位数的验证码
     */
    public String randomCode();

    /**
     * 检验验证码是否一致
     */
    public boolean registered(UserVo userVo);

    public boolean findPassword_sendEmail(String email);

    public boolean changePassword(String email, String oldPassword, String newPassword, String newPasswordRepeat);
}